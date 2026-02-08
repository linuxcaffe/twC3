package com.taskwarriormobile;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final String TAG = "TW_APP";
    private TaskwarriorHelper taskHelper;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Starting Taskwarrior Mobile");
        
        // Create simple UI
        TextView tv = new TextView(this);
        tv.setText("Taskwarrior Mobile\n\nLoading...");
        tv.setTextSize(20);
        tv.setPadding(40, 40, 40, 40);
        setContentView(tv);
        
        // Initialize helper
        taskHelper = new TaskwarriorHelper(this);
        
        // Setup in background
        new Thread(() -> initTaskwarrior(tv)).start();
    }
    
    private void initTaskwarrior(TextView tv) {
        try {
            updateUI(tv, "Setting up...");
            
            // Setup Taskwarrior
            if (!taskHelper.setup()) {
                updateUI(tv, "Setup failed!");
                return;
            }
            
            updateUI(tv, "Testing Taskwarrior...");
            
            // Get version
            String version = taskHelper.getVersion();
            Log.d(TAG, "Version result: " + version);
            
            if (version.contains("Taskwarrior")) {
                // Get initial task list
                String tasks = taskHelper.listTasks();
                if (tasks == null || tasks.isEmpty()) {
                    tasks = "No tasks yet";
                }
                
                final String message = "✓ TASKWARRIOR READY!\n\n" +
                                     version + "\n\n" +
                                     "Tasks:\n" + tasks + "\n\n" +
                                     "Tap to add test task";
                
                updateUI(tv, message);
                setClickHandler(tv);
                
            } else {
                updateUI(tv, "Failed: " + version + "\n\nTap to retry");
                setClickHandler(tv);
            }
            
        } catch (Exception e) {
            updateUI(tv, "Error: " + e.getMessage());
        }
    }
    
    private void updateUI(TextView tv, String text) {
        runOnUiThread(() -> tv.setText(text));
    }
    
    private void setClickHandler(TextView tv) {
        runOnUiThread(() -> tv.setOnClickListener(v -> addTestTask(tv)));
    }
    
    private void addTestTask(TextView tv) {
        updateUI(tv, "Adding task...");
        
        new Thread(() -> {
            try {
                // Add a task
                String result = taskHelper.addTask("Task from twC3 app");
                
                // Get updated list
                String tasks = taskHelper.listTasks();
                
                final String message = "Task added!\n\n" +
                                     "All tasks:\n" + tasks + "\n\n" +
                                     "Tap to add another";
                
                updateUI(tv, message);
                
            } catch (Exception e) {
                updateUI(tv, "Error: " + e.getMessage());
            }
        }).start();
    }
}
