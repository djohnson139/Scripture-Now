package com.caseybrooks.scripturememory.notifications;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.caseybrooks.androidbibletools.basic.Passage;
import com.caseybrooks.androidbibletools.defaults.DefaultFormatter;
import com.caseybrooks.scripturememory.R;
import com.caseybrooks.scripturememory.activities.MainActivity;
import com.caseybrooks.scripturememory.data.MetaReceiver;
import com.caseybrooks.scripturememory.data.MetaSettings;
import com.caseybrooks.scripturememory.data.VerseDB;

public class MainNotification {
//Data Members
//------------------------------------------------------------------------------

    private static MainNotification instance = null;

	private Notification notification;
    private NotificationManager manager;

	private int id;

//Constructors and Initialization
//------------------------------------------------------------------------------
	private MainNotification() {
    }

    public static MainNotification getInstance() {
        if(instance == null) instance = new MainNotification();
        return instance;
    }
	
	public static MainNotification notify(Context context) {
        getInstance();

        instance.manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        instance.id = MetaSettings.getVerseId(context);

        VerseDB db = new VerseDB(context);

        db.open();
        Passage verse = db.getVerse(instance.id);
        db.close();

        if(verse != null) {
            switch (MetaSettings.getVerseDisplayMode(context)) {
                case 0:
                    verse.setFormatter(new DefaultFormatter.Normal());
                    break;
                case 1:
                    verse.setFormatter(new DefaultFormatter.Dashes());
                    break;
                case 2:
                    verse.setFormatter(new DefaultFormatter.FirstLetters());
                    break;
                case 3:
                    verse.setFormatter(new DefaultFormatter.DashedLetter());
                    break;
                case 4:
                    float randomness = MetaSettings.getRandomnessLevel(context);
                    int offset = MetaSettings.getRandomSeedOffset(context);
                    verse.setFormatter(new DefaultFormatter.RandomWords(randomness, offset));
                    break;
                default:
                    break;
            }
        }

        //Opens the dashboard when clicked
        Intent resultIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPI = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        //Builds the notification
        NotificationCompat.Builder mb = new NotificationCompat.Builder(context);

        //Removes the notification when the user hits "Dismiss"
        Intent dismiss = new Intent(context, DismissVerseReceiver.class);
        PendingIntent dismissPI = PendingIntent.getBroadcast(context, 0, dismiss, PendingIntent.FLAG_CANCEL_CURRENT);
        mb.addAction(R.drawable.ic_action_clear_dark, "Dismiss", dismissPI);

        //goes to the next verse when the user hits "Next"
        if(verse != null) {
            Intent next = new Intent(MetaReceiver.NEXT_VERSE);
            next.putExtra("notificationId", 1);
            next.putExtra("SQL_ID", MetaSettings.getVerseId(context));
            PendingIntent nextPI = PendingIntent.getBroadcast(context, 0, next, PendingIntent.FLAG_CANCEL_CURRENT);
            mb.addAction(R.drawable.ic_action_arrow_right_dark, "Next", nextPI);

            mb.setContentTitle(verse.getReference().toString());
            mb.setContentText(verse.getText());
            mb.setStyle(new NotificationCompat.BigTextStyle().bigText(verse.getText()));
        }
        else {
            mb.setContentTitle("All verses memorized!");
            mb.setContentText("Why don't you try adding some more, or start memorizing a different list?");
        }

        mb.setOngoing(true);
        mb.setSmallIcon(R.drawable.ic_cross);
        mb.setPriority(NotificationCompat.PRIORITY_LOW);
        mb.setContentIntent(resultPI);
        instance.notification = mb.build();

        return instance;
	}
	
	public void show() {
        if(instance != null) instance.manager.notify(1, instance.notification);
	}
	
	public void dismiss() {
        if(instance != null) instance.manager.cancel(1);
	}
	
//Broadcast Receivers relevant to this notification
//------------------------------------------------------------------------------
	
	//Button to dismiss the ongoing notification
    public static class DismissVerseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            MetaSettings.putNotificationActive(context, false);
            MainNotification.getInstance().dismiss();
        }
    }
}