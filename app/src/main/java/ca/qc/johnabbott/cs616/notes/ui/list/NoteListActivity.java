package ca.qc.johnabbott.cs616.notes.ui.list;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import ca.qc.johnabbott.cs616.notes.R;
import ca.qc.johnabbott.cs616.notes.model.Note;
import ca.qc.johnabbott.cs616.notes.ui.editor.NoteEditActivity;

public class NoteListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void displayEditNoteFragment(Note note, int code) {
        Context context = getApplicationContext();
        Intent intent = new Intent(context, NoteEditActivity.class);
        intent.putExtra("note", note);
        startActivityForResult(intent, code);
    }

}
