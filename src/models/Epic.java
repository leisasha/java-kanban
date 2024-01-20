package models;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasksId = new ArrayList<>();

    //constructors
    public Epic() {
    }
    public Epic(String name) {
        super(name);
    }
    public Epic(String name, String description) {
        super(name, description);
    }

    //get and set
    public ArrayList<Integer> getSubtasksId() {
        return subtasksId;
    }
    public void setSubtasksId(ArrayList<Integer> subtasksId) {
        this.subtasksId = subtasksId;
    }
}
