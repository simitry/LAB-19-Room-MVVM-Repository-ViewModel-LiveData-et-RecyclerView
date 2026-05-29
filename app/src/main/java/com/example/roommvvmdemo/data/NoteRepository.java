package com.example.roommvvmdemo.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.roommvvmdemo.data.local.Note;
import com.example.roommvvmdemo.data.local.NoteDao;
import com.example.roommvvmdemo.data.local.NoteDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repository layer.
 *
 * The Repository hides the data source details from the ViewModel.
 * Today the source is Room. Tomorrow it could be Room + Retrofit + cache rules.
 */
public class NoteRepository {

    private final NoteDao noteDao;
    private final LiveData<List<Note>> allNotes;

    /*
     * Single-thread executor:
     * Room writes happen one at a time, in order, away from the UI thread.
     *
     * This is deliberately simple for the lab. In a bigger app, you might inject
     * a shared executor or use coroutines/Rx depending on the stack.
     */
    private final ExecutorService databaseExecutor;

    public NoteRepository(Application application) {
        NoteDatabase database = NoteDatabase.getInstance(application);

        noteDao = database.noteDao();
        allNotes = noteDao.getAllNotes();
        databaseExecutor = Executors.newSingleThreadExecutor();
    }

    public void insert(Note note) {
        /*
         * Never block the main thread with database writes.
         * This keeps typing, scrolling, and animations smooth.
         */
        databaseExecutor.execute(() -> noteDao.insert(note));
    }

    public void delete(Note note) {
        databaseExecutor.execute(() -> noteDao.delete(note));
    }

    public void deleteAllNotes() {
        databaseExecutor.execute(noteDao::deleteAllNotes);
    }

    public LiveData<List<Note>> getAllNotes() {
        /*
         * Reading this LiveData is cheap.
         * Room manages query execution and re-emission internally.
         */
        return allNotes;
    }
}
