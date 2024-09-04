package BackEnd.Model;

public class Task<T> {
    private int id;
    private String name;
    private String description;
    private boolean isCompleted;
    private T category;

    public Task(String name, String description, T category) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.isCompleted = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        this.isCompleted = completed;
    }

    public T getCategory() {
        return category;
    }

    public void setCategory(T category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return name + " [" + category + "]";
    }
}
