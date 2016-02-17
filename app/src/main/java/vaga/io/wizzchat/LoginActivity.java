package vaga.io.wizzchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText _nameEditText;
    private EditText _emailEditText;
    private EditText _phoneEditText;
    private Button _registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        _nameEditText = (EditText) findViewById(R.id.nameEditText);
        _emailEditText = (EditText) findViewById(R.id.emailEditText);
        _phoneEditText = (EditText) findViewById(R.id.phoneEditText);

        _registerButton = (Button) findViewById(R.id.registerButton);
    }

    public void onRegister(View view) {

        _registerButton.setEnabled(false);

        if (!isValid()) {
            Log.d(TAG, "No ! :(");
        } else {
            generateKeyPair();
            Log.d(TAG, "Yes ! :)");
        }

        _registerButton.setEnabled(true);
    }

    private boolean isValid() {

        boolean valid = true;

        String name = _nameEditText.getText().toString();
        String email = _emailEditText.getText().toString();
        String phone = _phoneEditText.getText().toString();

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

        // Phone
        if (phone.isEmpty() || !Patterns.PHONE.matcher(phone).matches()) {
            _phoneEditText.setError("Enter a valid phone number");
            valid = false;
        } else {
            _phoneEditText.setError(null);
        }

        return valid;
    }

    private boolean generateKeyPair() {

        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            KeyPair keypair = kpg.genKeyPair();

            PrivateKey privateKey = keypair.getPrivate();
            PublicKey publicKey = keypair.getPublic();

            Log.d(TAG, "Public key : " + publicKey.toString());
            Log.d(TAG, "Private key : " + privateKey.toString());
        }
        catch(NoSuchAlgorithmException e) {
            Log.d(TAG, "Can't find algorithm", e);
            return false;
        }
        return true;
    }
}
