package vaga.io.wizzchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class ListActivity extends AppCompatActivity {

    private static final String TAG = "ListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent;

        switch (item.getItemId()) {
            // Add a contact
            case R.id.new_contact:
                Log.d(TAG, "New contact");
                intent = new Intent(this, AddActivity.class);
                startActivity(intent);
                return true;
            // Show the identity
            case R.id.my_identity:
                Log.d(TAG, "My Identity");
                intent = new Intent(this, GetActivity.class);
                startActivity(intent);
                return true;
            // Default
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
