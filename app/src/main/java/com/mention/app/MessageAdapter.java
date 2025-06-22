package com.mention.app;

import android.content.Context;
import android.text.Spanned;
import android.view.*;
import android.widget.*;
import java.util.ArrayList;

public class MessageAdapter extends ArrayAdapter<Spanned> {
    public MessageAdapter(Context context, ArrayList<Spanned> data) {
        super(context, 0, data);
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_message, parent, false);

        TextView msg = convertView.findViewById(R.id.msgText);
        msg.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
        msg.setText(getItem(pos));
        return convertView;
    }
}
