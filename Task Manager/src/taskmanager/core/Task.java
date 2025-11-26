package taskmanager.core;

import java.time.LocalDate;

public class Task {
    private String id;
    private String title;
    private String description;
    private Category category;
    private Status status;
    private Person assignee;
    private LocalDate dueDate;
    private String eisenhower;

    // Minimal constructor
    public Task(String id, String title, Category category, Person assignee) {
        if (id == null || id.trim().isEmpty()) throw new IllegalArgumentException("ID cannot be empty");
        if (title == null || title.trim().isEmpty()) throw new IllegalArgumentException("Title cannot be empty");
        if (category == null) throw new IllegalArgumentException("Category cannot be null");
        if (assignee == null) throw new IllegalArgumentException("Assignee cannot be null");

        this.id = id;
        this.title = title;
        this.category = category;
        this.assignee = assignee;
        this.status = Status.TO_DO; // Default status
        this.description = "";
        this.eisenhower = "I"; // Default or null? Requirements say values: "I", "II", "III", "IV". Let's default to I or handle null.
    }

    // Full constructor
    public Task(String id, String title, String description, Category category, Status status, Person assignee, LocalDate dueDate, String eisenhower) {
        if (id == null || id.trim().isEmpty()) throw new IllegalArgumentException("ID cannot be empty");
        if (title == null || title.trim().isEmpty()) throw new IllegalArgumentException("Title cannot be empty");
        if (category == null) throw new IllegalArgumentException("Category cannot be null");
        if (status == null) throw new IllegalArgumentException("Status cannot be null");
        if (assignee == null) throw new IllegalArgumentException("Assignee cannot be null");
        
        if (dueDate != null && dueDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Due date cannot be in the past");
        }
        
        if (eisenhower != null && !eisenhower.matches("I|II|III|IV")) {
            throw new IllegalArgumentException("Eisenhower must be I, II, III, or IV");
        }

        this.id = id;
        this.title = title;
        this.description = description == null ? "" : description;
        this.category = category;
        this.status = status;
        this.assignee = assignee;
        this.dueDate = dueDate;
        this.eisenhower = eisenhower;
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Category getCategory() { return category; }
    public Status getStatus() { return status; }
    public Person getAssignee() { return assignee; }
    public LocalDate getDueDate() { return dueDate; }
    public String getEisenhower() { return eisenhower; }

    // Setters
    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) throw new IllegalArgumentException("Title cannot be empty");
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description == null ? "" : description;
    }

    public void setCategory(Category category) {
        if (category == null) throw new IllegalArgumentException("Category cannot be null");
        this.category = category;
    }

    public void updateStatus(Status status) {
        if (status == null) throw new IllegalArgumentException("Status cannot be null");
        this.status = status;
    }

    public void assignPerson(Person assignee) {
        if (assignee == null) throw new IllegalArgumentException("Assignee cannot be null");
        this.assignee = assignee;
    }

    public void setDueDate(LocalDate dueDate) {
        if (dueDate != null && dueDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Due date cannot be in the past");
        }
        this.dueDate = dueDate;
    }

    public void setEisenhower(String eisenhower) {
        if (eisenhower != null && !eisenhower.matches("I|II|III|IV")) {
            throw new IllegalArgumentException("Eisenhower must be I, II, III, or IV");
        }
        this.eisenhower = eisenhower;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("--------------------------------------------------\n");
        sb.append(String.format("Task ID: %s | Status: %s | Category: %s\n", id, status, category));
        sb.append(String.format("Title: %s\n", title));
        sb.append(String.format("Assignee: %s\n", assignee.getName()));
        if (dueDate != null) {
            java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("dd MM yyyy");
            sb.append(String.format("Due Date: %s\n", dueDate.format(fmt)));
        }
        if (eisenhower != null) sb.append(String.format("Priority: %s\n", eisenhower));
        if (description != null && !description.isEmpty()) sb.append(String.format("Description: %s\n", description));
        sb.append("--------------------------------------------------");
        return sb.toString();
    }


}
