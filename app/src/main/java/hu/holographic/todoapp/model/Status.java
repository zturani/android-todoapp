package hu.holographic.todoapp.model;

import android.content.res.Resources;

import androidx.annotation.NonNull;

import hu.holographic.todoapp.R;

public enum Status {
    //DONE(0, "grey"), NORMAL(1, "green"), IMPORTANT(2, "yellow"), VERY_IMPORTANT(3, "red");
    DONE(0, "#A6A6A6"), NORMAL(1, "#7AC909"), IMPORTANT(2, "#F4C117"), VERY_IMPORTANT(3, "#F43817");

    int value;
    String color;

    Status(int value, String color) {
        this.value = value;
        this.color = color;
    }

    public int getValue() {
        return value;
    }

    public String getColor() {
        return color;
    }

    public static Status getStatusByValue(int value) {
        switch (value){
            case 0:
                return Status.DONE;
                case 1:
                return Status.NORMAL;
                case 2:
                return Status.IMPORTANT;
                case 3:
                return Status.VERY_IMPORTANT;
        }
        return null;
    }

    @NonNull
    @Override
    public String toString() {
        switch (this) {
            case DONE:
                return Resources.getSystem().getString(R.string.done);
            case NORMAL:
                return Resources.getSystem().getString(R.string.normal);
            case IMPORTANT:
                return Resources.getSystem().getString(R.string.important);
            case VERY_IMPORTANT:
                return Resources.getSystem().getString(R.string.very_important);
        }
        return null;
    }
}
