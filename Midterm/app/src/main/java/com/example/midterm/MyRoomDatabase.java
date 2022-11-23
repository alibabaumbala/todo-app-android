package com.example.midterm;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {TodoItems.class}, version = 1)
public abstract class MyRoomDatabase extends RoomDatabase {

    private static MyRoomDatabase singleton;

    public static MyRoomDatabase getInstance(Context context){
        return Room.databaseBuilder(context.getApplicationContext(),MyRoomDatabase.class,"my_room_database.db")
                .allowMainThreadQueries()
                .build();
    }

    public abstract TodoDAO todoDAO();
}
