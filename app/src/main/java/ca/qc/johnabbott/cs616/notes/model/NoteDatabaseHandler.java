package ca.qc.johnabbott.cs616.notes.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import ca.qc.johnabbott.cs616.notes.sqlite.Table;
import ca.qc.johnabbott.cs616.notes.sqlite.TableFactory;

/**
 * Notes database
 */
public class NoteDatabaseHandler extends SQLiteOpenHelper {

    /**
     * Filename to store the local database (on device).
     */
    private static final String DATABASE_FILE_NAME = "notes.db";

    /**
     * Update this field for every structural change to the database.
     */
    private static final int DATABASE_VERSION = 3;

    private Context context;

    /*  NoteDatabaseHandler Tables */
    private Table<Note> notesTable;
    private Table<User> usersTable;
    private Table<Collaborator> collaboratorsTable;

    /**
     * Construct a new database handler.
     * @param context The application context.
     */
    public NoteDatabaseHandler(Context context) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION);

        usersTable = TableFactory.makeFactory(this, User.class)
                .getTable();

        notesTable = TableFactory.makeFactory(this, Note.class)
                                .setSeedData(SampleData.generateNotes())
                                .getTable();

        collaboratorsTable = TableFactory.makeFactory(this, Collaborator.class)
                                         .getTable();

        this.context = context;
    }

    /**
     * Get the Category table.
     * @return The Category table.
     */
    /*public CategoryTable getCategoryTable() {
        return categoryTable;
    }*/

    @Override
    public void onCreate(SQLiteDatabase database) {
        //database.execSQL(categoryTable.getCreateSQL());
        database.execSQL(notesTable.getCreateTableStatement());
        database.execSQL(usersTable.getCreateTableStatement());
        database.execSQL(collaboratorsTable.getCreateTableStatement());

        if(usersTable.hasInitialData())
            usersTable.initialize(database);
        if(notesTable.hasInitialData())
            notesTable.initialize(database);
        if(collaboratorsTable.hasInitialData())
            collaboratorsTable.initialize(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(NoteDatabaseHandler.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        database.execSQL(notesTable.getDropTableStatement());
        database.execSQL(usersTable.getDropTableStatement());
        database.execSQL(collaboratorsTable.getDropTableStatement());
        onCreate(database);
    }

    public Table<Note> getNotesTable() {
        return notesTable;
    }

    public Table<User> getUsersTable() {
        return usersTable;
    }

    public Table<Collaborator> getCollaboratorsTable() {
        return collaboratorsTable;
    }

    public Context getContext() {
        return context;
    }
}
