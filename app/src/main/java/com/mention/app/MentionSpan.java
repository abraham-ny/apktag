package com.mention.app;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.net.*;
import android.text.*;
import android.text.style.*;
import android.view.*;
import android.widget.*;

public class MentionSpan extends ClickableSpan {
    private final String type;
    private final String value;
    private final Context context;

    public MentionSpan(String type, String value, Context context) {
        this.type = type;
        this.value = value;
        this.context = context;
    }

    @Override
    public void onClick(View widget) {
        switch (type) {
            case "mention":
                if (value.startsWith("@com.")) {
                    // Try to launch app
                    Intent launch = context.getPackageManager().getLaunchIntentForPackage(value.substring(1));
                    if (launch != null) {
                        context.startActivity(launch);
                    } else {
                        Toast.makeText(context, "App not installed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Mentioned: " + value, Toast.LENGTH_SHORT).show();
                }
                break;
            case "hashtag":
                Toast.makeText(context, "Hashtag: " + value, Toast.LENGTH_SHORT).show();
				if(value.contains("about")||value.contains("info")){
					abtDlg();
				}else if(value.contains("source")||value.contains("code")){
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse("https://github.com/abraham-ny/apktag"));
					context.startActivity(i);
				}else if(value.contains("video")){
					openYouTubeOrFallback(context, "uuidsd");
				}
                break;
            case "url":
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(value));
                context.startActivity(i);
                break;
        }
    }
	
	public void openYouTubeOrFallback(Context context, String videoId) {
		String youtubeAppUrl = "vnd.youtube:" + videoId;
		String webUrl = "https://www.youtube.com/watch?v=" + videoId;

		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeAppUrl));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		// Try YouTube app
		try {
			context.getPackageManager().getPackageInfo("com.google.android.youtube", 0);
			intent.setPackage("com.google.android.youtube");
			context.startActivity(intent);
			return;
		} catch (Exception e) {
			// App not found or failed, continue
		}

		// Try YouTube Go
		try {
			context.getPackageManager().getPackageInfo("com.google.android.apps.youtube.mango", 0);
			intent.setPackage("com.google.android.apps.youtube.mango");
			context.startActivity(intent);
			return;
		} catch (Exception e) {
			// App not found or failed, continue
		}

		// Try web browser
		try {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl));
			browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(browserIntent);
			return;
		} catch (Exception e) {
			// Browser not available, fallback to clipboard
		}

		// Copy to clipboard
		android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData clip = ClipData.newPlainText("YouTube URL", webUrl);
		clipboard.setPrimaryClip(clip);
		Toast.makeText(context, "Copied URL", Toast.LENGTH_SHORT).show();
	}
	
	public void abtDlg(){
		AlertDialog.Builder bld = new AlertDialog.Builder(context);
		bld.setTitle("@#");
		bld.setMessage("Mentioned is a demo on how to implement hashtag, mentions and url detection and formatting on android using java.\n\t(c) 2025 Abraham Moruri.")
			.setPositiveButton("Close", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					p1.cancel();
				}
			});
		AlertDialog dlg = bld.create();
		dlg.show();
		Toast.makeText(context, "Star and fork this project on github", Toast.LENGTH_LONG).show();
	}

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(Color.BLUE);
        ds.setUnderlineText(false);
        ds.bgColor = Color.parseColor("#DDDDFF");
    }
}
