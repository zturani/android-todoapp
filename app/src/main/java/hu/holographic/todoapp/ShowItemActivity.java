package hu.holographic.todoapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import hu.holographic.todoapp.model.ToDoItem;


public class ShowItemActivity extends AppCompatActivity {

    private CalendarView cvTaskDate;
    private LinearLayout llTitleBackGr;
    private TextView tvTaskName;
    private TextView tvStatus;
    private TextView tvDaysToGo;
    private Intent intent;
    private ToDoItem tdi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showitem);

        intent = getIntent();
        tdi = (ToDoItem) intent.getSerializableExtra("tdi");
        cvTaskDate = findViewById(R.id.cvTaskDate);
        llTitleBackGr = findViewById(R.id.llTitleBackGr);
        tvTaskName  = findViewById(R.id.tvTaskName);
        tvStatus = findViewById(R.id.tvStatus);
        tvDaysToGo = findViewById(R.id.tvDaysToGo);

        Calendar c = Calendar.getInstance();
        c.set(tdi.getYear(), tdi.getMonth()-1, tdi.getDay());
        long date = c.getTimeInMillis();
        int color = Color.parseColor(tdi.getStatus().getColor());

        cvTaskDate.setDate(date,false,true);
        cvTaskDate.setOnDateChangeListener((view, year, month, dayOfMonth) -> cvTaskDate.setDate(date)); //ne lehessen más napot kijelölni
        llTitleBackGr.setBackgroundColor(color);
        tvTaskName.setText(tdi.getName());
        tvStatus.setText(getString(tdi.getStatus().getResourceId()));
        tvStatus.setTextColor(Color.WHITE);
        long diff = date-System.currentTimeMillis();
        int days = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

        Date d = new Date();
                //(int) ((date-System.currentTimeMillis())/86400000)+1;

        if (days==0 && tdi.getDay()== d.getDate()){
            tvDaysToGo.setText(R.string.today);
            tvDaysToGo.setTextColor(color);
        } else if(days==0) {
            tvDaysToGo.setText(getString(R.string.less_than_one_day));
            tvDaysToGo.setTextColor(color);
        }
         else if(days>=0) {
            tvDaysToGo.setText(getString(R.string.days_to_go,days));
            tvDaysToGo.setTextColor(color);
        } else{
            tvDaysToGo.setText(getString(R.string.days_ago,Math.abs(days)));
            tvDaysToGo.setTextColor(Color.RED);
        }


        //vissza gomb
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
