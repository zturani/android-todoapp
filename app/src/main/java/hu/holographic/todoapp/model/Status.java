package hu.holographic.todoapp.model;

import hu.holographic.todoapp.R;

public enum Status {
    DONE(0, "#A6A6A6", R.string.done),
    NORMAL(1, "#7AC909", R.string.normal),
    IMPORTANT(2, "#F4C117", R.string.important),
    VERY_IMPORTANT(3, "#F43817", R.string.very_important);

    int value;
    String color;
    int resourceId;

    Status(int value, String color, int resourceId) {
        this.value = value;
        this.color = color;
        this.resourceId = resourceId;
    }

    public int getValue() {
        return value;
    }

    public String getColor() {
        return color;
    }

    public int getResourceId() { return resourceId; }

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
}
