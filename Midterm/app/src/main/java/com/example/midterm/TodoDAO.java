package com.example.midterm;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TodoDAO {

    @Query("update TodoItems set title = :title,content = :content where id = :id")
    int update(String title,String content,String id);

    @Query("DELETE FROM TodoItems WHERE id = :id")
    int delete(String id);

    @Query("DELETE FROM TodoItems")
    int deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long add(TodoItems item);

    @Query("SELECT * FROM TodoItems WHERE ID = :id")
    boolean get(String id);

    @Query("SELECT * FROM TodoItems where userID = :userID")
    List<TodoItems> getAll(String userID);
}
