package hu.holographic.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import hu.holographic.todoapp.model.Status;
import hu.holographic.todoapp.model.ToDoItem;

public class ToDoItemActivity extends AppCompatActivity {

    private ToDoItem tdi;
    private EditText etName;
    private RadioGroup rgStatus;
    private RadioButton radioButton;
    private DatePicker dpDate;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todoitem);

        etName = findViewById(R.id.etName);
        rgStatus = findViewById(R.id.rgStatus);
        dpDate = findViewById(R.id.dpDate);

        intent = getIntent();
        tdi = (ToDoItem) intent.getSerializableExtra("tdi");

        //TODO: rádiógombok színét programból állítani
        if (tdi != null) { //ha edit
            etName.setText(tdi.getName());
            int statusValue = tdi.getStatus().getValue();
            if (statusValue == 0) statusValue = 1; //készből legyen normál
            radioButton = (RadioButton) rgStatus.getChildAt(statusValue - 1);
            radioButton.toggle();

            dpDate.updateDate(tdi.getYear(), tdi.getMonth()-1, tdi.getDay());

            setTitle(R.string.edit_task);
        } else {
            setTitle(R.string.new_task);
        }

        //vissza gomb
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public void cancel(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void save(View view) {
        if (tdi == null) {
            tdi = new ToDoItem();
        }

        tdi.setName(etName.getText().toString());

        View setButton = rgStatus.findViewById(rgStatus.getCheckedRadioButtonId());
        int statusId = rgStatus.indexOfChild(setButton) + 1; // 1: normal 2: fontos 3: nagyon fontos
        tdi.setStatus(Status.getStatusByValue(statusId));

        tdi.setYear(dpDate.getYear());
        tdi.setMonth(dpDate.getMonth()+1);
        tdi.setDay(dpDate.getDayOfMonth());

        intent.putExtra("tdi", tdi);
        setResult(RESULT_OK, intent);
        finish();
    }
}
