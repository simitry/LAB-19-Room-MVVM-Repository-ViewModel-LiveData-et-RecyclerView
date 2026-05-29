package com.example.roommvvmdemo.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roommvvmdemo.R;
import com.example.roommvvmdemo.data.local.Note;

/**
 * RecyclerView adapter layer.
 *
 * I am using ListAdapter instead of a plain Adapter + notifyDataSetChanged().
 * That is a little more advanced, but it teaches a better habit:
 * RecyclerView only redraws rows that actually changed.
 */
public class NoteAdapter extends ListAdapter<Note, NoteAdapter.NoteHolder> {

    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;

    public NoteAdapter() {
        super(DIFF_CALLBACK);
    }

    /*
     * DiffUtil tells RecyclerView how to compare old and new lists.
     *
     * areItemsTheSame:
     *   "Is this the same database row?"
     *
     * areContentsTheSame:
     *   "If it is the same row, did the visible content change?"
     */
    private static final DiffUtil.ItemCallback<Note> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Note>() {
                @Override
                public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
                    return oldItem.getTitle().equals(newItem.getTitle())
                            && oldItem.getDescription().equals(newItem.getDescription());
                }
            };

    public interface OnItemClickListener {
        void onItemClick(Note note);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(Note note);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        clickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        longClickListener = listener;
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /*
         * Inflate one row layout.
         * parent is the RecyclerView.
         * attachToRoot=false because RecyclerView attaches rows itself.
         */
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_item, parent, false);
        return new NoteHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        Note currentNote = getItem(position);
        holder.bind(currentNote);
    }

    class NoteHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final TextView tvDescription;

        NoteHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);

            itemView.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if (clickListener != null && position != RecyclerView.NO_POSITION) {
                    clickListener.onItemClick(getItem(position));
                }
            });

            itemView.setOnLongClickListener(view -> {
                int position = getBindingAdapterPosition();
                if (longClickListener != null && position != RecyclerView.NO_POSITION) {
                    longClickListener.onItemLongClick(getItem(position));
                    return true;
                }
                return false;
            });
        }

        void bind(Note note) {
            tvTitle.setText(note.getTitle());
            tvDescription.setText(note.getDescription());
        }
    }
}
