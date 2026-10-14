package ics.model;

/**
 * Root of every record that is stored in a txt file.
 * It owns the ID and forces each subclass to say how it becomes one line of text.
 * Because of this, one generic Repository<T extends Entity> can store anything.
 */
public abstract class Entity {

    private String id;   // e.g. CUS-0001. Null until a repository assigns it.

    protected Entity(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    /** Called by Repository.add(). An ID can be given only once. */
    public void setId(String id) {
        if (this.id != null && !this.id.isBlank()) {
            throw new IllegalStateException("ID is already set to " + this.id);
        }
        this.id = id;
    }

    /** One record becomes one text line (see ics.util.FileCodec). */
    public abstract String toFileLine();
}
