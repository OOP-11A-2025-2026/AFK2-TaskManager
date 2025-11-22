package taskmanager.data;

import taskmanager.core.Task;
import taskmanager.exceptions.RepositoryException;

import java.util.ArrayList;

public class TaskRepository {
    private ArrayList<Task> tasks;

    public TaskRepository() {
        tasks = new ArrayList<>();
    }

    public TaskRepository(ArrayList<Task> tasks) {
        this.tasks = tasks;
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
        if(!exists(task.getId())) tasks.add(task);
        else throw new RepositoryException("Task already exists");
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
                return true;
            }
        }
        return false;
    }

    // GET BY ASSIGNEE AND THIRD RULE!!!
}
