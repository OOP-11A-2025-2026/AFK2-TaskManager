package taskmanager.core;

import java.time.LocalDate;

public class Task {
    private String id;
    private String title;
    private String description;
    //private Category category;
    //private Status status;
    //private Person assignee;
    private LocalDate dueDate;
    private String eisenhower;

    public Task(String id, String title/*, Category category, Person assignee*/) {
        if(id.trim().isEmpty() || id == null) {
            throw new IllegalArgumentException("id is null or empty");
        }
        if(title.trim().isEmpty() || title == null) {
            throw new IllegalArgumentException("title is null or empty");
        }

        this.id = id;
        this.title = title;
        /*
        this.category = category;
        this.assignee = assignee;
        */
    }

    public Task(String id, String title, String eisenhower, LocalDate dueDate, String description/*, Category category, Person assignee, Status status*/) {
        if(id.trim().isEmpty() || id == null) {
            throw new IllegalArgumentException("id is null or empty");
        }
        if(description.trim().isEmpty() || description == null) {
            throw new IllegalArgumentException("description is null or empty");
        }
        if(title.trim().isEmpty() || title == null) {
            throw new IllegalArgumentException("title is null or empty");
        }
        if(eisenhower.equals("I") || eisenhower.equals("II") || eisenhower.equals("III") || eisenhower.equals("IV")) {
            this.eisenhower = eisenhower;
        } else throw new IllegalArgumentException("eisenhower is illegal");

        if(dueDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("dueDate is before now");
        }

        this.description = description;
        this.id = id;
        this.title = title;
        this.dueDate = dueDate;
        /*
        if(category == null) {
            throw new IllegalArgumentException("category is null");
        }
        if(assignee == null) {
            throw new IllegalArgumentException("assignee is null");
        }
        if(status == null) {
            throw new IllegalArgumentException("status is null");
        }

        this.category = category;
        this.assignee = assignee;
        this.status = status;
        */
    }

    // Getters and Setters:
    public String getId() {
        return id;
    }


    public String getTitle() {
        return title;
    }
    protected void setTitle(String title) {
        if(title.trim().isEmpty() || title == null) {
            throw new IllegalArgumentException("title is null or empty");
        }
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    protected void setDescription(String description) {
        if(description.trim().isEmpty() || description == null) {
            throw new IllegalArgumentException("description is null or empty");
        }
        this.description = description;
    }
    public LocalDate getDueDate() {
        return dueDate;
    }
    protected void setDueDate(LocalDate dueDate) {
        if(dueDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("dueDate is before now");
        }
        this.dueDate = dueDate;
    }
    public String getEisenhower() {
        return eisenhower;
    }
    protected void setEisenhower(String eisenhower) {
        if(eisenhower.equals("I") || eisenhower.equals("II") || eisenhower.equals("III") || eisenhower.equals("IV")) {
            this.eisenhower = eisenhower;
        } else throw new IllegalArgumentException("eisenhower is illegal");
        this.eisenhower = eisenhower;
    }

    /*
    public void updateStatus(Status s) {
        if(status == null) {
            throw new IllegalArgumentException("status is null");
        }
        status = s;
    }

    public void assignPerson(Person p) {
        if(assignee == null) {
            throw new IllegalArgumentException("assignee is null");
        }
        assignee = p;
      }
     */


}
