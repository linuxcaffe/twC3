package com.taskwarriormobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.util.*;

public class TaskwarriorTermuxAPI {
    private static final String TAG = "TaskwarriorTermux";
    private Context context;
    
    public TaskwarriorTermuxAPI(Context context) {
        this.context = context;
    }
    
    // Method 1: Try direct execution (might work with Termux:Task)
    public String tryDirect() {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "/data/data/com.termux/files/usr/bin/task",
                "--version"
            );
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
            String version = reader.readLine();
            process.waitFor();
            return "Direct: " + (version != null ? version : "No output");
        } catch (Exception e) {
            return "Direct failed: " + e.getMessage();
        }
    }
    
    // Method 2: Try via Termux:Task plugin
    public String tryTermuxTaskPlugin() {
        try {
            // This intent might work if Termux:Task is installed
            Intent intent = new Intent("com.termux.task.EXECUTE");
            intent.setClassName("com.termux.task", "com.termux.task.ExecuteService");
            intent.putExtra("command", "task --version");
            
            // We'd need a ServiceConnection to get results
            // For now, just try if the service exists
            return "Termux:Task service check - needs implementation";
        } catch (Exception e) {
            return "Termux:Task failed: " + e.getMessage();
        }
    }
    
    // Method 3: Create a bridge script in Termux
    public String setupBridgeScript() {
        // We could guide user to create a script in Termux
        // that our app can call via Termux:Run
        return "Bridge script setup needed";
    }
    
    // Method 4: Use Termux:Run API
    public String tryTermuxRun() {
        try {
            // Termux:Run allows executing commands via Intent
            Intent intent = new Intent("com.termux.RUN_COMMAND");
            intent.setClassName("com.termux", "com.termux.app.RunCommandService");
            intent.putExtra("com.termux.RUN_COMMAND_PATH", "/data/data/com.termux/files/usr/bin/task");
            intent.putExtra("com.termux.RUN_COMMAND_ARGUMENTS", new String[]{"--version"});
            intent.putExtra("com.termux.RUN_COMMAND_BACKGROUND", false);
            
            // This requires Termux:Run plugin
            return "Termux:Run intent ready - needs broadcast";
        } catch (Exception e) {
            return "Termux:Run failed: " + e.getMessage();
        }
    }
    
    // Check all methods
    public String diagnose() {
        StringBuilder sb = new StringBuilder();
        sb.append("Taskwarrior Integration Diagnosis\n");
        sb.append("═══════════════════════════════\n\n");
        
        sb.append("1. ").append(tryDirect()).append("\n\n");
        sb.append("2. ").append(tryTermuxTaskPlugin()).append("\n\n");
        sb.append("3. ").append(tryTermuxRun()).append("\n\n");
        
        sb.append("═══════════════════════════════\n");
        sb.append("Recommended next steps:\n");
        sb.append("1. Install Termux:Task plugin\n");
        sb.append("2. Or create bridge script\n");
        sb.append("3. Or use Termux:Run API\n");
        
        return sb.toString();
    }
}
