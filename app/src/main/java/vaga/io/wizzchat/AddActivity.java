package vaga.io.wizzchat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import io.realm.Realm;
import vaga.io.wizzchat.models.Contact;

public class AddActivity extends AppCompatActivity {

    private static final String TAG = "AddActivity";

    private EditText _nameEditText;
    private EditText _emailEditText;
    private EditText _publicKeyEditText;
    private Button _addButton;
    private Button _scanButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        _nameEditText = (EditText) findViewById(R.id.nameEditText);
        _emailEditText = (EditText) findViewById(R.id.emailEditText);
        _publicKeyEditText = (EditText) findViewById(R.id.publicKeyEditText);
        _addButton = (Button) findViewById(R.id.addButton);
        _scanButton = (Button) findViewById(R.id.scanButton);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("New contact");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void onScan(View view) {

        try {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            showDialog(AddActivity.this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }
    }

    public void onAdd(View view) {

        _addButton.setEnabled(false);
        _scanButton.setEnabled(false);

        // The form is not valid
        if (!isValid()) {
            Toast.makeText(getBaseContext(), "Add failed", Toast.LENGTH_LONG).show();
            _addButton.setEnabled(true);
            _scanButton.setEnabled(true);
            return;
        }

        // Create a progress dialog
        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppDialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Adding...");
        progressDialog.show();

        new Handler().postDelayed(
                new Runnable() {
                    public void run() {

                        // Create the profile
                        if (!onSuccess()) {
                            Toast.makeText(getBaseContext(), "Internal error", Toast.LENGTH_LONG).show();
                            // Hide the progress dialog
                            progressDialog.dismiss();
                            return;
                        }

                        // Hide the progress dialog
                        progressDialog.dismiss();

                        AddActivity.this.finish();
                    }
                }, 2000);
    }

    public boolean onSuccess() {

        Realm realm = Realm.getInstance(getApplicationContext());

        realm.beginTransaction();

        // Set the profile
        Contact contact = realm.createObject(Contact.class);
        contact.setName(_nameEditText.getText().toString());
        contact.setEmail(_emailEditText.getText().toString());
        contact.setPublicKey(_publicKeyEditText.getText().toString());

        realm.commitTransaction();

        return true;
    }

    public boolean isValid() {

        boolean valid = true;

        String name = _nameEditText.getText().toString();
        String email = _emailEditText.getText().toString();
        String publicKey = _publicKeyEditText.getText().toString();

        // Name
        if (name.isEmpty() || name.length() < 4) {
            _nameEditText.setError("At least 4 characters");
            valid = false;
        } else {
            _nameEditText.setError(null);
        }

        // Email
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailEditText.setError("Enter a valid email address");
            valid = false;
        } else {
            _emailEditText.setError(null);
        }

        // Public key
        if (publicKey.isEmpty() || publicKey.length() < 4) {
            _publicKeyEditText.setError("At least 4 characters");
            valid = false;
        } else {
            _publicKeyEditText.setError(null);
        }

        return valid;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == 0) {

            if (resultCode == RESULT_OK) {

                String content = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");

                try {
                    JSONObject contact = new JSONObject(content);

                    _nameEditText.setText(contact.getString("name"));
                    _emailEditText.setText(contact.getString("email"));
                    _publicKeyEditText.setText(contact.getString("public_key"));
                } catch(Exception e) {
                    Toast.makeText(getBaseContext(), "It's not a JSON format...", Toast.LENGTH_LONG).show();
                }

            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            }
        }
    }

    private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {

        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);

        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialogInterface, int i) {

                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);

                try {
                    act.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {}
            }
        });

        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {}
        });

        return downloadDialog.show();
    }
}
