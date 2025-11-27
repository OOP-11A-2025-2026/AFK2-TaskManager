package taskmanager.services;

import taskmanager.core.*;
import taskmanager.data.PersonRepository;
import taskmanager.data.TaskRepository;
import taskmanager.exceptions.InvalidDataException;
import taskmanager.exceptions.NotFoundException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class TaskService
{

    private final PersonRepository personRepo;
    private final TaskRepository taskRepo;

    public TaskService(PersonRepository personRepo, TaskRepository taskRepo)
    {
        this.personRepo = personRepo;
        this.taskRepo = taskRepo;
    }

    public Task createTask(String title, String description, Category category, String assigneeId, LocalDate dueDate, String eisenhower)
    {
        Person assignee = null;
        if (assigneeId != null)
        {
            assignee = personRepo.findById(assigneeId);
            if (assignee == null)
            {
                throw new NotFoundException("Person with ID " + assigneeId + " doesn't exist.");
            }
        } else {
             throw new InvalidDataException("Assignee ID is required.");
        }

        String id = UUID.randomUUID().toString().substring(0, 8); // Simple ID generation
        Task task = new Task(id, title, description, category, Status.TO_DO, assignee, dueDate, eisenhower);

        taskRepo.add(task);
        // persist
        try { taskRepo.saveAll(); } catch (Exception ignored) {}
        return task;
    }

    public Task createTaskShortcut(String shortcut)
    {
        if (shortcut == null || shortcut.isEmpty())
        {
            throw new InvalidDataException("Shortcut command is empty.");
        }
        if (!shortcut.startsWith("/"))
        {
            throw new InvalidDataException("Shortcut command must start with '/'.");
        }
        String[] parts = shortcut.trim().split(" ", 2);
        if (parts.length < 2)
        {
            throw new InvalidDataException("Shortcut must contain a title after the category.");
        }
        String catToken = parts[0].substring(1).toLowerCase();
        String title = parts[1];

        // map common shortcut names to Category enum
        Category category;
        switch (catToken) {
            case "bug":
            case "bug_fix":
            case "bugfix":
                category = Category.BUG_FIX;
                break;
            case "feature":
            case "feat":
                category = Category.FEATURE;
                break;
            case "refactor":
                category = Category.REFACTOR;
                break;
            case "doc":
            case "documentation":
                category = Category.DOCUMENTATION;
                break;
            case "other":
                category = Category.OTHER;
                break;
            default:
                try {
                    category = Category.valueOf(catToken.toUpperCase());
                } catch (Exception e) {
                    throw new InvalidDataException("Invalid category shortcut: " + catToken);
                }
        }

        String id = UUID.randomUUID().toString().substring(0, 8);
        
        // Assign to first available person for shortcut or throw if empty
        if (personRepo.getPeople().isEmpty()) {
             throw new InvalidDataException("No people available to assign task to.");
        }
        Person defaultAssignee = personRepo.getPeople().get(0);

        Task task = new Task(id, title, category, defaultAssignee);
        taskRepo.add(task);
        try { taskRepo.saveAll(); } catch (Exception ignored) {}
        return task;
    }

    // Expose people list for Console UI
    public List<Person> listPeople() {
        return Collections.unmodifiableList(personRepo.getPeople());
    }

    public boolean personExists(String id) {
        return personRepo.exists(id);
    }

    public List<Task> readAll() {return taskRepo.getAll();}

    public List<Task> readByAssignee(String personId)
    {
        if (!personRepo.exists(personId))
        {
            throw new NotFoundException("Person with ID " + personId + " is not found.");
        }

        return taskRepo.getByAssignee(personId);
    }

    public Task updateTask(String id, String title, String description, Category category, String assigneeId, LocalDate dueDate, String eisenhower, Status status)
    {
        Task task = taskRepo.findById(id);
        if (task == null)
        {
            throw new NotFoundException("Task with ID " + id + " is not found.");
        }

        Person assignee = null;
        if (assigneeId != null)
        {
            assignee = personRepo.findById(assigneeId);
            if (assignee == null)
            {
                throw new NotFoundException("Person with ID " + assigneeId + " doesn't exist.");
            }
        }

        task.setTitle(title);
        task.setDescription(description);
        task.setCategory(category);
        task.assignPerson(assignee);
        task.setDueDate(dueDate);
        task.setEisenhower(eisenhower);
        task.updateStatus(status);

        try { taskRepo.saveAll(); } catch (Exception ignored) {}
        return task;
    }

    public void deleteTask(String id)
    {
        boolean result = taskRepo.delete(id);
        if (!result)
        {
            throw new NotFoundException("No task with ID " + id + " for deletion.");
        }
        try { taskRepo.saveAll(); } catch (Exception ignored) {}
    }

    public List<Task> sortByDueDate()
    {
        return taskRepo.getAll().stream().sorted(Comparator.comparing(Task::getDueDate, Comparator.nullsLast(Comparator.naturalOrder()))).collect(Collectors.toList());
    }

    public List<Task> sortByCategory()
    {
        return taskRepo.getAll().stream().sorted(Comparator.comparing(Task::getCategory, Comparator.nullsLast(Comparator.naturalOrder()))).collect(Collectors.toList());
    }

    public List<Task> sortByEisenhower()
    {
        Map<String, Integer> priority = Map.of("I",   1, "II",  2, "III", 3, "IV",  4);
        return taskRepo.getAll().stream().sorted(Comparator.comparing(t -> priority.getOrDefault(t.getEisenhower(), 99))).collect(Collectors.toList());
    }

    public List<Task> search(String keyword)
    {
        if (keyword == null || keyword.isEmpty()) {return readAll();}
        String lower = keyword.toLowerCase();
        return taskRepo.getAll().stream().filter(t -> t.getTitle().toLowerCase().contains(lower) || t.getDescription().toLowerCase().contains(lower)).collect(Collectors.toList());
    }

    public boolean updateStatus(String id, Status status) {
        Task task = taskRepo.findById(id);
        if (task == null) throw new NotFoundException("Task with ID " + id + " is not found.");

        // If marking as DONE, remove the task from repository
        if (status == Status.DONE) {
            deleteTask(id);
            return true;
        }

        task.updateStatus(status);
        try { taskRepo.saveAll(); } catch (Exception ignored) {}
        return false;
    }

    public void updateDueDate(String id, LocalDate date) {
        Task task = taskRepo.findById(id);
        if (task == null) throw new NotFoundException("Task with ID " + id + " is not found.");
        task.setDueDate(date);
        try { taskRepo.saveAll(); } catch (Exception ignored) {}
    }

    public void updateEisenhower(String id, String eisenhower) {
        Task task = taskRepo.findById(id);
        if (task == null) throw new NotFoundException("Task with ID " + id + " is not found.");
        task.setEisenhower(eisenhower);
        try { taskRepo.saveAll(); } catch (Exception ignored) {}
    }

    public void updateTitle(String id, String title) {
        Task task = taskRepo.findById(id);
        if (task == null) throw new NotFoundException("Task with ID " + id + " is not found.");
        task.setTitle(title);
        try { taskRepo.saveAll(); } catch (Exception ignored) {}
    }

    public void updateDescription(String id, String description) {
        Task task = taskRepo.findById(id);
        if (task == null) throw new NotFoundException("Task with ID " + id + " is not found.");
        task.setDescription(description);
        try { taskRepo.saveAll(); } catch (Exception ignored) {}
    }

    public void updateCategory(String id, Category category) {
        Task task = taskRepo.findById(id);
        if (task == null) throw new NotFoundException("Task with ID " + id + " is not found.");
        task.setCategory(category);
        try { taskRepo.saveAll(); } catch (Exception ignored) {}
    }

    public void updateAssignee(String id, String assigneeId) {
        Task task = taskRepo.findById(id);
        if (task == null) throw new NotFoundException("Task with ID " + id + " is not found.");
        
        Person assignee = personRepo.findById(assigneeId);
        if (assignee == null) throw new NotFoundException("Person with ID " + assigneeId + " doesn't exist.");
        
        task.assignPerson(assignee);
        try { taskRepo.saveAll(); } catch (Exception ignored) {}
    }
}