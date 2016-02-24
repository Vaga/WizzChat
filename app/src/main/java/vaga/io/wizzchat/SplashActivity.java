package vaga.io.wizzchat;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import io.realm.Realm;
import vaga.io.wizzchat.models.Profile;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    private final int SPLASH_DISPLAY_LENGTH = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                Realm realm = Realm.getInstance(SplashActivity.this.getApplicationContext());
                Intent intent;

                // Retrieve the profile
                Profile profile = realm.where(Profile.class).findFirst();
                if (profile == null) {
                    // Redirect to LoginActivity
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                }
                else {
                    // Redirect to ListActivity
                    intent = new Intent(SplashActivity.this, ListActivity.class);
                }
                SplashActivity.this.startActivity(intent);
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
