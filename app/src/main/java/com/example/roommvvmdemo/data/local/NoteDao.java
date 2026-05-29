package com.example.roommvvmdemo.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/**
 * DAO layer: Data Access Object.
 *
 * The DAO is the only class that describes SQL operations.
 * Nobody else in the app writes SQL directly.
 */
@Dao
public interface NoteDao {

    /*
     * Room generates the INSERT statement.
     * This method must not be called on the main thread.
     */
    @Insert
    void insert(Note note);

    /*
     * Room deletes by primary key.
     * The Note object must contain the id of the row to remove.
     */
    @Delete
    void delete(Note note);

    /*
     * A simple destructive action for the lab.
     * In real apps, this might be protected by a confirmation dialog.
     */
    @Query("DELETE FROM notes_table")
    void deleteAllNotes();

    /*
     * LiveData<List<Note>> is the key part of the reactive flow.
     *
     * When the table changes, Room automatically emits a fresh List<Note>.
     * MainActivity does not manually reload the database after insert/delete.
     */
    @Query("SELECT * FROM notes_table ORDER BY id DESC")
    LiveData<List<Note>> getAllNotes();
}
