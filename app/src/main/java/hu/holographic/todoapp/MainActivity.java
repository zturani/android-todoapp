package hu.holographic.todoapp;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wdullaer.swipeactionadapter.SwipeActionAdapter;
import com.wdullaer.swipeactionadapter.SwipeDirection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.ContextMenu;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ToDoItemActivity.class);
                startActivityForResult(intent, RQC_NEW);
            }
        });

        dao = new ToDoItemDAO(this);
        items = dao.getAllItems();

        adapter = new ToDoItemAdapter(this, R.layout.todoitem, items);
        ListView lvItems = findViewById(R.id.lvItems);

        //https://github.com/wdullaer/SwipeActionAdapter
        swipeAdapter = new SwipeActionAdapter(adapter);
        swipeAdapter.setListView(lvItems)
                .setFarSwipeFraction(1)
                .setNormalSwipeFraction(.5F)
                .setDimBackgrounds(true).addBackground(SwipeDirection.DIRECTION_NORMAL_LEFT, R.layout.row_bg_left)
                .addBackground(SwipeDirection.DIRECTION_NORMAL_RIGHT, R.layout.draw_bg_right);

        lvItems.setAdapter(swipeAdapter);

        registerForContextMenu(lvItems);

        swipeAdapter.setSwipeActionListener(new SwipeActionAdapter.SwipeActionListener() {
            @Override
            public boolean hasActions(int position, SwipeDirection direction) {
                if (direction.isLeft()) return true; // Change this to false to disable left swipes
                if (direction.isRight()) return true;
                return false;
            }

            @Override
            public boolean shouldDismiss(int i, SwipeDirection direction) {
             if ( direction == SwipeDirection.DIRECTION_NORMAL_LEFT) {
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
                    swipeAdapter.notifyDataSetChanged();
                }
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.contextmenu_main, menu);
    }

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
                adapter.notifyDataSetChanged();
                return true;
            case R.id.ciEdit:
                Intent intent = new Intent(this, ToDoItemActivity.class);
                intent.putExtra("tdi", tdi);
                intent.putExtra("index", index);
                startActivityForResult(intent, RQC_EDIT);
                return true;

        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {

            ToDoItem tdi;
            tdi = (ToDoItem) data.getSerializableExtra("tdi");

            if (requestCode == RQC_NEW) {
                adapter.add(tdi);
                dao.saveOrUpdateItem(tdi);
            } else if (requestCode == RQC_EDIT) {
                int index = data.getIntExtra("index", -1);
                items.set(index, tdi);
                swipeAdapter.notifyDataSetChanged();
                dao.saveOrUpdateItem(tdi);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
