package com.example.missitchat;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaledrone.lib.Listener;
import com.scaledrone.lib.Room;
import com.scaledrone.lib.RoomListener;
import com.scaledrone.lib.Scaledrone;

public class MainActivity extends AppCompatActivity implements RoomListener {

    private String channelId = "4eYUnWVfbNL9PqLe";
    private String roomName = "observable-room";
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

        // connecting to Scaledrone

        scaledrone = new Scaledrone(channelId, data);
        scaledrone.connect(new Listener() {
            @Override
            public void onOpen() {
                System.out.println("Scaledrone connected successfully");
                scaledrone.subscribe(roomName, MainActivity.this);
            }

            @Override
            public void onOpenFailure(Exception ex) {
                System.out.println(ex);
            }

            @Override
            public void onFailure(Exception ex) {
                System.out.println(ex);
            }

            @Override
            public void onClosed(String reason) {
                System.out.println(reason);
            }
        });
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
    }

    @Override
    public void onOpenFailure(Room room, Exception e) {
        System.err.println(e);
    }

    @Override
    public void onMessage(Room room, com.scaledrone.lib.Message message) {
        final ObjectMapper mapper = new ObjectMapper();

        try {
            final MemberData data = mapper.treeToValue(message.getMember().getClientData(), MemberData.class);
            boolean received = !message.getClientID().equals(scaledrone.getClientID());
            final Message receivedMessage = new Message(message.getData().asText(), data, received);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    messageAdapter.addMessage(receivedMessage);
                    messagesView.scrollToPosition(messageAdapter.getItemCount() - 1);
                }
            });
        } catch (JsonProcessingException e) {
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
}
