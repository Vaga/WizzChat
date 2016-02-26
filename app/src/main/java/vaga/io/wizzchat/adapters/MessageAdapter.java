package vaga.io.wizzchat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import fr.tkeunebr.gravatar.Gravatar;
import io.realm.Realm;
import io.realm.RealmResults;
import vaga.io.wizzchat.R;
import vaga.io.wizzchat.models.Contact;

public class MessageAdapter extends BaseAdapter {

    private class Message {

        public String content;
    }

    private Context _context;
    private LayoutInflater _layoutInflater;
    private ArrayList<Message> _messages;

    private static class ViewHolder {
        public TextView messageTextView;
    }

    public MessageAdapter(Context context, LayoutInflater layoutInflater) {

        this._context = context;
        this._layoutInflater = layoutInflater;

        this._messages = new ArrayList<Message>();
    }

    public void load() {

        _messages.clear();

        Realm realm = Realm.getInstance(_context);
        RealmResults<Contact> contacts = realm.where(Contact.class).findAllSorted("name");

        for (Message message : _messages) {
            this.addMessage("test");
        }
    }

    public void addMessage(/*Message message*/String content) {

        Message message = new Message();
        message.content = content;

        _messages.add(message);
        notifyDataSetChanged();
    }

    public void removeMessage(int position) {

        Message message = (Message) getItem(position);


//        Realm realm = Realm.getInstance(_context);
//
//        realm.beginTransaction();
//        contact.removeFromRealm();
//        realm.commitTransaction();
//
//        _contacts.remove(position);

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return _messages.size();
    }

    @Override
    public Object getItem(int position) {
        return _messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {

            convertView = _layoutInflater.inflate(R.layout.row_message, null);

            holder = new ViewHolder();
            holder.messageTextView = (TextView) convertView.findViewById(R.id.messageTextView);

            convertView.setTag(holder);
        }
        else {

            holder = (ViewHolder) convertView.getTag();
        }

        // Retrieve the contact
        Message message = (Message) getItem(position);

        // Hydrate the view
        holder.messageTextView.setText(message.content);

        return convertView;
    }
}
