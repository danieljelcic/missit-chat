**Team**

Dr. Fahad Dogar / Research Project Lead    
Daniel Jelcic / Developer and Research Assistant


#MissIt

MissIt is a communication service that uses missed calls to transmit messages between phones. MissIt encodes data by modulating the duration of missed calls. Using missed calls to transmit data for short text messaging in place of protocols such as SMS, RTT or IM is in the interest of resource-constrained users in developing regions where digital communication is necessary, costs of traditional message protocols is too high and high latency in exchange for low or no cost is tolerable.Essential to MissIt is its user asymmetry in that it is the most useful in interaction between one resource-restricted (RR) and one resource-unrestricted user (RUR).

MissIt and the technology behind it is a product of research at the NAT research lab at Tufts University led by Prof. Fahad Dogar. The project in this repository uses none of the MissIt technology and only the concept behind it.


#MissIt Chat

MissIt Chat is a messaging app for Android which, besides regular text messaging (like SMS), enables RR users to respond to messages received from a RUR user by sending one of 4 responses which the RUR user defined. A RUR user, in anticipation of the other user being RR, is responsible for entering at least 2 and up to 4 suggested responses, which are transmitted to the RR user via a traditional text messaging channel. The RR user responds by selecting one of the suggested responses, which triggers a MissIt-like transmission of a missed call the length of which corresponds to the selected response.

MissIt Chat does not implement the MissIt interface, but instead emulates the experience of a MissIt message exchange through the interface including suggested response bubbles, progress bars etc., and the underlying channels: SMS for RUR users and missed phone calls for RR users through a Firebase database protocol which requires an internet connection.

The app is developed with the Android SDK, requiring the minimum SDK version 16 (Jelly Bean). Compile with Gradle (`app/build.gradle`).   
A precompiled APK of version 0.3 is available in `app/build/outputs/apk/com.example.missitchat.v0.3.apk`.    
Mockups can be found on Figma for the [RR user interface](https://www.figma.com/file/pBLc5BceUBaZF0TGcJA2yc/MissIt-Chat-Mockups-Restricted?node-id=0%3A1) and the [RUR user interface](https://www.figma.com/file/BATxx8RH6Gz726dWM2LZ8soS/MissIt-Chat-Mockups-Unrestricted).


#Message Types

MissIt Chat defines 2 types of messages: regular, and MissIt messages.

Regular messages are analogous to SMS messages and are only defined by the message body, timestamp and sender.

MissIt messages contain a regular message but contain a set of MissIt Suggested Response data. Regular messages become Missit messages as soon and only when MissIt Suggested Response data is added to it before sending. Only a RUR user can send MissIt messages, and a RR user can only respond to MissIt messages.

A MissIt message is “unanswered” when no MissIt response has been attempted by the user who received the message or the transmission failed. They are defined by status codes 2 = RESPONSE_NOT_ATTEMPTED and 3 = RESPONSE_FAILED.

A MissIt message is “answered” when a MissIt response has been attempted and the transmission of it is in one of three states defined by following status codes: 4 = RESPONSE_ESTABLISHING, 1 = RESPONSE_TRANSMITTING and 0 = RESPONSE_SUCCESS.

All of the above types can be sent or received. That defines 6 message types in total: regular sent, regular received, MissIt sent unanswered, MissIt received unanswered, MissIt sent answered and MissIt received answered.


#Authentication

Users can register with a username, email and a password, and can log in with an email and a password which have previously been registered with a username. All authentication is done through Firebase Auth. Usernames are used to identify users in the interface, start new conversations and are analogous to phone numbers in an SMS or phone call protocol. User IDs (uids) are generated by Firebase Auth and are used to identify users in the database.


#Database

All of the MissIt server exchange is implemented through the Firebase realtime database. In the current implementation, no data is stored on user’s device and is only written to and read from on the cloud. The noSQL structure of database nodes is as follows:

Users[] :  
	uid :  
		username : {string}  
		conversation-list[] :  
			username : {timestamp long}  
Usernames[] :  
	username : user_id  
Conversations[] :  
	uid :  
		other_uid :  
			timestamp :  
				body : {string}  
				is_received : {boolean}  
				missit : {null by default}  
					suggestion_1 : {string}  
					suggestion_2 : {string}  
					suggestion_3 : {string}  
					suggestion_4 : {string}  
					res-code : {int, n for responded with suggestion_n+1, -1 for no response}  
					status-code : {int, status codes as defined in the MissitSuggestions class}  
					res-timestamp : {timestamp long}   

Nodes referred to as `node_name[]` are lists, and `{type}` mark types for key values.

The Users node is used to map the Firebase UID attained at login to a username and a list of user’s conversations used to bind the ConversationList activity. The Usernames node is used to map usernames to Firebase UIDs when opening a new or existing conversation in the Conversation Activity. The Conversations node contains a list of all the messages exchanged between all the users and all the other users they’ve exchanged messages with. There are two instances of each conversation in the database, i.e. between user A and user B under `Conversations/{A’s UID}/{B’s UID}` and `Conversations/{B’s UID}/{A’s UID}`. Conversation nodes contain all the information about all the messages, including timestamp, body, Missit suggestions and responses.


#Activities


##ConversationListActivity

ConversationList is the launcher activity. If a user is already logged in (there is a Firebase Auth instance), it is the first thing the user sees. The user can log out, start a new conversation or click on any of the existing conversation from the list to open the Conversation activity with the user whose conversation list item was clicked on.

The list of conversations is retrieved from the `Users/uid/conversation-list` and there is a data change listener which updates the list whenever there is a change to the node on the server. The view is managed by the ConversationViewAdapter which is fed the complete list of conversations every time there is a change in the node..

If there is no Firebase Auth instance upon opening the activity, ConversationList starts an intent to the Welcome activity.


##WelcomeActivity

The Welcome activity is only ever launched from the ConversationList activity if no user is currently logged in. It contains buttons that start intents to the Register and Login activities


##RegisterActivity

The Register activity contains a form where a user can register an account with a username, email and password. The username and email need to be unique. Errors with Firebase Auth and Database are handled with toasts. Clicking the register button registers the user, logs them in (creates a local instance of Firebase Auth) and opens the ConversationList activity.


##LoginActivity

The Login activity contains a form where a user can log into their registered account with their email and password. Errors with Firebase Auth are handled with toasts. Clicking the login button logs them in (creates a local instance of Firebase Auth) and opens the ConversationList activity.


##ConversationActivity

The Conversation activity is started by clicking on a conversation item in the ConversationList activity which starts a new intent to the Conversation activity containing the username of the other user participating in the conversation. The list of messages is retrieved from the `Conversations/uid/other_uid` node and there is a data change listener which updates the list whenever there is a change to the node on the server. The view is managed by the MessageViewAdapter which is fed the complete list of conversations every time there is a change in the node. MessageViewAdapter is also responsible for determining the type of message and displaying the proper layout accordingly.

A TextEdit field at the bottom allows the user to type a regular message to be sent. Pressing the send button on the right will send the message as a regular message by adding it to the `Conversations/uid/other_uid` and then `Conversations/other_uid`. The message is only displayed once it’s retrieved from the database with the data change listener on the former node. Sending a message also updates both user’s `Users/uid/conversation_list` node, changing the timestamp of the latest message.

A RUR user can turn the typed in message (before sending it) into a MissIt message by clicking on the MissIt button on the left of the message input field, which opens the SuggestionEditDialogFragment. The Conversation activity implements a SuggestionEditListener interface from the SuggestionEditDialogFragment class which handles suggestions that have been entered in the dialog. If the activity receives an empty list of suggestions, the message currently being edited stays a regular message. If it receives a non-empty list of suggestions, the message has become a MissIt message, indicated with a change of color around the message input region. Clicking send in this state will send a MissIt message, which is at first always of the sent unanswered type.

The Conversation activity is also responsible for resolving received MissIt messages. Clicking on a send button next to a received suggested response causes the SuggestionViewAdapter to call the OnSuggestedResponseSendClick method from the SuggestedResponseSendListener interface implemented in the MessageViewAdapter, which then calls the OnSendMissitResponse method from the MissItResponseListener interface implemented in the Conversation activity. A MissIt response is sent with a series of updates to the database that emulate the process of establishing a call. The protocol for sending a response to a received unanswered Missit message is the following (each step is called in the previous steps on complete listener and all the nodes being set belong to the message being responded to):

1. Set RR’s response code to the number corresponding to the chosen suggested response
2. Set RR’s status code to RESPONSE_ESTABLISHING
3. Set RUR’s status code to RESPONSE_TRANSMITTING
4. Set RR’s status code to RESPONSE_TRANSMITTING
5. Emulate the delay and then set RUR’s response code to the number corresponding to the chosen suggested response
6. Set RR’s timestamp
7. Set RR’s status code to RESPONSE_SUCCESS
8. Set RUR’s timestamp
9. Set RUR’s status code to RESPONSE_SUCCESS

Answered MissIt messages are not stored as separate messages like regular messages, both in the activity and the database, but are rather stored as response codes to the unanswered MissIt messages which can be used to retrieve the response text from the list of suggestions.


#Dialog Fragments


##NewConversationDialogFragment

The NewConversation dialog is used to establish a conversation with a user the current user has not exchanged any messages with. Errors with invalid usernames are displayed as errors of the input field. Entering a username with which a conversation already exists opens that conversation in the Conversation activity as it would happen by clicking on a conversation list item. Entering a valid username with which a conversation doesn’t exists opens the Conversation activity with the entered username in the intent and doesn’t add the conversation to any of the nodes in the database - sending the first message does that.


##SuggestionEditDialogFragment

The SuggestionEdit fragment displays the current message and any suggested responses generated by the activity that called it or saved by the activity that called if from previously opening and closing the fragment. A user can then add, remove and edit up to 4 suggested responses to the message they are sending. User input is saved whenever the input field being edited loses focus, which happens whenever a different field is clicked, any buttons are pressed or the dialog is dismissed. Upon dismissing or pressing the close button, the dialog calls the OnClose method from the SuggestionEditListener interface implemented by the Conversation activity, and passes in all the suggestions recorded as of the event.


#RecyclerView Adapters

RecyclerView Adapters all extend the RecyclerView.Adapter<ViewHolder> class used to maintain a list of views with bound data in a recycler view. All of them also define a class extending RecyclerView.ViewHolder, which are views holding each of the list items.


##ConversationViewAdapter

The adapter maintains a rendered list of conversation list items corresponding to conversations the current user has with other users. It uses an ArrayList of Conversation objects as the underlying data structure.

##MessageViewAdapter

The adapter maintains the list of message views corresponding to messages exchanged between two users. The adapter is responsible for determining the message type (as described in Message Types) and inflating a corresponding layout. There are 6 message view layouts corresponding to 6 message types, and 6 MessageHolder final child classes corresponding to the 6 message types (with some other non final child classes in between). The adapter is also responsible for managing the display of indeterminate progress bars for sent answered MissIt messages being transmitted and determinate progress bars and text for received answered MissIt messages. The MessageViewHolders extending the MissitUnansweredMessageHolder class also use a SuggestionViewAdapter to maintain a list of suggested responses for the unanswered MissIt message they’re holding, and implement the SuggestedResponseSendListener interface for relaying outgoing MissIt responses. It uses an ArrayList of Message objects as the underlying data structure.


##SuggestionEditViewAdapter

The adapter maintains a list of text input fields to be populated with suggested responses to a message. Each text input field has a corresponding clear button the click upon which the text input field is removed. It uses an ArrayList of Suggestion objects as the underlying data structure.

##SuggestionViewAdapter

The adapter maintains a list of text views corresponding to suggested responses to an unanswered MissIt message. For sent messages, each view only displays information about the sent suggested response. For received messages, each view has a send button revealed upon clicking on the suggested response body which is used to trigger a MissIt transmission. It uses a MissItSuggestions object as the underlying data structure.
