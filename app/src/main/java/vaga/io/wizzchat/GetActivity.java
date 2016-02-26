package vaga.io.wizzchat;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import io.realm.Realm;
import vaga.io.wizzchat.models.Profile;

public class GetActivity extends AppCompatActivity {

    private TextView _nameTextView;
    private TextView _emailTextView;
    private ImageView _qrcodeImageView;

    private Profile _profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get);

        _nameTextView = (TextView) findViewById(R.id.nameTextView);
        _emailTextView = (TextView) findViewById(R.id.emailTextView);
        _qrcodeImageView = (ImageView) findViewById(R.id.qrcodeImageView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("My identity");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Realm realm = Realm.getInstance(GetActivity.this.getApplicationContext());

        // Get the profile
        _profile = realm.where(Profile.class).findFirst();
        if (_profile == null) {
            Toast.makeText(getBaseContext(), "Internal error", Toast.LENGTH_LONG).show();
            return;
        }

        // Hydrate the view
        _nameTextView.setText(_profile.getName());
        _emailTextView.setText(_profile.getEmail());

        drawQRCode(_profile);
    }

    public void onPastePublicKey(View view) {

        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("public_key", _profile.getPublicKey());
        clipboard.setPrimaryClip(clip);

        Toast.makeText(getBaseContext(), "Public key copied", Toast.LENGTH_LONG).show();
    }

    // TODO : Better way to retrieve the QRCode
    private void drawQRCode(Profile profile) {

        String url = "https://api.qrserver.com/v1/create-qr-code/?size=800x800&data=";
        String query = "{\"name\":\"" + profile.getName() + "\",";
        query += "\"email\":\"" + profile.getEmail() + "\",";
        query += "\"public_key\":\"" + profile.getPublicKey() + "\"}";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            URL urlConnection = new URL(url + URLEncoder.encode(query, "utf-8"));
            HttpURLConnection connection = (HttpURLConnection) urlConnection.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);

            _qrcodeImageView.setImageBitmap(myBitmap);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
