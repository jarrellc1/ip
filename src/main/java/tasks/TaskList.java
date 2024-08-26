package tasks;

import storage.Storage;
import exceptions.JarException;

import java.io.IOException;
import java.util.ArrayList;


public class TaskList {
    private ArrayList<Task> tasks = new ArrayList<>();

    public TaskList() {
    }

    public TaskList(Storage storage) throws IOException, JarException {
        this.tasks = new ArrayList<>(storage.load());  // Load tasks from the storage
    }

    public void addTask(Task task) throws JarException {
        if (task == null) {
            throw new JarException("Cannot add a null task.");
        }
        this.tasks.add(task);
    }

    public String listTasks() {
        if (tasks.isEmpty()) {
            return "No Tasks added yet, pls add tasks!";
        }
        StringBuilder taskListString = new StringBuilder();
        int counter = 1;
        for (Task task : tasks) {
            taskListString.append(counter).append(". ").append(task.toString()).append("\n");
            counter++;
        }
        return taskListString.toString();
    }

    public void markTaskAsDone(int index) throws JarException {
        Task task = getTask(index);
        task.setStatus(true);
    }

    public void markTaskAsUndone(int index) throws JarException {
        Task task = getTask(index);
        task.setStatus(false);
    }

    public Task getTask(int index) throws JarException {
        if (index < 0 || index >= tasks.size()) {
            throw new JarException("Invalid task number!");
        }
        return tasks.get(index);
    }

    public void deleteTask(int index) throws JarException {
        if (index < 0 || index >= tasks.size()) {
            throw new JarException("Invalid task number, cannot delete task");
        }
        tasks.remove(index);
    }

    public int getTaskCount() {
        return tasks.size();
    }

    public ArrayList<Task> getTasks() {
        return this.tasks;
    }

    public ArrayList<Task> findTasks(String keyword) throws JarException {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new JarException("Task cannot be empty!");
        } else {
            ArrayList<Task> sameTasks = new ArrayList<>();
            for (Task task : tasks) {
                if (task.toString().contains(keyword)) {
                    sameTasks.add(task);
                }
            }
            return sameTasks;
        }
    }
}
