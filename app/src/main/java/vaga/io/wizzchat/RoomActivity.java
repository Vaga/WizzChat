package vaga.io.wizzchat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;
import io.realm.Realm;
import io.realm.RealmResults;
import vaga.io.wizzchat.adapters.MessageAdapter;
import vaga.io.wizzchat.api.RestClient;
import vaga.io.wizzchat.models.Contact;
import vaga.io.wizzchat.models.Message;
import vaga.io.wizzchat.models.Profile;
import vaga.io.wizzchat.utils.RSA;

public class RoomActivity extends AppCompatActivity {

    private static final String TAG = "RoomActivity";
    public static final String NOTIFY_REFRESH_THREAD = "REFRESH_THREAD";

    private ListView _messagesListView;
    private EditText _contentEditText;
    private Button _sendButton;
    private MessageAdapter _messageAdapter;
    private Profile _from;
    private Contact _to;

    private BroadcastReceiver _broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        // Retrieve email
        Bundle extras = getIntent().getExtras();
        String id = extras.getString("id", "");
        if (id.length() == 0)
            finish();

        _broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                Bundle bundle = intent.getExtras();
                if (bundle == null)
                    return;

                String author = bundle.getString("author");
                String content = bundle.getString("message");
                String date = bundle.getString("date");

                if (author.equals(RoomActivity.this._to.getId())) {

                    try {

                        String decryptedContent = RSA.decrypt(RoomActivity.this._from.getPrivateKey(), content);

                        Message m = new Message();
                        m.setContent(decryptedContent);
                        m.setDate(date);
                        m.setTo("");
                        RoomActivity.this._messageAdapter.addMessage(m);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(NOTIFY_REFRESH_THREAD);
        registerReceiver(_broadcastReceiver, filter);

        Realm realm = Realm.getInstance(this.getApplicationContext());
        _from = realm.where(Profile.class).findFirst();
        _to = realm.where(Contact.class).equalTo("id", id).findFirst();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.title_room) + " " + _to.getName());
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        _messagesListView = (ListView) findViewById(R.id.messagesListView);
        _messagesListView.setEmptyView(findViewById(R.id.emptyStateViewStub));
        _contentEditText = (EditText) findViewById(R.id.contentEditText);
        _sendButton = (Button) findViewById(R.id.sendButton);

        _messageAdapter = new MessageAdapter(this, getLayoutInflater());

        RealmResults<Message> messages = realm.where(Message.class).equalTo("to", id).findAll();
        for (Message message : messages) {
            _messageAdapter.addMessage(message);
        }

        RestClient.getMessages(_to.getId(), _from.getId(), new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                Log.d(TAG, "GetMessages Array : " + response.toString());

                for (int i = response.length() - 1; i >= 0; i--) {

                    try {

                        JSONObject message = response.getJSONObject(i);
                        String decryptedContent = RSA.decrypt(RoomActivity.this._from.getPrivateKey(), message.getString("data"));

                        Message m = new Message();
                        m.setContent(decryptedContent);
                        m.setDate(message.getString("at"));
                        m.setTo("");
                        RoomActivity.this._messageAdapter.addMessage(m);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                RoomActivity.this._messageAdapter.sort();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Log.d(TAG, "GetMessages Object : " + response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String data, Throwable e) {

                Log.d(TAG, "Status code : " + statusCode);
                Log.d(TAG, "Data : " + data);
            }
        });

        _messagesListView.setAdapter(_messageAdapter);
    }



    public void onSend(View view) {

        String content = _contentEditText.getText().toString();

        if (content.length() > 0) {

            _sendButton.setEnabled(false);

            Realm realm = Realm.getInstance(this.getApplicationContext());

            realm.beginTransaction();

            // Add a message
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            Message message = realm.createObject(Message.class);
            message.setContent(content);
            message.setDate(dateFormat.format(new Date()));
            message.setTo(_to.getId());

            realm.commitTransaction();

            _messageAdapter.addMessage(message);

            String encryptedContent;

            try {
                encryptedContent = RSA.encrypt(_to.getPublicKey(), content);
            } catch(Exception e) {
                //Error
                return;
            }

            RestClient.postMessage(_from.getId(), _to.getId(), encryptedContent, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    Log.d(TAG, "PostMessage Object : " + response.toString());

                    RoomActivity.this._sendButton.setEnabled(true);
                    RoomActivity.this._contentEditText.setText("");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String data, Throwable e) {

                    Log.d(TAG, "Status code : " + statusCode);
                    Log.d(TAG, "Data : " + data);

                    RoomActivity.this._sendButton.setEnabled(true);
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.internal_error), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject data) {

                    Log.d(TAG, "Status code : " + statusCode);
                    Log.d(TAG, "Data : " + data);

                    RoomActivity.this._sendButton.setEnabled(true);
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.internal_error), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    protected void onPause() {

        super.onPause();
        unregisterReceiver(_broadcastReceiver);
    }
}
