package vaga.io.wizzchat.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import fr.tkeunebr.gravatar.Gravatar;
import io.realm.Realm;
import io.realm.RealmResults;
import vaga.io.wizzchat.R;
import vaga.io.wizzchat.models.Contact;
import vaga.io.wizzchat.models.Message;

public class MessageAdapter extends BaseAdapter {

    private static final String TAG = "MessageAdapter";

    private Context _context;
    private LayoutInflater _layoutInflater;
    private ArrayList<Message> _messages;

    private static class ViewHolder {
        public TextView contentTextView;
        public TextView dateTextView;
    }

    public MessageAdapter(Context context, LayoutInflater layoutInflater) {

        this._context = context;
        this._layoutInflater = layoutInflater;

        this._messages = new ArrayList<Message>();
    }

    public void addMessage(Message message) {

        _messages.add(message);
        notifyDataSetChanged();
    }

    public void sort() {

        Collections.sort(_messages, new Comparator<Message>() {

            @Override
            public int compare(Message lhs, Message rhs) {

                DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                try {
                    Date d1 = format.parse(lhs.getDate());
                    Date d2 = format.parse(rhs.getDate());

                    return d1.compareTo(d2);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });

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

        // Retrieve the message
        Message message = (Message) getItem(position);

        //if (convertView == null) {

        if (message.getTo() == "")
            convertView = _layoutInflater.inflate(R.layout.row_message_other, null);
        else
            convertView = _layoutInflater.inflate(R.layout.row_message_me, null);

        holder = new ViewHolder();
        holder.contentTextView = (TextView) convertView.findViewById(R.id.contentTextView);
        holder.dateTextView = (TextView) convertView.findViewById(R.id.dateTextView);

        convertView.setTag(holder);
        //}
        //else {

        //    holder = (ViewHolder) convertView.getTag();
        //}

        // Hydrate the view
        holder.contentTextView.setText(message.getContent());
        holder.dateTextView.setText(message.getDate());

        return convertView;
    }
}
