package ca.qc.johnabbott.cs616.notes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ca.qc.johnabbott.cs616.notes.model.Category;
import ca.qc.johnabbott.cs616.notes.model.Note;
import ca.qc.johnabbott.cs616.notes.model.NoteDatabaseHandler;
import ca.qc.johnabbott.cs616.notes.model.SampleData;
import ca.qc.johnabbott.cs616.notes.sqlite.DatabaseException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.fail;

/**
 * Unit tests for DB
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseTest {

    private List<Note> seedData;
    private NoteDatabaseHandler dbh;

    @Before
    public void setUp() throws DatabaseException {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        dbh = new NoteDatabaseHandler(appContext);
        tearDown();

        // insert all seed data.
        seedData = SampleData.getData(appContext).getFirst();
        for(Note n : seedData)
            dbh.getNotesTable().create(n);
    }

    @After
    public void tearDown() {
        // delete all rows from the database.
        SQLiteDatabase database = dbh.getWritableDatabase();
        database.delete(dbh.getNotesTable().getName(), "", new String[]{});
    }

    @Test
    public void testCreateSetsIds() {
        // check that all notes in seed data now have DB ids.
        for(Note n : seedData)
            assertTrue(n.getId() >= 0);
    }

    @Test
    public void testCreate() throws DatabaseException {
        // check that one of the seed data items is in the DB
        Note note = dbh.getNotesTable().read(seedData.get(0).getId());
        assertEquals(seedData.get(0), note);
    }

    @Test
    public void testDateFormat() throws DatabaseException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.ZONE_OFFSET, 10);
        calendar.set(Calendar.MILLISECOND, 321);
        Note note = new Note()
                        .setBody("Foo")
                        .setTitle("Bar")
                        .setCategory(Category.PURPLE)
                        .setHasReminder(false)
                        .setCreated(calendar.getTime())
                        .setModified(new Date());
        dbh.getNotesTable().create(note);

        Note test = dbh.getNotesTable().read(note.getId());

        Calendar result = Calendar.getInstance();
        result.setTime(test.getCreated());
        assertEquals(10, result.get(Calendar.ZONE_OFFSET));
        assertEquals(321, result.get(Calendar.MILLISECOND));

    }

    @Test(expected = DatabaseException.class)
    public void testCreateTitleNull() throws DatabaseException {
        // create note with no title
        Note test = new Note()
                        .setBody("Foo")
                        .setCategory(Category.PURPLE)
                        .setHasReminder(false)
                        .setCreated(new Date())
                        .setModified(new Date());
        dbh.getNotesTable().create(test);
    }

    @Test(expected = DatabaseException.class)
    public void testCreateCreatedNull() throws DatabaseException {
        // create note with no created date
        Note test = new Note()
                .setTitle("Bar")
                .setBody("Foo")
                .setCategory(Category.PURPLE)
                .setHasReminder(false)
                .setModified(new Date());
        dbh.getNotesTable().create(test);
    }

    @Test(expected = DatabaseException.class)
    public void testCreateModifiedNull() throws DatabaseException {
        // create note with no created date
        Note test = new Note()
                .setTitle("Bar")
                .setBody("Foo")
                .setCategory(Category.PURPLE)
                .setHasReminder(false)
                .setCreated(new Date());
        dbh.getNotesTable().create(test);
    }

    @Test(expected = DatabaseException.class)
    public void testCreatedBeforeModified() throws DatabaseException {
        Note test = new Note()
                .setBody("Foo")
                .setCategory(Category.PURPLE)
                .setHasReminder(false)
                .setCreated(new Date());
        test.setModified(new Date(test.getCreated().getTime() - 1000));
        dbh.getNotesTable().create(test);
    }

    @Test
    public void testRead() throws DatabaseException {
        // read one of the seed data notes
        Note note = dbh.getNotesTable().read(seedData.get(0).getId());
        assertEquals(seedData.get(0), note);
    }

    @Test
    public void testReadAll() throws DatabaseException {
        List<Note> data = dbh.getNotesTable().readAll();
        assertTrue(data.containsAll(seedData) && seedData.containsAll(data));
    }

    @Test
    public void testUpdate() throws DatabaseException {

        // make edits to a note.
        Note note = seedData.get(0);
        note.setTitle("Foo")
                .setBody("Bar")
                .setHasReminder(true)
                .setReminder(new Date())
                .setCategory(Category.PURPLE)
                .setModified(new Date());

        // update in DB
        assertTrue(dbh.getNotesTable().update(note));

        // read it back as a copy and test that it's identical to the above.
        Note test = dbh.getNotesTable().read(note.getId());
        assertEquals(note, test);
    }

    @Test(expected = DatabaseException.class)
    public void testDelete() throws DatabaseException {
        try {
            // delete from db and check that it's gone.
            assertTrue(dbh.getNotesTable().delete(seedData.get(0)));
        }
        catch (DatabaseException e) {
            fail("Failed to delete note.");
        }
        // this line should throw as DatabaseException.
        dbh.getNotesTable().read(seedData.get(0).getId());
    }

}

