package ca.qc.johnabbott.cs616.notes.ui.list;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
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
import ca.qc.johnabbott.cs616.notes.ui.editor.NoteEditActivity;

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
        noteListRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        int margins = getResources().getDimensionPixelOffset(R.dimen.note_margin);
        noteListRecyclerView.addItemDecoration(new NoteItemDecoration(margins));

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

    public void displayEditNoteFragment(Note note) {
        Context context = getContext();
        Intent intent = new Intent(context, NoteEditActivity.class);
        intent.putExtra("note", note);
        context.startActivity(intent);
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
                    displayEditNoteFragment(note);
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
    }

}
