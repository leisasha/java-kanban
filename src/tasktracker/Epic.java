package tasktracker;

import java.util.ArrayList;

public class Epic extends Task{
    private final ArrayList<Integer> subtasksId = new ArrayList<>();

    //constructors
    public Epic() {
    }
    public Epic(String name) {
        super(name);
    }
    public Epic(String name, String description) {
        super(name, description);
    }

    //methods
    protected void addSubtask(int subtaskId) {
        subtasksId.add(subtaskId);
    }

    protected ArrayList<Integer> getSubtasksId() {
        return subtasksId;
    }
}
