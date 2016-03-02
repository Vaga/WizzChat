package vaga.io.wizzchat;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import vaga.io.wizzchat.adapters.ContactAdapter;
import vaga.io.wizzchat.models.Contact;

public class ContactActivity extends AppCompatActivity {

    private static final String TAG = "ContactActivity";

    ListView _contactsListView;

    ContactAdapter _contactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        _contactsListView = (ListView) findViewById(R.id.contactsListView);
        _contactAdapter = new ContactAdapter(this, getLayoutInflater());

        _contactsListView.setAdapter(_contactAdapter);

        _contactsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Contact contact = (Contact) _contactAdapter.getItem(position);

                ContactActivity.this.showDialog(ContactActivity.this, getResources().getString(R.string.remove_contact) + contact.getName(), getResources().getString(R.string.are_you_sure), position);

                return true;
            }
        });

        _contactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Contact contact = (Contact) _contactAdapter.getItem(position);

                Intent intent = new Intent(ContactActivity.this, RoomActivity.class);
                intent.putExtra("email", contact.getEmail());
                startActivity(intent);
            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.title_contact));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onResume() {

        super.onResume();

        _contactAdapter.load();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent;

        switch (item.getItemId()) {
            // Add a contact
            case R.id.new_contact:
                intent = new Intent(this, AddActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private AlertDialog showDialog(final Activity act, String title, String message, final int position) {

        AlertDialog.Builder removeDialog = new AlertDialog.Builder(act);
        removeDialog.setTitle(title);
        removeDialog.setMessage(message);

        removeDialog.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialogInterface, int i) {

                _contactAdapter.removeContact(position);
            }
        });

        removeDialog.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {}
        });

        return removeDialog.show();
    }
}
