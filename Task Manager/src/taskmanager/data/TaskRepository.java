package taskmanager.data;

import taskmanager.core.Task;
import taskmanager.exceptions.RepositoryException;
import taskmanager.core.Person;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class TaskRepository {
    private ArrayList<Task> tasks;
    private final PersonRepository personRepo;
    private final File storageFile;

    public TaskRepository(PersonRepository personRepo) {
        this.personRepo = personRepo;
        this.tasks = new ArrayList<>();
        this.storageFile = new File("tasks.txt");
        loadFromFile();
    }

    public TaskRepository(ArrayList<Task> tasks, PersonRepository personRepo) {
        this.tasks = tasks;
        this.personRepo = personRepo;
        this.storageFile = new File("tasks.txt");
    }

    public boolean exists(String id) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public void add(Task task) {
        if(!exists(task.getId())) {
            tasks.add(task);
            saveAll();
        } else throw new RepositoryException("Task already exists");
    }

    public ArrayList<Task> getAll() {
        return tasks;
    }

    public Task findById(String id) {
        for(int i = 0; i < tasks.size(); i++) {
            if(tasks.get(i).getId().equals(id)) {
                return tasks.get(i);
            }
        }
        return null;
    }

    public boolean delete(String id) {
        for(int i = 0; i < tasks.size(); i++) {
            if(tasks.get(i).getId().equals(id)) {
                tasks.remove(i);
                saveAll();
                return true;
            }
        }
        return false;
    }

    public ArrayList<Task> getByAssignee(String personId) {
        ArrayList<Task> result = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getAssignee().getId().equals(personId)) {
                result.add(task);
            }
        }
        return result;
    }

    public void saveAll() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(storageFile))) {
            for (Task t : tasks) {
                String due = t.getDueDate() == null ? "" : t.getDueDate().toString();
                String desc = t.getDescription() == null ? "" : t.getDescription().replace("\n", "\\n");
                String line = String.join("|",
                        t.getId(),
                        t.getTitle().replace("|", " "),
                        desc.replace("|", " "),
                        t.getCategory().name(),
                        t.getStatus().name(),
                        t.getAssignee().getId(),
                        due,
                        t.getEisenhower() == null ? "" : t.getEisenhower()
                );
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RepositoryException("Failed to save tasks: " + e.getMessage());
        }
    }

    private void loadFromFile() {
        if (!storageFile.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(storageFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("\\|", -1);
                // id|title|description|category|status|assigneeId|dueDate|eisenhower
                if (parts.length < 8) continue;
                String id = parts[0];
                String title = parts[1];
                String description = parts[2].replace("\\n", "\n");
                String cat = parts[3];
                String status = parts[4];
                String assigneeId = parts[5];
                String due = parts[6];
                String eisenhower = parts[7].isEmpty() ? null : parts[7];

                taskmanager.core.Category category;
                taskmanager.core.Status st;
                try {
                    category = taskmanager.core.Category.valueOf(cat);
                    st = taskmanager.core.Status.valueOf(status);
                } catch (IllegalArgumentException e) {
                    continue;
                }

                Person assignee = personRepo.findById(assigneeId);
                if (assignee == null) continue; // skip tasks with unknown people

                LocalDate dueDate = null;
                if (!due.isEmpty()) {
                    try { dueDate = LocalDate.parse(due); } catch (Exception ex) { dueDate = null; }
                }

                Task t = new Task(id, title, description, category, st, assignee, dueDate, eisenhower);
                tasks.add(t);
            }
        } catch (IOException e) {
            throw new RepositoryException("Failed to load tasks: " + e.getMessage());
        }
    }
}
