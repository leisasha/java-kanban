package models;

import java.util.List;
import java.util.ArrayList;

public class Epic extends Task {
    private List<Integer> subtasksId = new ArrayList<>();

    //constructors
    public Epic() {
    }

    public Epic(String name) {
        super(name);
    }

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(String name, String description, Status status) {
        super(name, description, status);
    }

    //get and set
    public List<Integer> getSubtasksId() {
        return subtasksId;
    }

    public void setSubtasksId(List<Integer> subtasksId) {
        this.subtasksId = subtasksId;
    }
}
