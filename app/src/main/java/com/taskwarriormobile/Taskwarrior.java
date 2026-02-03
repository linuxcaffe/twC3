package com.taskwarriormobile;

import android.content.Context;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Taskwarrior {
    private Context context;
    
    public Taskwarrior(Context context) {
        this.context = context;
    }
    
    public List<String> getTasks() {
        List<String> tasks = new ArrayList<>();
        // TODO: Implement Taskwarrior integration
        tasks.add("1. Sample task - Update Taskwarrior integration");
        tasks.add("2. Another task - Connect to local Taskwarrior data");
        tasks.add("3. Third task - Implement task management UI");
        return tasks;
    }
    
    public String executeCommand(String command) {
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            process.waitFor();
            return output.toString();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
