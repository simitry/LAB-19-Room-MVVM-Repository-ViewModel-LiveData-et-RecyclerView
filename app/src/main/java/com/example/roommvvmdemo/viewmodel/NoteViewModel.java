package com.example.roommvvmdemo.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.roommvvmdemo.data.NoteRepository;
import com.example.roommvvmdemo.data.local.Note;

import java.util.List;

/**
 * ViewModel layer.
 *
 * The ViewModel is not a database class and not a View class.
 * Its job is to expose screen state and receive screen actions.
 */
public class NoteViewModel extends AndroidViewModel {

    private final NoteRepository repository;
    private final LiveData<List<Note>> allNotes;

    public NoteViewModel(@NonNull Application application) {
        super(application);

        /*
         * AndroidViewModel gives us Application.
         * We use Application instead of Activity to avoid leaking an Activity.
         */
        repository = new NoteRepository(application);
        allNotes = repository.getAllNotes();
    }

    public void insert(Note note) {
        repository.insert(note);
    }

    public void delete(Note note) {
        repository.delete(note);
    }

    public void deleteAllNotes() {
        repository.deleteAllNotes();
    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }
}
