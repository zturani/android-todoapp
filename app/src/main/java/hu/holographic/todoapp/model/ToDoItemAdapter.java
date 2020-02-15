package hu.holographic.todoapp.model;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import hu.holographic.todoapp.R;

public class ToDoItemAdapter extends ArrayAdapter<ToDoItem> {

    private int resource;
    private Context context;

    public ToDoItemAdapter(@NonNull Context context, int resource,  @NonNull List<ToDoItem> objects) {
        super(context, resource, objects);
        this.resource =resource;
        this.context=context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ToDoItem tdi = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(resource,null); //xml felfújás
        }

        TextView tvName = convertView.findViewById(R.id.tvName);
        TextView tvDate = convertView.findViewById(R.id.tvDate);
        ImageView ivStatus = convertView.findViewById(R.id.ivStatus);

        tvName.setText(tdi.getName());
        tvDate.setText(tdi.getYear()+"."+tdi.getMonth()+"."+tdi.getDay()+".");

        if (tdi.getStatus()==Status.DONE){
            tvName.setTextColor(Color.GRAY);
            ivStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.circle_grey)); //szürke pipa
        } else {
            tvName.setTextColor(Color.BLACK);
            Drawable d = context.getResources().getDrawable(R.drawable.circle_full);
            d.setColorFilter(Color.parseColor(tdi.getStatus().color), PorterDuff.Mode.SRC_IN); //színes kör
            ivStatus.setImageDrawable(d);
        }

        return convertView;
    }

}
