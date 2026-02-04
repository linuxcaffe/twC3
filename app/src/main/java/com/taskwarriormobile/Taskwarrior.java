package com.taskwarriormobile;

import android.content.Context;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.util.*;

public class Taskwarrior {
    private static final String TAG = "TaskwarriorMobile";
    private Context context;
    private String debugInfo = "";
    
    public Taskwarrior(Context context) {
        this.context = context;
        debugInfo = "Initialized\n";
    }
    
    // Get debug information
    public String getDebugInfo() {
        return debugInfo;
    }
    
    // Check all possible Taskwarrior locations
    public String checkAllPaths() {
        StringBuilder paths = new StringBuilder();
        paths.append("Checking Taskwarrior paths:\n");
        
        String[] possiblePaths = {
            "task",  // In PATH
            "/data/data/com.termux/files/usr/bin/task",  // Termux default
            "/data/data/com.termux/files/usr/bin/bash -c task",  // Termux via bash
            "/system/bin/task",
            "/system/xbin/task",
            "/bin/task",
            "/usr/bin/task"
        };
        
        for (String path : possiblePaths) {
            String result = testPath(path);
            paths.append(path).append(": ").append(result).append("\n");
        }
        
        debugInfo += paths.toString();
        return paths.toString();
    }
    
    private String testPath(String path) {
        try {
            ProcessBuilder pb;
            if (path.contains("bash -c")) {
                pb = new ProcessBuilder("/system/bin/sh", "-c", path);
            } else {
                pb = new ProcessBuilder(path, "--version");
            }
            
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            process.waitFor();
            
            if (line != null && line.contains("Taskwarrior")) {
                return "✅ Found: " + line;
            } else {
                return "❌ Not found";
            }
        } catch (Exception e) {
            return "❌ Error: " + e.getClass().getSimpleName();
        }
    }
    
    // Check if Taskwarrior is available
    public boolean isAvailable() {
        debugInfo += "Checking availability...\n";
        String checkResult = checkAllPaths();
        
        // Try the most likely path
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "/data/data/com.termux/files/usr/bin/task", 
                "--version"
            );
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
            String version = reader.readLine();
            int exitCode = process.waitFor();
            
            if (exitCode == 0 && version != null && version.contains("Taskwarrior")) {
                debugInfo += "✅ Found via direct path: " + version + "\n";
                return true;
            }
        } catch (Exception e) {
            debugInfo += "❌ Direct path failed: " + e.getMessage() + "\n";
        }
        
        debugInfo += "Taskwarrior not found via any method\n";
        return false;
    }
    
    // Get tasks
    public List<Task> getTasks() {
        if (isAvailable()) {
            return getRealTasks();
        } else {
            return getSampleTasks();
        }
    }
    
    private List<Task> getRealTasks() {
        List<Task> tasks = new ArrayList<>();
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "/data/data/com.termux/files/usr/bin/task",
                "rc.json.array=on",
                "rc.confirmation=off",
                "export"
            );
            
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
            
            process.waitFor();
            
            JSONArray jsonArray = new JSONArray(output.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                Task task = new Task();
                task.description = json.optString("description", "Unknown");
                task.status = json.optString("status", "pending");
                tasks.add(task);
            }
            
            debugInfo += "✅ Got " + tasks.size() + " real tasks\n";
            
        } catch (Exception e) {
            debugInfo += "❌ Failed to get real tasks: " + e.getMessage() + "\n";
            tasks = getSampleTasks();
        }
        
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
        if (isAvailable()) {
            try {
                ProcessBuilder pb = new ProcessBuilder(
                    "/data/data/com.termux/files/usr/bin/task",
                    "add",
                    description
                );
                
                Process process = pb.start();
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
                
                process.waitFor();
                
                return "✅ Added via Termux: " + description;
                
            } catch (Exception e) {
                return "❌ Failed to add: " + e.getMessage();
            }
        } else {
            return "⚠️ Taskwarrior not found in Termux\n" +
                   "Install: pkg install taskwarrior\n" +
                   "Then restart this app";
        }
    }
    
    private List<Task> getSampleTasks() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("Open Termux and install taskwarrior", "pending"));
        tasks.add(new Task("Run: pkg install taskwarrior", "pending"));
        tasks.add(new Task("Check: task --version", "pending"));
        tasks.add(new Task("Add test: task add 'Hello'", "pending"));
        debugInfo += "Using sample tasks\n";
        return tasks;
    }
    
    public static class Task {
        public String description;
        public String status;
        
        public Task() {}
        
        public Task(String description, String status) {
            this.description = description;
            this.status = status;
        }
        
        @Override
        public String toString() {
            return description;
        }
    }
}
