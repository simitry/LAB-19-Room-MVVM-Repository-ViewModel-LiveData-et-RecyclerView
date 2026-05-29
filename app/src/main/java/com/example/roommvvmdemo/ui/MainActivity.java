package com.example.roommvvmdemo.ui;

import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roommvvmdemo.R;
import com.example.roommvvmdemo.data.local.Note;
import com.example.roommvvmdemo.viewmodel.NoteViewModel;

/**
 * View layer.
 *
 * MainActivity owns screen widgets and user interactions.
 * It does not know SQL. It does not know ExecutorService. It does not know how
 * Room stores data. That is the whole point of MVVM in this lab.
 */
public class MainActivity extends ComponentActivity {

    private NoteViewModel noteViewModel;

    private EditText etTitle;
    private EditText etDescription;
    private Button btnAdd;
    private Button btnDeleteAll;
    private NoteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
         * The Activity can be destroyed and recreated by rotation.
         * The ViewModel can survive that recreation.
         * The Room database persists even if the app is closed.
         */
        setContentView(R.layout.activity_main);

        findViews();
        setupRecyclerView();
        setupViewModel();
        setupUserActions();
    }

    private void findViews() {
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        btnAdd = findViewById(R.id.btnAdd);
        btnDeleteAll = findViewById(R.id.btnDeleteAll);
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        /*
         * LinearLayoutManager means a normal vertical list.
         */
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        /*
         * setHasFixedSize(true) says the RecyclerView size itself does not
         * depend on the number/content of notes. This can help performance.
         */
        recyclerView.setHasFixedSize(true);

        adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void setupViewModel() {
        /*
         * ViewModelProvider returns the same NoteViewModel across rotation.
         * If this is the first creation, Android creates a new instance.
         */
        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);

        /*
         * LiveData observation is lifecycle-aware:
         * - active screen: observer receives notes;
         * - stopped/destroyed screen: no unsafe UI update.
         *
         * When Room emits a new List<Note>, ListAdapter calculates the difference
         * and updates only the necessary RecyclerView rows.
         */
        noteViewModel.getAllNotes().observe(this, notes -> adapter.submitList(notes));
    }

    private void setupUserActions() {
        btnAdd.setOnClickListener(view -> saveNoteFromInputs());

        btnDeleteAll.setOnClickListener(view -> {
            noteViewModel.deleteAllNotes();
            Toast.makeText(this, "Toutes les notes ont ete supprimees", Toast.LENGTH_SHORT).show();
        });

        adapter.setOnItemLongClickListener(note -> {
            noteViewModel.delete(note);
            Toast.makeText(this, "Note supprimee", Toast.LENGTH_SHORT).show();
        });

        adapter.setOnItemClickListener(note -> Toast.makeText(
                this,
                "Titre : " + note.getTitle(),
                Toast.LENGTH_SHORT
        ).show());
    }

    private void saveNoteFromInputs() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        /*
         * Small UI validation.
         * The database layer stays simple because this lab focuses on MVVM/Room.
         */
        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Remplir le titre et la description", Toast.LENGTH_SHORT).show();
            return;
        }

        Note note = new Note(title, description);

        /*
         * Flow from UI to database:
         * Activity -> ViewModel -> Repository -> DAO -> Room -> SQLite.
         */
        noteViewModel.insert(note);

        etTitle.setText("");
        etDescription.setText("");
        etTitle.requestFocus();
        hideKeyboard();

        Toast.makeText(this, "Note ajoutee", Toast.LENGTH_SHORT).show();
    }

    private void hideKeyboard() {
        /*
         * Purely ergonomic: after adding a note, hide the keyboard so the user
         * can immediately see the RecyclerView update.
         */
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (inputMethodManager != null && etDescription.getWindowToken() != null) {
            inputMethodManager.hideSoftInputFromWindow(etDescription.getWindowToken(), 0);
        }
    }
}
