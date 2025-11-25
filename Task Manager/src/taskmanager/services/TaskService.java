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

    public Task createTask(String id, String title, String description, Category category, String assigneeId, LocalDate dueDate, String eisenhower)
    {
        Person assignee = null;
        if (assigneeId != null)
        {
            assignee = personRepo.findById(assigneeId);
            if (assignee == null)
            {
                throw new NotFoundException("Person with ID " + assigneeId + " doesn't exist.");
            }
        }

        Task task = new Task(id, title, description, category, Status.TO_DO, assignee, dueDate, eisenhower);

        taskRepo.add(task);
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
        String catToken = parts[0].substring(1).toUpperCase();
        String title = parts[1];

        Category category;
        try
        {
            category = Category.valueOf(catToken);
        }
        catch (Exception e)
        {
            throw new InvalidDataException("Invalid category: " + catToken);
        }

        String id = UUID.randomUUID().toString();

        Task task = new Task(id, title, "", category, Status.TO_DO, null, null, null);
        taskRepo.add(task);
        return task;
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
        task.setAssignee(assignee);
        task.setDueDate(dueDate);
        task.setEisenhower(eisenhower);
        task.updateStatus(status);

        return task;
    }

    public void deleteTask(String id)
    {
        boolean result = taskRepo.delete(id);
        if (!result)
        {
            throw new NotFoundException("No task with ID " + id + " for deletion.");
        }
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
}