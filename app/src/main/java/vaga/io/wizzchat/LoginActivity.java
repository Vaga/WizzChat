package vaga.io.wizzchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import io.realm.Realm;
import vaga.io.wizzchat.models.Profile;
import vaga.io.wizzchat.services.RegistrationIntentService;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText _nameEditText;
    private EditText _emailEditText;
    private Button _registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        _nameEditText = (EditText) findViewById(R.id.nameEditText);
        _emailEditText = (EditText) findViewById(R.id.emailEditText);
        _registerButton = (Button) findViewById(R.id.registerButton);
    }

    public void onRegister(View view) {

        // Disable the register button
        _registerButton.setEnabled(false);

        // The form is not valid
        if (!isValid()) {
            Toast.makeText(getBaseContext(), getResources().getString(R.string.register_failed), Toast.LENGTH_LONG).show();
            _registerButton.setEnabled(true);
            return;
        }

        // Create a progress dialog
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppDialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getResources().getString(R.string.registering));
        progressDialog.show();

        new Handler().postDelayed(
                new Runnable() {
                    public void run() {

                        // Create the profile
                        if (!onSuccess()) {
                            Toast.makeText(getBaseContext(), getResources().getString(R.string.internal_error), Toast.LENGTH_LONG).show();
                            // Hide the progress dialog
                            progressDialog.dismiss();
                            return;
                        }

                        Intent service = new Intent(LoginActivity.this, RegistrationIntentService.class);
                        startService(service);

                        // Hide the progress dialog
                        progressDialog.dismiss();

                        // Go to ListActivity
                        Intent intent = new Intent(LoginActivity.this, ListActivity.class);
                        LoginActivity.this.startActivity(intent);
                        LoginActivity.this.finish();
                    }
                }, 3000);
    }

    private boolean onSuccess() {

        // Retrieve informations
        String name = _nameEditText.getText().toString();
        String email = _emailEditText.getText().toString();

        Realm realm = Realm.getInstance(LoginActivity.this.getApplicationContext());

        realm.beginTransaction();

        // Set the profile
        Profile profile = realm.createObject(Profile.class);
        profile.setName(name);
        profile.setEmail(email);

        // Generate the private and public key
        boolean done = generateKeyPair(profile);

        // Save the profile
        if (done)
            realm.commitTransaction();
        else
            realm.cancelTransaction();

        _registerButton.setEnabled(true);

        return done;
    }

    private boolean isValid() {

        boolean valid = true;

        String name = _nameEditText.getText().toString();
        String email = _emailEditText.getText().toString();

        // Name
        if (name.isEmpty() || name.length() < 4) {
            _nameEditText.setError(getResources().getString(R.string.error_name));
            valid = false;
        } else {
            _nameEditText.setError(null);
        }

        // Email
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailEditText.setError(getResources().getString(R.string.error_email));
            valid = false;
        } else {
            _emailEditText.setError(null);
        }

        return valid;
    }

    private boolean generateKeyPair(Profile profile) {

        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            KeyPair keypair = kpg.genKeyPair();

            PrivateKey privateKey = keypair.getPrivate();
            PublicKey publicKey = keypair.getPublic();

            profile.setPublicKey(publicKey.toString());
            profile.setPrivateKey(privateKey.toString());
        }
        catch(NoSuchAlgorithmException e) {
            Log.d(TAG, "Can't find algorithm", e);
            return false;
        }
        return true;
    }
}
