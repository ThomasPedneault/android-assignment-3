package ca.qc.johnabbott.cs616.notes.ui.editor;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ca.qc.johnabbott.cs616.notes.R;
import ca.qc.johnabbott.cs616.notes.model.Category;
import ca.qc.johnabbott.cs616.notes.model.Note;
import ca.qc.johnabbott.cs616.notes.model.NoteDatabaseHandler;
import ca.qc.johnabbott.cs616.notes.model.User;
import ca.qc.johnabbott.cs616.notes.sqlite.DatabaseException;
import ca.qc.johnabbott.cs616.notes.ui.util.AddCollaboratorDialogFragment;
import ca.qc.johnabbott.cs616.notes.ui.util.CircleView;
import ca.qc.johnabbott.cs616.notes.ui.util.DatePickerDialogFragment;
import ca.qc.johnabbott.cs616.notes.ui.util.TimePickerDialogFragment;

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
    private TextView reminderTextView;
    private ConstraintLayout parentLayout;
    private LinearLayout optionsLinearLayout;
    private Switch showOptionsSwitch;

    // root view
    private View root;
    private ImageView addCollaboratorImageView;

    // collaborators
    private List<User> collaborators;
    private RecyclerView collaboratorsRecyclerView;
    private NoteDatabaseHandler dbh;


    public NoteEditFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_notes, container, false);

        // UI references
        titleEditText = root.findViewById(R.id.title_EditText);
        bodyEditText = root.findViewById(R.id.body_EditText);
        reminderTextView = root.findViewById(R.id.reminder_TextView);
        parentLayout = root.findViewById(R.id.main_ConstraintLayout);

        // Set the event listeners for all views.
        setOptionOnCheckedListener();
        setReminderOnClickListener();

        // Set the OnClick listeners for the CircleView objects.
        setCircleViewOnClickListener(R.id.red_circleView, Category.RED);
        setCircleViewOnClickListener(R.id.orange_circleView, Category.ORANGE);
        setCircleViewOnClickListener(R.id.yellow_circleView, Category.YELLOW);
        setCircleViewOnClickListener(R.id.green_circleView, Category.GREEN);
        setCircleViewOnClickListener(R.id.lightBlue_circleView, Category.LIGHT_BLUE);
        setCircleViewOnClickListener(R.id.darkBlue_circleView, Category.DARK_BLUE);
        setCircleViewOnClickListener(R.id.purple_circleView, Category.PURPLE);
        setCircleViewOnClickListener(R.id.white_circleView, Category.BROWN);

        dbh = new NoteDatabaseHandler(getContext());

        // initialize collaborator
        addCollaboratorImageView = root.findViewById(R.id.addCollaborator_ImageView);
        addCollaboratorImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCollaboratorDialogFragment dialog;
                try {
                    List<User> usersTable = dbh.getUsersTable().readAll();
                    usersTable.removeAll(collaborators);
                    dialog = new AddCollaboratorDialogFragment(usersTable);
                    dialog.show(getFragmentManager(), "addCollaborator");
                } catch (DatabaseException e) {
                    Log.d("COLLAB-DIALOG", "Failed to display dialog.");
                }
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
        setNote(getActivity().getIntent().getParcelableExtra("note"));

        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("FRAG", "FRAGMENT DESTROYED");

        Intent returnIntent = new Intent();
        returnIntent.putExtra("note", getNote());
        getActivity().setResult(Activity.RESULT_OK, returnIntent);
    }

    public class CollaboratorViewHolder extends RecyclerView.ViewHolder {
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
        String title = "Default Note Title";
        if(!titleEditText.getText().toString().equals("")) {
            title = titleEditText.getText().toString();
        }
        Note note = new Note()
                    .setTitle(title)
                    .setBody(bodyEditText.getText().toString())
                    .setCategory(currentNote.getCategory());

        note.setId(currentNote.getId());

        try {
            note.setReminder(DATE_FORMAT.parse(reminderTextView.getText().toString()));
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
            reminderTextView.setText(DATE_FORMAT.format(note.getReminder()));
        else
            reminderTextView.setText("Enter a reminder...");
        setBackgroundColor(note.getCategory());
    }

    private void setOptionOnCheckedListener() {
        optionsLinearLayout = root.findViewById(R.id.options_LinearLayout);
        showOptionsSwitch = root.findViewById(R.id.showOptions_Switch);
        showOptionsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    optionsLinearLayout.setVisibility(View.VISIBLE);
                }
                else {
                    optionsLinearLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setReminderOnClickListener() {
        reminderTextView = root.findViewById(R.id.reminder_TextView);
        reminderTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();

                final TimePickerDialogFragment timePickerDialogFragment = TimePickerDialogFragment.create(
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);

                                Date currentReminder = calendar.getTime();
                                reminderTextView.setText(DATE_FORMAT.format(currentReminder));
                            }
                        }
                );

                DatePickerDialogFragment datePickerDialogFragment;
                datePickerDialogFragment = DatePickerDialogFragment.create(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, month);
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                timePickerDialogFragment.show(getFragmentManager(), "timePicker");
                            }
                        }
                );

                datePickerDialogFragment.show(getFragmentManager(), "datePicker");
            }
        });
    }

    private void setCircleViewOnClickListener(int id, final Category category) {
        final CircleView circleView = root.findViewById(id);
        circleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackgroundColor(category);
            }
        });
    }

    private void setBackgroundColor(Category category) {
        parentLayout.setBackgroundColor(getContext().getColor(category.getAndroidColorId()));
        currentNote.setCategory(category);
    }

}
