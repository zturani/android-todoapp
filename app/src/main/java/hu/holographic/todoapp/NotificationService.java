package hu.holographic.todoapp;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.List;

import hu.holographic.todoapp.model.IToDoItemDAO;
import hu.holographic.todoapp.model.ToDoItem;
import hu.holographic.todoapp.model.ToDoItemDAO;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class NotificationService extends IntentService {

    static final int SERVICE_ID = 1000;
    static final int RQC_TODO = 1;
    static final int RQC_ITEM = 2;
    private List<ToDoItem> todaysItems;
    private IToDoItemDAO dao;
    private NotificationManager mgr;

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("TDI", "notification service started");

        dao = new ToDoItemDAO(this);
        todaysItems = dao.getTodaysItems();
        StringBuilder sb = new StringBuilder();
        for (ToDoItem tdi : todaysItems) {
            sb.append(tdi.getName()).append("\n");
        }

        mgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent appIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingAppIntent = PendingIntent.getActivity(this,RQC_TODO, appIntent, PendingIntent.FLAG_UPDATE_CURRENT );

        Intent itemIntent = new Intent(this, ShowItemActivity.class);
        itemIntent.putExtra("tdi", todaysItems.get(0));
        PendingIntent pendingItemInent = PendingIntent.getActivity(this,RQC_ITEM, itemIntent, PendingIntent.FLAG_UPDATE_CURRENT );


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("Cs1","TodoApp Channel",NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("This is ToDo channel");
            channel.setSound(null, null);
            mgr.createNotificationChannel(channel);
        }

        if (todaysItems.size() > 0) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"Cs1");
            builder.setContentTitle(getString(R.string.todays_tasks))
                    .setNumber(todaysItems.size())
                    .setContentText(sb)
                    .setSmallIcon(R.drawable.ic_check)
            .addAction(R.drawable.ic_check, getString(R.string.todoapp), pendingAppIntent)
            .addAction(R.drawable.ic_check, getString(R.string.details), pendingItemInent);
            Notification n = builder.build();
            mgr.notify(1,n);
        }

    }
}
