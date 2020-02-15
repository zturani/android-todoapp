package hu.holographic.todoapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wdullaer.swipeactionadapter.SwipeActionAdapter;
import com.wdullaer.swipeactionadapter.SwipeDirection;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import hu.holographic.todoapp.model.IToDoItemDAO;
import hu.holographic.todoapp.model.Status;
import hu.holographic.todoapp.model.ToDoItem;
import hu.holographic.todoapp.model.ToDoItemAdapter;
import hu.holographic.todoapp.model.ToDoItemDAO;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final int RQC_NEW = 1;
    private static final int RQC_EDIT = 2;
    private List<ToDoItem> items;
    private ToDoItemAdapter adapter;
    private SwipeActionAdapter swipeAdapter;
    private IToDoItemDAO dao;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    boolean swipe;
    boolean appRunning;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dao = new ToDoItemDAO(this);
        items = dao.getAllItems();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sortItems();
        List<ToDoItem> todaysItems = dao.getTodaysItems();

        adapter = new ToDoItemAdapter(this, R.layout.todoitem, items);
        ListView lvItems = findViewById(R.id.lvItems);
        lvItems.setOnItemClickListener(this);
        registerForContextMenu(lvItems);

        //beállítások figyelése
        listener = (prefs, key) -> {
            Log.i("TDI", "prefs changed");
            swipe = sharedPreferences.getBoolean("swipe", true);
            sortItems();
            adapter.notifyDataSetChanged();
            setAlarm(sharedPreferences.getBoolean("service", false));
        };
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
        setAlarm(sharedPreferences.getBoolean("service", false));

        //új elem gomb
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ToDoItemActivity.class);
                startActivityForResult(intent, RQC_NEW);
            }
        });

        //swipe akciók https://github.com/wdullaer/SwipeActionAdapter
        swipeAdapter = new SwipeActionAdapter(adapter);
        swipeAdapter.setListView(lvItems)
                .setFarSwipeFraction(1)
                .setNormalSwipeFraction(.5F)
                .setDimBackgrounds(true).addBackground(SwipeDirection.DIRECTION_NORMAL_LEFT, R.layout.item_bg_left)
                .addBackground(SwipeDirection.DIRECTION_NORMAL_RIGHT, R.layout.item_bg_right);
        lvItems.setAdapter(swipeAdapter);
        swipe = sharedPreferences.getBoolean("swipe", true);
        swipeAdapter.setSwipeActionListener(new SwipeActionAdapter.SwipeActionListener() { //swipe
            @Override
            public boolean hasActions(int position, SwipeDirection direction) {
                if (direction.isLeft()) return swipe;
                if (direction.isRight()) return swipe;
                return false;
            }

            @Override
            public boolean shouldDismiss(int i, SwipeDirection direction) {
                if (direction == SwipeDirection.DIRECTION_NORMAL_LEFT) {
                    return true;
                }
                return false;
            }

            @Override
            public void onSwipe(int[] positions, SwipeDirection[] directions) {
                for (int i = 0; i < directions.length; i++) {
                    SwipeDirection direction = directions[i];
                    int position = positions[i];
                    ToDoItem tdi = items.get(position);

                    switch (direction) {
                        case DIRECTION_NORMAL_LEFT: //törlés
                            adapter.remove(tdi);
                            dao.deleteItem(tdi);
                            break;
                        case DIRECTION_NORMAL_RIGHT: //pipa
                            tdi.setStatus(Status.DONE);
                            dao.saveOrUpdateItem(tdi);
                            break;
                    }
                    sortItems();
                    swipeAdapter.notifyDataSetChanged();
                }
            }
        });

        //ma lejáró tennivalók
        if (!appRunning && todaysItems.size() > 0) { //csak indításnál
            appRunning = true;
            StringBuilder sb = new StringBuilder();
            for (ToDoItem tdi : todaysItems) {
                sb.append(tdi.getName()).append("\n");
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this); //dialógus összeállítása
            builder.setTitle(getString(R.string.todays_tasks))
                    .setMessage(sb)
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog ad = builder.create();
            ad.show();
        }
    }

    //menü létrehozása
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //menü elemek kezelése
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) { //beállítások
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;

        } else if (id == R.id.mi_add) { //új elem
            Intent intent = new Intent(getApplicationContext(), ToDoItemActivity.class);
            startActivityForResult(intent, RQC_NEW);

        } else if (id == R.id.mi_removeall) { //minden elem törlése
            AlertDialog.Builder builder = new AlertDialog.Builder(this); //dialógus összeállítása
            builder.setTitle("Törlés megerősítése")
                    .setMessage("Biztosan törölsz minden elemet?")
                    .setNegativeButton("Nem", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setPositiveButton("Igen", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            adapter.clear();
                            dao.deleteAllItems();
                            dialog.dismiss();
                        }
                    });
            AlertDialog ad = builder.create(); //legyártása
            ad.show(); //megjelenítése
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //helyi menü létrehozása
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.contextmenu_main, menu);
    }

    //helyi menü kezelése
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position;
        ToDoItem tdi = items.get(index);

        switch (id) {
            case R.id.ciDelete:
                adapter.remove(tdi);
                dao.deleteItem(tdi);
                return true;
            case R.id.ciDone:
                tdi.setStatus(Status.DONE);
                dao.saveOrUpdateItem(tdi);
                sortItems();
                adapter.notifyDataSetChanged();
                return true;
            case R.id.ciEdit:
                Intent intent = new Intent(this, ToDoItemActivity.class);
                intent.putExtra("tdi", tdi);
                intent.putExtra("index", index);
                startActivityForResult(intent, RQC_EDIT);
                sortItems();
                adapter.notifyDataSetChanged();
                return true;

        }
        return super.onContextItemSelected(item);
    }

    //visszakapott tdi
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            ToDoItem tdi;
            tdi = (ToDoItem) data.getSerializableExtra("tdi");

            if (requestCode == RQC_NEW) {
                adapter.add(tdi);
                dao.saveOrUpdateItem(tdi);

            } else if (requestCode == RQC_EDIT) {
                int index = data.getIntExtra("index", -1);
                items.set(index, tdi);

                dao.saveOrUpdateItem(tdi);
            }
            sortItems();
            adapter.notifyDataSetChanged();
        }
    }

    //elemre kattintás
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, ShowItemActivity.class);
        ToDoItem tdi = items.get(position);
        intent.putExtra("tdi", tdi);
        startActivity(intent);
    }

    //elemek rendezése
    public void sortItems() {
        String order = sharedPreferences.getString("order", "");
        switch (order) {
            case "by_name":
                Collections.sort(items, (o1, o2) -> o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase()));
                break;
            case "by_deadline":
                Collections.sort(items, (o1, o2) -> {
                    int result;
                    result = o1.getYear() - o2.getYear();
                    if (result != 0) return result;
                    result = o1.getMonth() - o2.getMonth();
                    if (result != 0) return result;
                    result = o1.getDay() - o2.getDay();
                    return result;
                });
                break;
            case "by_importance":
                Collections.sort(items, (o1, o2) -> {
                    int result = o2.getStatus().getValue() - o1.getStatus().getValue();
                    return result;
                });
                break;
            default: //by_creation date
                Collections.sort(items, (o1, o2) -> {
                    int result = (int) (o2.getTimestamp() - o1.getTimestamp());
                    return result;
                });
                break;
        }
    }

    //alarm beállítása
    public void setAlarm(boolean alarm) {
        AlarmManager alarmMgr;
        PendingIntent alarmIntent;

        alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationService.class);

        PendingIntent servicePendingIntent = PendingIntent.getService(context,
                NotificationService.SERVICE_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        if (alarm) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 7);
            calendar.set(Calendar.MINUTE, 0);
            alarmMgr.setInexactRepeating(AlarmManager.RTC,calendar.getTimeInMillis(),AlarmManager.INTERVAL_HALF_DAY,servicePendingIntent);
            //alarmMgr.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 1000 * 60 * 1, servicePendingIntent);
            Log.i("TDI", "alarm set");
        } else {
            alarmMgr.cancel(servicePendingIntent);
            Log.i("TDI", "alarm unset");
        }
    }
}
