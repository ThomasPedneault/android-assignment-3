package ca.qc.johnabbott.cs616.notes.ui.list;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.List;

import ca.qc.johnabbott.cs616.notes.R;
import ca.qc.johnabbott.cs616.notes.model.Note;
import ca.qc.johnabbott.cs616.notes.model.NoteDatabaseHandler;
import ca.qc.johnabbott.cs616.notes.sqlite.DatabaseException;

/**
 * A placeholder fragment containing a simple view.
 */
public class NoteListFragment extends Fragment {

    private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm";
    private static SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_PATTERN);


    private RecyclerView noteListRecyclerView;
    private NoteDatabaseHandler dbh;
    private NoteAdapter noteAdapter;

    public NoteListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_note_list, container, false);

        noteListRecyclerView = root.findViewById(R.id.noteList_RecyclerView);
        noteListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dbh = new NoteDatabaseHandler(getContext());
        refreshNoteRecyclerView();
        return root;
    }

    public void refreshNoteRecyclerView() {
        try {
            List<Note> allNotes = dbh.getNotesTable().readAll();
            noteAdapter = new NoteAdapter(allNotes);
            noteListRecyclerView.setAdapter(noteAdapter);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    private  class NoteViewHolder extends RecyclerView.ViewHolder {

        private final ConstraintLayout noteLayout;
        private final TextView noteTitleView;
        private final TextView noteBodyView;
        private final TextView noteReminderView;
        private final View root;
        private Note note;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView;

            noteLayout = itemView.findViewById(R.id.noteLayout_ConstraintLayout);
            noteTitleView = itemView.findViewById(R.id.noteTitle_TextView);
            noteBodyView = itemView.findViewById(R.id.noteBody_TextView);
            noteReminderView = itemView.findViewById(R.id.noteReminder_TextView);

            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), note.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        public void setNote(Note note) {
            this.note = note;
            this.noteLayout.setBackgroundColor(getContext().getColor(note.getCategory().getAndroidColorId()));
            this.noteTitleView.setText(note.getTitle());
            this.noteBodyView.setText(note.getBody());

            if(note.isHasReminder()) {
                this.noteReminderView.setText(format.format(note.getReminder()));
                this.noteReminderView.setVisibility(View.VISIBLE);
            } else {
                this.noteReminderView.setVisibility(View.GONE);
            }
        }
    }

    private class NoteAdapter extends RecyclerView.Adapter<NoteViewHolder> {

        private List<Note> data;

        public NoteAdapter(List<Note> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new NoteViewHolder(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.layout_note, parent, false)
            );
        }

        @Override
        public void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int position) {
            noteViewHolder.setNote(data.get(position));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }


    }

}
