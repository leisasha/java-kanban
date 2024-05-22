package models;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtasksId = new ArrayList<>();
    private ZonedDateTime endTime;

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

    public List<Integer> getSubtasksId() {
        return subtasksId;
    }

    public void setSubtasksId(List<Integer> subtasksId) {
        this.subtasksId = subtasksId;
    }

    @Override
    public ZonedDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(ZonedDateTime endTime) {
        this.endTime = endTime;
    }
}
