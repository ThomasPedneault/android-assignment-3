package ca.qc.johnabbott.cs616.notes.sqlite;

/**
 * Indicated model classes that have an ID field.
 *
 * @param <I>
 *
 * @author Ian Clement (ian.clement@johnabbott.qc.ca)
 */
public interface Identifiable<I> {
    I getId();
    void setId(I id);
}
