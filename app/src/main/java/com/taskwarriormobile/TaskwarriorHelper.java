package com.taskwarriormobile;

import android.content.Context;
import android.util.Log;
import java.io.*;

public class TaskwarriorHelper {
    private static final String TAG = "TW_HELPER";
    private Context context;
    private File taskBinary;
    private File taskDataDir;
    
    public TaskwarriorHelper(Context context) {
        this.context = context;
        this.taskBinary = new File(context.getFilesDir(), "task");
        this.taskDataDir = new File(context.getFilesDir(), "taskdata");
    }
    
    public boolean setup() {
        try {
            // Create data directory
            taskDataDir.mkdirs();
            
            // Extract binary
            if (!taskBinary.exists()) {
                extractBinary();
            }
            
            // Make executable
            taskBinary.setExecutable(true);
            
            Log.d(TAG, "Setup complete. Binary: " + taskBinary.getAbsolutePath());
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "Setup failed: " + e.getMessage());
            return false;
        }
    }
    
    private void extractBinary() throws IOException {
        InputStream in = context.getAssets().open("bin/arm64-v8a/task");
        OutputStream out = new FileOutputStream(taskBinary);
        
        byte[] buffer = new byte[1024];
        int length;
        while ((length = in.read(buffer)) > 0) {
            out.write(buffer, 0, length);
        }
        
        in.close();
        out.close();
    }
    
    public String runCommand(String command) {
        try {
            // Set TASKDATA environment - CRITICAL for Taskwarrior
            String[] cmd = {
                "sh", "-c",
                "TASKDATA='" + taskDataDir.getAbsolutePath() + "' " +
                "'" + taskBinary.getAbsolutePath() + "' " + command
            };
            
            Log.d(TAG, "Running: " + String.join(" ", cmd));
            
            Process process = Runtime.getRuntime().exec(cmd);
            
            // Read output
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
            );
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            
            // Also read errors
            BufferedReader errorReader = new BufferedReader(
                new InputStreamReader(process.getErrorStream())
            );
            StringBuilder errors = new StringBuilder();
            while ((line = errorReader.readLine()) != null) {
                errors.append(line).append("\n");
            }
            
            process.waitFor();
            
            String result = output.toString().trim();
            String errorStr = errors.toString().trim();
            
            // Return errors if no output
            if (result.isEmpty() && !errorStr.isEmpty()) {
                return errorStr;
            }
            
            return result.isEmpty() ? "(Command executed)" : result;
            
        } catch (Exception e) {
            Log.e(TAG, "Command failed: " + e.getMessage());
            return "Error: " + e.getMessage();
        }
    }
    
    public String getVersion() {
        return runCommand("--version");
    }
    
    public String listTasks() {
        return runCommand("list");
    }
    
    public String addTask(String description) {
        return runCommand("add \"" + description + "\"");
    }
}
