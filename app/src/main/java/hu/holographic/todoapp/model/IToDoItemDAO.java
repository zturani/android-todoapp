package hu.holographic.todoapp.model;

import java.util.List;

public interface IToDoItemDAO {
    List<ToDoItem> getAllItems ();
    List<ToDoItem> getTodaysItems ();
    void deleteItem(ToDoItem item);
    void saveOrUpdateItem(ToDoItem item);
    void deleteAllItems ();
}
