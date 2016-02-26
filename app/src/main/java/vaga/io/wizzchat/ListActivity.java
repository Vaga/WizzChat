package vaga.io.wizzchat;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import vaga.io.wizzchat.adapters.ContactAdapter;
import vaga.io.wizzchat.models.Contact;

public class ListActivity extends AppCompatActivity {

    private static final String TAG = "ListActivity";

    ListView _contactsListView;

    ContactAdapter _contactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        _contactsListView = (ListView) findViewById(R.id.contactsListView);
        _contactAdapter = new ContactAdapter(this, getLayoutInflater());

        _contactsListView.setAdapter(_contactAdapter);

        _contactsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Contact contact = (Contact) _contactAdapter.getItem(position);

                ListActivity.this.showDialog(ListActivity.this, "Remove " + contact.getName(), "Are you sure ?", position);

                return false;
            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onResume() {

        super.onResume();

        _contactAdapter.load();
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

    private AlertDialog showDialog(final Activity act, String title, String message, final int position) {

        AlertDialog.Builder removeDialog = new AlertDialog.Builder(act);
        removeDialog.setTitle(title);
        removeDialog.setMessage(message);

        removeDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialogInterface, int i) {

                _contactAdapter.removeContact(position);
            }
        });

        removeDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {}
        });

        return removeDialog.show();
    }
}
