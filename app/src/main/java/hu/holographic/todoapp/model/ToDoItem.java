package hu.holographic.todoapp.model;

import java.io.Serializable;

public class ToDoItem implements Serializable {
    private int id;
    private String name;
    private int year;
    private int month;
    private int day;
    private Status status;
    private long timestamp;

    public ToDoItem() {
        this.id = -1;
        this.timestamp = System.currentTimeMillis();
    }

    public ToDoItem(String name, int year, int month, int day, Status status) {
        this.id = -1;
        this.name = name;
        this.year = year;
        this.month = month;
        this.day = day;
        this.status = status;
        this.timestamp = System.currentTimeMillis();
    }

    public ToDoItem(int id, String name, int year, int month, int day, Status status, long timestamp) {
        this.id = id;
        this.name = name;
        this.year = year;
        this.month = month;
        this.day = day;
        this.status = status;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public long getTimestamp() { return timestamp; }

    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
