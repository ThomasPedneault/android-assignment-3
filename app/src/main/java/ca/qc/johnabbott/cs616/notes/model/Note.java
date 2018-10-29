package ca.qc.johnabbott.cs616.notes.model;

import java.util.Date;
import java.util.Objects;

import ca.qc.johnabbott.cs616.notes.sqlite.Identifiable;

/**
 * Represent a single note in the "Notes" app.
 * @author Ian Clement (ian.clement@johnabbott.qc.ca)
 */
public class Note implements Identifiable<Long> {

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

}
