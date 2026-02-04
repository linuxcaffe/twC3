package com.taskwarriormobile;

import android.content.Context;
import java.util.*;

public class Taskwarrior {
    private Context context;
    
    public Taskwarrior(Context context) {
        this.context = context;
    }
    
    public List<Task> getTasks() {
        // For now, return sample tasks
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("1", "Install Taskwarrior on Android", "pending", "H", "setup"));
        tasks.add(new Task("2", "Configure taskrc file", "pending", "M", "setup"));
        tasks.add(new Task("3", "Add first real task", "pending", "M", null));
        tasks.add(new Task("4", "Test sync with server", "completed", "L", "sync"));
        tasks.add(new Task("5", "Customize mobile interface", "pending", "L", "ui"));
        return tasks;
    }
    
    public List<Task> getPendingTasks() {
        List<Task> allTasks = getTasks();
        List<Task> pending = new ArrayList<>();
        for (Task task : allTasks) {
            if ("pending".equals(task.status)) {
                pending.add(task);
            }
        }
        return pending;
    }
    
    public String addTask(String description) {
        // Simulate adding a task
        return "Added: " + description;
    }
    
    public String completeTask(String uuid) {
        return "Completed: " + uuid;
    }
    
    public static class Task {
        public String uuid;
        public String description;
        public String status;
        public String priority;
        public String project;
        
        public Task(String uuid, String description, String status, String priority, String project) {
            this.uuid = uuid;
            this.description = description;
            this.status = status;
            this.priority = priority;
            this.project = project;
        }
        
        @Override
        public String toString() {
            String prio = priority != null ? "[" + priority + "] " : "";
            String proj = project != null ? " +" + project : "";
            return prio + description + proj;
        }
    }
}
