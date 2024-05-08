package models;

import java.util.Objects;

public class Task {
    private int id;
    private String name;
    private String description;
    private Status status = Status.NEW;

    public Task() {
    }

    public Task(String name) {
        this.name = name;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Task(String name, Status status) {
        this.name = name;
        this.status = status;
    }

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        String descriptionPerformance;
        if (description != null) {
            descriptionPerformance = ", description.length=" + Integer.toString(description.length());
        } else {
            descriptionPerformance = ", description=null";
        }

        return getClass().getSimpleName() + "{" +
                "id=" + id +
                ", name='" + name + "'" +
                descriptionPerformance +
                ", status=" + status +
                '}';
    }
}
