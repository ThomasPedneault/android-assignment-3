package ca.qc.johnabbott.cs616.notes.ui.editor;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ca.qc.johnabbott.cs616.notes.R;
import ca.qc.johnabbott.cs616.notes.model.Category;
import ca.qc.johnabbott.cs616.notes.model.Note;
import ca.qc.johnabbott.cs616.notes.model.User;

/**
 * Note editor with limited features.
 */
public class NoteEditFragment extends Fragment {

    private static final String DATE_FORMAT_STRING = "EEEE, MMMM d 'at' h:mm a";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_STRING);

    // tracks changes in the note
    private Note currentNote;

    // UI Components references
    private EditText titleEditText;
    private EditText bodyEditText;
    private EditText reminderEditText;
    private EditText categoryEditText;

    // root view
    private View root;
    private ImageView addCollaboratorImageView;

    // collaborators
    private List<User> collaborators;
    private RecyclerView collaboratorsRecyclerView;

    public NoteEditFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_notes, container, false);

        // UI references
        titleEditText = root.findViewById(R.id.noteTitle_EditText);
        bodyEditText = root.findViewById(R.id.noteBody_EditText);
        reminderEditText = root.findViewById(R.id.reminder_EditText);
        categoryEditText = root.findViewById(R.id.category_EditText);

        // initialize collaborator
        addCollaboratorImageView = root.findViewById(R.id.addCollabortor_ImageView);
        addCollaboratorImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Garply", Toast.LENGTH_SHORT).show();
            }
        });

        collaborators = new ArrayList<>();

        collaboratorsRecyclerView = root.findViewById(R.id.collaborator_RecyclerView);
        collaboratorsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, true));
        collaboratorsRecyclerView.setHasFixedSize(true);
        collaboratorsRecyclerView.setAdapter(new RecyclerView.Adapter<CollaboratorViewHolder>() {

            @NonNull
            @Override
            public CollaboratorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new CollaboratorViewHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.list_item_collaborator_avatar_only, parent, false)
                );
            }

            @Override
            public void onBindViewHolder(@NonNull CollaboratorViewHolder holder, int position) {
                holder.setCollaborator(collaborators.get(position));
            }

            @Override
            public int getItemCount() {
                return collaborators.size();
            }
        });


        // initialize the note
        Note note = getActivity().getIntent().getParcelableExtra("note");
        setNote(note);

        return root;
    }

    private class CollaboratorViewHolder extends RecyclerView.ViewHolder {

        private final ImageView avatarImageView;

        public CollaboratorViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarImageView = itemView.findViewById(R.id.avatar_ImageView);
        }

        public void setCollaborator(User user) {
            avatarImageView.setImageBitmap(user.getAvatar());
        }
    }

    public Note getNote() {
        Note note = new Note()
                    .setTitle(titleEditText.getText().toString())
                    .setBody(bodyEditText.getText().toString())
                    .setCategory(Category.valueOf(categoryEditText.getText().toString()));
        try {
            note.setReminder(DATE_FORMAT.parse(reminderEditText.getText().toString()));
            note.setHasReminder(true);
        } catch (ParseException e) {
            note.setHasReminder(false);
        }
        note.setModified(new Date());
        return note;
    }

    public void setNote(Note note) {
        this.currentNote = note;
        titleEditText.setText(note.getTitle());
        bodyEditText.setText(note.getBody());
        if(note.isHasReminder())
            reminderEditText.setText(DATE_FORMAT.format(note.getReminder()));
        else
            reminderEditText.setHint(reminderEditText.getHint().toString() + " (" + DATE_FORMAT_STRING + ")");
        categoryEditText.setText(note.getCategory().toString());
    }

}
