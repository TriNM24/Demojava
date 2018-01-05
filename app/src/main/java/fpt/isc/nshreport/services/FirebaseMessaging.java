package fpt.isc.nshreport.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

import fpt.isc.nshreport.R;
import fpt.isc.nshreport.activities.FirstActivity;
import fpt.isc.nshreport.models.objectParse.NotificationCount;
import fpt.isc.nshreport.utilities.NSHSharedPreferences;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.leolin.shortcutbadger.ShortcutBadger;

public class FirebaseMessaging extends FirebaseMessagingService {

    private EventBus bus = EventBus.getDefault();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            String message = remoteMessage.getData().get("message").toString();
            sendNotification(message);

            //get notification count
            getNotifyCount();
        }
    }

    private void getNotifyCount() {

        NSHServer.getServer().notifyCount(NSHSharedPreferences.getToken(this))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError);
    }

    private void handleError(Throwable throwable) {
    }

    private void handleResponse(NotificationCount notificationCount) {
        int count = notificationCount.count;
        NSHSharedPreferences.saveNotifiCount(String.valueOf(count), this);
        //post eventbus
        bus.post(String.valueOf(count));
        ShortcutBadger.applyCount(this,count);
    }


    private void sendNotification(String message) {
        //new code
        Intent intent = new Intent();
        intent.setClass(this, FirstActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.nsh_logo_push2)
                .setContentTitle("NSH_ProjectTools")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        System.out.println("moa   adsfasdf");

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int Unique_Integer_Number = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

        notificationManager.notify(Unique_Integer_Number /* ID of notification */, notificationBuilder.build());
    }
}
