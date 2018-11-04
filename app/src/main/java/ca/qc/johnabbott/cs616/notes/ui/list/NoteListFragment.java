package ca.qc.johnabbott.cs616.notes.ui.list;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.InvalidPropertiesFormatException;
import java.util.List;

import ca.qc.johnabbott.cs616.notes.R;
import ca.qc.johnabbott.cs616.notes.model.Category;
import ca.qc.johnabbott.cs616.notes.model.Note;
import ca.qc.johnabbott.cs616.notes.model.NoteDatabaseHandler;
import ca.qc.johnabbott.cs616.notes.sqlite.DatabaseException;
import ca.qc.johnabbott.cs616.notes.ui.editor.NoteEditActivity;

/**
 * A placeholder fragment containing a simple view.
 */
public class NoteListFragment extends Fragment {

    private static final int UPDATE_NOTE_CODE = 1;
    private static final int CREATE_NOTE_CODE = 2;
    private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm";
    private static SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_PATTERN);

    private RecyclerView noteListRecyclerView;
    private FloatingActionButton newNoteFab;

    private NoteDatabaseHandler dbh;
    private NoteAdapter noteAdapter;
    private Note previousNote;
    private String orderBy;

    public NoteListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_note_list, container, false);

        initSpinner(root);

        noteListRecyclerView = root.findViewById(R.id.noteList_RecyclerView);
        noteListRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        int margins = getResources().getDimensionPixelOffset(R.dimen.note_margin);
        noteListRecyclerView.addItemDecoration(new NoteItemDecoration(margins));

        newNoteFab = root.findViewById(R.id.newNote_Fab);
        newNoteFab.setOnClickListener(v -> {
            Note note = new Note().setCreated(new Date()).setCategory(Category.RED);
            try {
                dbh.getNotesTable().create(note);
                displayEditNoteFragment(note, CREATE_NOTE_CODE);
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
        });

        dbh = new NoteDatabaseHandler(getContext());
        refreshNoteRecyclerView();

        return root;
    }

    private void initSpinner(View root) {
        Spinner sortSpinner = root.findViewById(R.id.sort_Spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.note_spinner_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                orderBy = (String) parent.getAdapter().getItem(position);
                refreshNoteRecyclerView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    public void refreshNoteRecyclerView() {
        try {
            List<Note> allNotes = dbh.getNotesTable().readAll();
            allNotes = orderNotes(allNotes);
            noteAdapter = new NoteAdapter(allNotes);
            noteListRecyclerView.setAdapter(noteAdapter);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    private List<Note> orderNotes(List<Note> notes) {
        if(orderBy == null) {
            orderBy = "Title";
        }

        switch(orderBy) {
            case "Title":
                notes.sort(Comparator.comparing(Note::getTitle));
                return notes;
            case "Creation Date":
                Collections.sort(notes, new Comparator<Note>() {
                    @Override
                    public int compare(Note note, Note t1) {
                        return note.getCreated().compareTo(t1.getCreated());
                    }
                });
                return notes;
            case "Last Modified":
                Collections.sort(notes, new Comparator<Note>() {
                    @Override
                    public int compare(Note note, Note t1) {
                        return note.getModified().compareTo(t1.getModified());
                    }
                });
                return notes;
            case "Reminder":
                List<Note> withReminders = new ArrayList<>();
                List<Note> noReminders = new ArrayList<>();
                for(Note note : notes) {
                    if(note.isHasReminder())
                        withReminders.add(note);
                    else
                        noReminders.add(note);
                }
                Collections.sort(withReminders, new Comparator<Note>() {
                    @Override
                    public int compare(Note o1, Note o2) {
                        return o2.getReminder().compareTo(o1.getReminder());
                    }
                });
                notes.clear();
                notes.addAll(withReminders);
                notes.addAll(noReminders);
                return notes;
            case "Category":
                notes.sort(Comparator.comparing(Note::getCategory));
                return notes;
            default:
                return notes;
        }
    }

    public void displayEditNoteFragment(Note note, int code) {
        Context context = getContext();
        Intent intent = new Intent(context, NoteEditActivity.class);
        intent.putExtra("note", note);
        startActivityForResult(intent, code);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK) {
            return;
        }

        String message;
        if(requestCode == CREATE_NOTE_CODE) {
            message = "Created new note.";
        } else {
            message = "Updated existing note.";
        }

        try {
            Note newNote = data.getParcelableExtra("note");
            previousNote = dbh.getNotesTable().read(newNote.getId());
            dbh.getNotesTable().update(newNote);
            refreshNoteRecyclerView();
            Log.d("LIST_ACT", "Updated db");

            Snackbar sb = Snackbar.make(noteListRecyclerView, message, Snackbar.LENGTH_SHORT);
            sb.setAction("UNDO", v -> {
                try {
                    if(requestCode == CREATE_NOTE_CODE) {
                        dbh.getNotesTable().delete(newNote);
                        refreshNoteRecyclerView();
                    } else if(requestCode == UPDATE_NOTE_CODE) {
                        dbh.getNotesTable().update(previousNote);
                        refreshNoteRecyclerView();
                        previousNote = null;
                    }
                    Log.d("LIST_ACT", "Updated db");
                } catch (DatabaseException e) {
                    Log.d("LIST_ACT", "Failed to update db");
                    e.printStackTrace();
                }
            });
            sb.show();
        } catch (DatabaseException e) {
            Log.d("LIST_ACT", "Failed to update db");
            e.printStackTrace();
        }
    }

    private class NoteViewHolder extends RecyclerView.ViewHolder {

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
                    displayEditNoteFragment(note, UPDATE_NOTE_CODE);
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

    private class NoteItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public NoteItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.bottom = space;

            int position = parent.getChildLayoutPosition(view);

            // Add top margin only for the first item and second item to avoid double space between items
            if (position == 0 || position == 1) {
                outRect.top = space;
            } else {
                outRect.top = 0;
            }

            if (position % 2 == 0) {
                outRect.left = space;
                outRect.right = space / 2;
            } else {
                outRect.left = space / 2;
                outRect.right = space;
            }
        }

        private void log(String msg) {
            Log.d(getTag(), msg);
        }
    }

}
