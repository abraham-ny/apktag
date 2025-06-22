package com.mention.app;

import android.app.*;
import android.database.*;
import android.os.*;
import android.provider.*;
import android.text.*;
import android.text.method.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.util.*;
import java.util.regex.*;

public class MainActivity extends Activity {

    private EditText input;
    private Button sendBtn;
    private ListView listView;
    private ArrayAdapter<Spanned> adapter;
    private ArrayList<Spanned> messages = new ArrayList<>();
    private PopupWindow popup;
    private List<String> contactNames = new ArrayList<>();
	TextView mtv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		mtv=findViewById(R.id.emptyView);
		SpannableString stt = new SpannableString("https://abrahamonline.netlify.app");
		applySpans(stt);
		getActionBar().setSubtitle(new SpannableString(stt));
		SpannableString spn = new SpannableString(mtv.getText().toString());
		applySpans(spn);
		mtv.setText(spn);

        input = findViewById(R.id.editText);
        sendBtn = findViewById(R.id.sendBtn);
        listView = findViewById(R.id.listView);
		listView.setEmptyView(mtv);

        adapter = new MessageAdapter(this, messages);
        listView.setAdapter(adapter);
        input.setMovementMethod(LinkMovementMethod.getInstance());

        loadContacts();

        input.addTextChangedListener(new TextWatcher() {
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
				public void afterTextChanged(Editable s) {}

				public void onTextChanged(CharSequence s, int start, int before, int count) {
					if (count > 0 && s.charAt(start) == '@') {
						showContactPopup(start);
						input.requestFocus();
					}
				}
			});

        sendBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String text = input.getText().toString();
					SpannableString spannable = new SpannableString(text);
					applySpans(spannable);
					messages.add(spannable);
					adapter.notifyDataSetChanged();
					input.setText("");
				}
			});
    }

    private void loadContacts() {
        Cursor cursor = getContentResolver().query(
            ContactsContract.Contacts.CONTENT_URI,
            null, null, null, null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                contactNames.add(name);
            }
            cursor.close();
        }
    }

    private void showContactPopup(int atIndex) {
        ListView popupList = new ListView(this);
        ArrayAdapter<String> popupAdapter = new ArrayAdapter<>(
            this, android.R.layout.simple_list_item_1, contactNames);
        popupList.setAdapter(popupAdapter);

        popup = new PopupWindow(popupList, 400, 600, true);
        popup.showAsDropDown(input);

        popupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					String name = contactNames.get(position);
					Editable text = input.getText();
					int insertAt = input.getSelectionStart();
					text.insert(insertAt, name);
					popup.dismiss();
				}
			});
    }

    private void applySpans(SpannableString s) {
        Pattern mentionPattern = Pattern.compile("@\\S+");
        Pattern hashtagPattern = Pattern.compile("#\\w+");
        Pattern urlPattern = Patterns.WEB_URL;

        applyPatternSpan(s, mentionPattern, "mention");
        applyPatternSpan(s, hashtagPattern, "hashtag");
        applyPatternSpan(s, urlPattern, "url");
    }

    private void applyPatternSpan(SpannableString s, Pattern pattern, String type) {
        Matcher matcher = pattern.matcher(s);
        while (matcher.find()) {
            final String matched = matcher.group();
            int start = matcher.start();
            int end = matcher.end();

            ClickableSpan span = new MentionSpan(type, matched, this);
            s.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }
}
