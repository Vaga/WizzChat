package vaga.io.wizzchat;

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
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import io.realm.Realm;
import io.realm.RealmResults;
import vaga.io.wizzchat.adapters.ContactAdapter;
import vaga.io.wizzchat.adapters.MessageAdapter;
import vaga.io.wizzchat.api.RestClient;
import vaga.io.wizzchat.models.Contact;
import vaga.io.wizzchat.models.Profile;

public class RoomActivity extends AppCompatActivity {

    private static final String TAG = "RoomActivity";

    private ListView _messagesListView;
    private EditText _contentEditText;
    private Button _sendButton;
    private MessageAdapter _messageAdapter;
    private Profile _from;
    private Contact _to;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        // Retrieve email
        Bundle extras = getIntent().getExtras();
        String email = extras.getString("email", "");
        if (email.length() == 0)
            finish();

        Realm realm = Realm.getInstance(this.getApplicationContext());
        _from = realm.where(Profile.class).findFirst();
        _to = realm.where(Contact.class).equalTo("email", email).findFirst();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.title_room) + " " + _to.getName());
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        _messagesListView = (ListView) findViewById(R.id.messagesListView);
        _contentEditText = (EditText) findViewById(R.id.contentEditText);
        _sendButton = (Button) findViewById(R.id.sendButton);

        _messageAdapter = new MessageAdapter(this, getLayoutInflater());

        RestClient.getMessages(_to.getPublicKey(), _from.getPublicKey(), new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                Log.d(TAG, "GetMessages Array : " + response.toString());

                for (int i = response.length() - 1; i >= 0; i--) {
                    try {
                        JSONObject message = response.getJSONObject(i);
                        RoomActivity.this._messageAdapter.addMessage(message.getString("data"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
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

            _messageAdapter.addMessage(content);

            RestClient.postMessage(_from.getPublicKey(), _to.getPublicKey(), content, new JsonHttpResponseHandler() {

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
}
