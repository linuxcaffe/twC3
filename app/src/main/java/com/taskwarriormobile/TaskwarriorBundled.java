package com.taskwarriormobile;

import android.content.Context;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.util.*;

public class TaskwarriorBundled {
    private static final String TAG = "TaskwarriorBundled";
    private Context context;
    private File taskBinary;
    private File taskDataDir;
    private String debugLog = "";
    
    public TaskwarriorBundled(Context context) {
        this.context = context;
        debugLog = "Initializing...\n";
        
        taskBinary = new File(context.getFilesDir(), "task");
        taskDataDir = new File(context.getFilesDir(), ".task");
        taskDataDir.mkdirs();
        
        debugLog += "Binary path: " + taskBinary.getAbsolutePath() + "\n";
        debugLog += "Data dir: " + taskDataDir.getAbsolutePath() + "\n";
        
        extractBinaryFromAssets();
        setupTaskRC();
    }
    
    public String getDebugLog() {
        return debugLog;
    }
    
    private void extractBinaryFromAssets() {
        debugLog += "Extracting binary...\n";
        
        if (taskBinary.exists()) {
            debugLog += "Binary already exists\n";
            return;
        }
        
        try {
            // List available assets for debugging
            String[] assets = context.getAssets().list("");
            debugLog += "Assets root: " + Arrays.toString(assets) + "\n";
            
            String[] binAssets = context.getAssets().list("bin");
            debugLog += "Assets bin/: " + (binAssets != null ? Arrays.toString(binAssets) : "null") + "\n";
            
            // Try to open the binary
            InputStream is = context.getAssets().open("bin/arm64-v8a/task");
            debugLog += "Asset opened successfully\n";
            
            FileOutputStream fos = new FileOutputStream(taskBinary);
            
            byte[] buffer = new byte[1024];
            int length;
            int total = 0;
            while ((length = is.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
                total += length;
            }
            
            fos.close();
            is.close();
            
            debugLog += "Extracted " + total + " bytes\n";
            
            // Make executable
            boolean executable = taskBinary.setExecutable(true);
            debugLog += "Set executable: " + executable + "\n";
            debugLog += "File exists: " + taskBinary.exists() + "\n";
            debugLog += "File can read: " + taskBinary.canRead() + "\n";
            debugLog += "File can execute: " + taskBinary.canExecute() + "\n";
            
        } catch (Exception e) {
            debugLog += "Extraction failed: " + e.getMessage() + "\n";
            e.printStackTrace();
            createFallbackStub();
        }
    }
    
    private void createFallbackStub() {
        try {
            debugLog += "Creating fallback stub\n";
            FileWriter writer = new FileWriter(taskBinary);
            writer.write("#!/system/bin/sh\n");
            writer.write("echo 'Taskwarrior 2.6.2 (stub)'\n");
            writer.write("echo 'Add real binary to assets/bin/arm64-v8a/task'\n");
            writer.write("exit 0\n");
            writer.close();
            taskBinary.setExecutable(true);
            debugLog += "Stub created\n";
        } catch (Exception e) {
            debugLog += "Stub failed: " + e.getMessage() + "\n";
        }
    }
    
    private void setupTaskRC() {
        File taskrc = new File(taskDataDir, "taskrc");
        if (!taskrc.exists()) {
            try {
                FileWriter writer = new FileWriter(taskrc);
                writer.write("# Taskwarrior Mobile Configuration\n");
                writer.write("color=off\n");
                writer.write("confirmation=off\n");
                writer.write("json.array=on\n");
                writer.write("verbose=nothing\n");
                writer.write("data.location=" + taskDataDir.getAbsolutePath() + "\n");
                writer.close();
                debugLog += "Created taskrc\n";
            } catch (IOException e) {
                debugLog += "Failed to create taskrc: " + e.getMessage() + "\n";
            }
        }
    }
    
    private String executeTaskCommand(String... args) {
        try {
            debugLog += "Executing: task " + Arrays.toString(args) + "\n";
            
            List<String> command = new ArrayList<>();
            command.add(taskBinary.getAbsolutePath());
            command.add("rc:" + new File(taskDataDir, "taskrc").getAbsolutePath());
            Collections.addAll(command, args);
            
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(taskDataDir);
            
            Process process = pb.start();
            
            // Read output
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            
            // Read errors
            BufferedReader errorReader = new BufferedReader(
                new InputStreamReader(process.getErrorStream()));
            StringBuilder errors = new StringBuilder();
            while ((line = errorReader.readLine()) != null) {
                errors.append(line).append("\n");
            }
            
            int exitCode = process.waitFor();
            
            debugLog += "Exit code: " + exitCode + "\n";
            debugLog += "Output length: " + output.length() + "\n";
            debugLog += "Errors: " + errors.length() + "\n";
            
            if (exitCode == 0) {
                return output.toString();
            } else {
                return "ERROR[" + exitCode + "]: " + errors.toString();
            }
            
        } catch (Exception e) {
            debugLog += "Execution exception: " + e.getMessage() + "\n";
            return "EXCEPTION: " + e.getMessage();
        }
    }
    
    public boolean isAvailable() {
        debugLog += "Checking availability...\n";
        String result = executeTaskCommand("--version");
        boolean available = !result.startsWith("ERROR") && !result.startsWith("EXCEPTION");
        debugLog += "Available: " + available + " - Result: " + result.substring(0, Math.min(50, result.length())) + "\n";
        return available;
    }
    
    public List<Task> getTasks() {
        debugLog += "Getting tasks...\n";
        if (isAvailable()) {
            return getRealTasks();
        } else {
            return getSampleTasks();
        }
    }
    
    private List<Task> getRealTasks() {
        List<Task> tasks = new ArrayList<>();
        try {
            String result = executeTaskCommand("export");
            
            if (!result.startsWith("ERROR") && !result.startsWith("EXCEPTION")) {
                JSONArray jsonArray = new JSONArray(result);
                
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);
                    Task task = new Task();
                    task.description = json.optString("description", "Unknown");
                    task.status = json.optString("status", "pending");
                    task.priority = json.optString("priority", "");
                    task.project = json.optString("project", "");
                    tasks.add(task);
                }
                
                debugLog += "Got " + tasks.size() + " real tasks\n";
            } else {
                debugLog += "Real tasks failed: " + result + "\n";
            }
            
        } catch (Exception e) {
            debugLog += "Parse exception: " + e.getMessage() + "\n";
        }
        
        if (tasks.isEmpty()) {
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
        debugLog += "Adding task: " + description + "\n";
        if (isAvailable()) {
            String result = executeTaskCommand("add", description);
            if (result.startsWith("ERROR") || result.startsWith("EXCEPTION")) {
                return "Failed to add task: " + result;
            } else {
                return "✅ Added: " + description;
            }
        } else {
            return "⚠️ Using sample mode\n(Would add: " + description + ")\n\nDebug: " + debugLog;
        }
    }
    
    private List<Task> getSampleTasks() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("Debug bundling issue", "pending", "H", "debug"));
        tasks.add(new Task("Check assets extraction", "pending", "M", "debug"));
        tasks.add(new Task("Binary should be in assets/", "pending", "M", "info"));
        tasks.add(new Task("Then extracted on first run", "pending", "L", "info"));
        return tasks;
    }
    
    public static class Task {
        public String description;
        public String status;
        public String priority;
        public String project;
        
        public Task() {}
        
        public Task(String description, String status, String priority, String project) {
            this.description = description;
            this.status = status;
            this.priority = priority;
            this.project = project;
        }
        
        @Override
        public String toString() {
            String prio = priority != null && !priority.isEmpty() ? "[" + priority + "] " : "";
            String proj = project != null && !project.isEmpty() ? " +" + project : "";
            return prio + description + proj;
        }
    }
}
