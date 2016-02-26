package vaga.io.wizzchat.adapters;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import vaga.io.wizzchat.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import vaga.io.wizzchat.models.Contact;

public class ContactAdapter extends BaseAdapter {

    private Context _context;
    private LayoutInflater _layoutInflater;
    private ArrayList<Contact> _contacts;

    private static class ViewHolder {
        public ImageView faceImageView;
        public TextView nameTextView;
        public TextView dateTextView;
    }

    public ContactAdapter(Context context, LayoutInflater layoutInflater) {

        this._context = context;
        this._layoutInflater = layoutInflater;

        this._contacts = new ArrayList<Contact>();
    }

    public void load() {

        _contacts.clear();

        Realm realm = Realm.getInstance(_context);
        RealmResults<Contact> contacts = realm.where(Contact.class).findAllSorted("name");

        for (Contact contact : contacts) {
            this.addContact(contact);
        }
    }

    public void addContact(Contact contact) {

        _contacts.add(contact);
        notifyDataSetChanged();
    }

    public void removeContact(int position) {

        Contact contact = (Contact) getItem(position);


        Realm realm = Realm.getInstance(_context);

        realm.beginTransaction();
        contact.removeFromRealm();
        realm.commitTransaction();

        _contacts.remove(position);

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return _contacts.size();
    }

    @Override
    public Object getItem(int position) {
        return _contacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {

            convertView = _layoutInflater.inflate(R.layout.row_contact, null);

            holder = new ViewHolder();
            holder.faceImageView = (ImageView) convertView.findViewById(R.id.faceImageView);
            holder.nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
            holder.dateTextView = (TextView) convertView.findViewById(R.id.dateTextView);

            convertView.setTag(holder);
        }
        else {

            holder = (ViewHolder) convertView.getTag();
        }

        // Retrieve the contact
        Contact contact = (Contact) getItem(position);

        // Hydrate the view
        holder.nameTextView.setText(contact.getName());
        holder.dateTextView.setText(contact.getEmail());

        return convertView;
    }
}
