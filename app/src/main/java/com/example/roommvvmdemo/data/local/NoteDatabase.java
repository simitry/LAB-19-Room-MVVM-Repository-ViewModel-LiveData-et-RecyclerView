package com.example.roommvvmdemo.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * RoomDatabase layer.
 *
 * This is the central access point to the local SQLite database.
 * Room generates the real implementation at compile time.
 */
@Database(entities = {Note.class}, version = 1, exportSchema = false)
public abstract class NoteDatabase extends RoomDatabase {

    /*
     * Room will generate the body of this method.
     * The DAO returned here is what the Repository uses.
     */
    public abstract NoteDao noteDao();

    /*
     * Singleton instance.
     *
     * volatile matters because this database can be requested from different
     * threads. It helps every thread see the latest value of instance.
     */
    private static volatile NoteDatabase instance;

    public static NoteDatabase getInstance(Context context) {
        if (instance == null) {
            /*
             * synchronized prevents two threads from creating two databases
             * at the same time during app startup.
             */
            synchronized (NoteDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    NoteDatabase.class,
                                    "notes_database"
                            )
                            /*
                             * Acceptable for a lab:
                             * if the schema version changes without a migration,
                             * Room recreates the database.
                             *
                             * In production, write real Migration objects instead,
                             * otherwise users can lose data.
                             */
                            .fallbackToDestructiveMigration(true)
                            .build();
                }
            }
        }

        return instance;
    }
}
