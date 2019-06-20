package com.example.missitchat;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaledrone.lib.Listener;
import com.scaledrone.lib.Room;
import com.scaledrone.lib.RoomListener;
import com.scaledrone.lib.Scaledrone;

public class MainActivity extends AppCompatActivity implements RoomListener {

    private String channelId = "4eYUnWVfbNL9PqLe";
    private String roomName = "observable-myRoom";
    private EditText messageEdit;
    private Scaledrone scaledrone;
    private RecyclerViewAdapter messageAdapter;
    private RecyclerView messagesView;

    // setup

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        messageEdit = (EditText) findViewById(R.id.messageEdit);

        messagesView = (RecyclerView) findViewById(R.id.messagesView);
        messageAdapter = new RecyclerViewAdapter(this);
        messagesView.setAdapter(messageAdapter);
        messagesView.setLayoutManager(new LinearLayoutManager(this));

        MemberData data = new MemberData(MemberData.generateRandomName(), MemberData.generateRandomColor());

        Listener webSocketListener = new Listener() {
            @Override
            public void onOpen() {
                System.out.println("Scaledrone connected successfully");
                toast("Scaledrone Client Connected");
                scaledrone.subscribe(roomName, MainActivity.this);
            }

            @Override
            public void onOpenFailure(Exception ex) {
                System.out.println("scaledrone:onOpenFailure" + ex);
            }

            @Override
            public void onFailure(Exception ex) {
                System.out.println("scaledrone:onFailure" + ex);
                toast("Scaledrone Client Failure, Attempting to Reconnect");
                scaledrone.close();
                scaledrone.connect(this);
            }

            @Override
            public void onClosed(String reason) {
                System.out.println("scaledrone:onClosed" + reason);
            }
        };

        // connecting to Scaledrone

        scaledrone = new Scaledrone(channelId, data);
        scaledrone.connect(webSocketListener);
    }

    // setup of toolbar

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // implements Scaledrone RoomListener

    @Override
    public void onOpen(Room room) {
        System.out.println("Connected to Scaledrone room");
        toast("Scaledrone Room Connected");
    }

    @Override
    public void onOpenFailure(Room room, Exception e) {
        System.err.println("scaledrone.room:onOpenFailure" + e);
        toast("Scaledrone Room Failure");
    }

    @Override
    public void onMessage(Room room, com.scaledrone.lib.Message message) {
        final ObjectMapper mapper = new ObjectMapper();

        try {
            Log.d("onMessage: ", message.toString());

            final MemberData data = mapper.treeToValue(message.getMember().getClientData(), MemberData.class); // null pointer exception error
            boolean received = !message.getClientID().equals(scaledrone.getClientID());
            final Message receivedMessage = new Message(message.getData().asText(), data, received);

            // display latest message

            TextView latestMessage = (TextView) findViewById(R.id.latestMessage);
            latestMessage.setText(receivedMessage.getMessageBody());

            if(received) {
                latestMessage.setBackgroundColor(Color.MAGENTA);
            } else {
                latestMessage.setBackgroundColor(Color.BLUE);
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    messageAdapter.addMessage(receivedMessage);
                    messagesView.scrollToPosition(messageAdapter.getItemCount() - 1);
                }
            });
        } catch (JsonProcessingException e) {
            System.err.println("JsonProcessingException: ");
            e.printStackTrace();
        }




    }

    public void sendMessgaqe(View view) {
        String message = messageEdit.getText().toString();
        if(message.length() > 0) {
            scaledrone.publish(roomName, message);
            messageEdit.getText().clear();
        }
    }

    public void toast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
