package com.example.roommvvmdemo.data.local;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity layer.
 *
 * This class describes the shape of one row in the SQLite table.
 * Room reads these annotations and generates the SQL mapping code for us.
 */
@Entity(tableName = "notes_table")
public class Note {

    /*
     * id is the database identity of the note.
     *
     * autoGenerate=true means:
     * - MainActivity does not choose the id;
     * - Repository does not choose the id;
     * - SQLite/Room creates it when the note is inserted.
     */
    @PrimaryKey(autoGenerate = true)
    private int id;

    /*
     * These fields become table columns.
     * For a larger app we could add createdAt, updatedAt, priority, archived, etc.
     */
    private final String title;
    private final String description;

    /*
     * The constructor intentionally does not receive id.
     * A new Note has content first; the database gives it an id later.
     */
    public Note(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    /*
     * Room needs this setter because id is generated after insertion.
     */
    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
