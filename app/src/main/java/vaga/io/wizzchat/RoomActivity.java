package vaga.io.wizzchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import vaga.io.wizzchat.adapters.ContactAdapter;
import vaga.io.wizzchat.adapters.MessageAdapter;

public class RoomActivity extends AppCompatActivity {

    private ListView _messagesListView;
    private EditText _contentEditText;
    private MessageAdapter _messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Room");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        _messagesListView = (ListView) findViewById(R.id.messagesListView);
        _contentEditText = (EditText) findViewById(R.id.contentEditText);

        _messageAdapter = new MessageAdapter(this, getLayoutInflater());

        _messageAdapter.addMessage("Bienvenue");

        _messagesListView.setAdapter(_messageAdapter);
    }

    public void onSend(View view) {

        String content = _contentEditText.getText().toString();

        if (content.length() > 0) {
            _messageAdapter.addMessage(content);
        }
    }
}
