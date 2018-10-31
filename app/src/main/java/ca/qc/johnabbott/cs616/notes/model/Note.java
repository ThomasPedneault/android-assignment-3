package ca.qc.johnabbott.cs616.notes.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.Objects;

import ca.qc.johnabbott.cs616.notes.sqlite.Identifiable;

/**
 * Represent a single note in the "Notes" app.
 * @author Ian Clement (ian.clement@johnabbott.qc.ca)
 */
public class Note implements Identifiable<Long>, Parcelable {

    // basic note elements
    private long id;
    private String title;
    private String body;
    private Category category;

    // reminders
    private boolean hasReminder;
    private Date reminder;

    // creation and modification times.
    private Date created;
    private Date modified;

    /**
     * Create a blank note.
     */
    public Note() {
        this(-1);
    }

    /**
     * Create a blank note with a specific ID.
     * @param id
     */
    public Note(long id) {
        this.id = id;
        this.category = Category.RED;
        this.created = new Date();
        this.modified = new Date();
    }

    /**
     * Create a note.
     * @param id
     * @param title
     * @param body
     * @param category
     * @param hasReminder
     * @param reminder
     * @param created
     * @param modified
     */
    public Note(long id, String title, String body, Category category, boolean hasReminder, Date reminder, Date created, Date modified) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.category = category;
        this.hasReminder = hasReminder;
        this.reminder = reminder;
        this.created = created;
        this.modified = modified;
    }

    protected Note(Parcel source) {
        this.id = source.readLong();
        this.title = source.readString();
        this.body = source.readString();
        this.category = Category.values()[source.readInt() - 1];

        // Verify if the created long value is -1 (no date set).
        long createdLong = source.readLong();
        this.created = createdLong != -1 ? new Date(createdLong) : new Date();

        this.hasReminder = source.readByte() == (byte) 1;

        // Verify if the reminder long value is -1 (no date set).
        long reminderLong = source.readLong();
        this.reminder = reminderLong != -1 ? new Date(reminderLong) : null;

        // Verify if the modified long value is -1 (no date set).
        long modifiedLong = source.readLong();
        this.modified = modifiedLong != -1 ? new Date(modifiedLong) : new Date();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public Note setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getBody() {
        return body;
    }

    public Note setBody(String body) {
        this.body = body;
        return this;
    }

    public Category getCategory() {
        return category;
    }

    public Note setCategory(Category category) {
        this.category = category;
        return this;
    }

    public boolean isHasReminder() {
        return hasReminder;
    }

    public Note setHasReminder(boolean hasReminder) {
        this.hasReminder = hasReminder;
        return this;
    }

    public Date getReminder() {
        return reminder;
    }

    public Note setReminder(Date reminder) {
        this.reminder = reminder;
        return this;
    }

    public Date getCreated() {
        return created;
    }

    public Note setCreated(Date created) {
        this.created = created;
        return this;
    }

    public Date getModified() {
        return modified;
    }

    public Note setModified(Date modified) {
        this.modified = modified;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return id == note.id &&
                hasReminder == note.hasReminder &&
                Objects.equals(title, note.title) &&
                Objects.equals(body, note.body) &&
                category == note.category &&
                Objects.equals(reminder, note.reminder) &&
                Objects.equals(created, note.created) &&
                Objects.equals(modified, note.modified);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, body, category, hasReminder, reminder, created, modified);
    }

    /**
     * Create a duplicate (aka clone) of the note.
     * @return
     */
    public Note clone() {
        Note clone = new Note();
        clone.id = this.id;
        clone.title = this.title;
        clone.body = this.body;
        clone.category = this.category;
        clone.created = this.created;
        clone.hasReminder = this.hasReminder;
        clone.reminder = this.reminder;
        clone.modified = this.modified;
        return clone;
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", category=" + category +
                ", hasReminder=" + hasReminder +
                ", reminder=" + reminder +
                ", created=" + created +
                ", modified=" + modified +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(body);
        dest.writeInt(category.getInternalColorId());
        // If the creation date is null, set it to -1.
        if(created == null)
            dest.writeLong(-1);
        else
            dest.writeLong(created.getTime());

        dest.writeByte((byte) (hasReminder ? 1 : 0));

        // If the reminder date is null, set it to -1.
        if(reminder == null)
            dest.writeLong(-1);
        else
            dest.writeLong(reminder.getTime());

        // If the modified date is null, set it to -1.
        if(modified == null)
            dest.writeLong(-1);
        else
            dest.writeLong(modified.getTime());
    }

    public static final Parcelable.Creator<Note> CREATOR = new Parcelable.Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel source) {
            return new Note(source);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };
}
