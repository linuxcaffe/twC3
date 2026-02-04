package com.taskwarriormobile;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    
    private TextView taskOutput;
    private Button refreshButton;
    private Button addButton;
    private EditText newTaskInput;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private Taskwarrior taskwarrior;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        taskOutput = findViewById(R.id.taskOutput);
        refreshButton = findViewById(R.id.refreshButton);
        addButton = findViewById(R.id.addButton);
        newTaskInput = findViewById(R.id.newTaskInput);
        
        taskwarrior = new Taskwarrior(this);
        
        // Initial message
        taskOutput.setText("Taskwarrior Mobile\n" +
                          "══════════════════\n\n" +
                          "Checking Termux integration...");
        
        refreshButton.setOnClickListener(v -> loadTasks());
        
        addButton.setOnClickListener(v -> {
            String desc = newTaskInput.getText().toString().trim();
            if (!desc.isEmpty()) {
                addTask(desc);
                newTaskInput.setText("");
            }
        });
        
        // Long press refresh for debug
        refreshButton.setOnLongClickListener(v -> {
            showDebugInfo();
            return true;
        });
        
        // Initial load
        taskOutput.postDelayed(this::loadTasks, 1000);
    }
    
    private void loadTasks() {
        refreshButton.setEnabled(false);
        refreshButton.setText("Checking...");
        
        executor.execute(() -> {
            boolean available = taskwarrior.isAvailable();
            List<Taskwarrior.Task> tasks = taskwarrior.getPendingTasks();
            
            mainHandler.post(() -> {
                displayTasks(tasks, available);
            });
        });
    }
    
    private void displayTasks(List<Taskwarrior.Task> tasks, boolean available) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("Taskwarrior Mobile\n");
        sb.append("══════════════════\n\n");
        
        if (available) {
            sb.append("✅ LIVE MODE - Connected to Termux!\n\n");
        } else {
            sb.append("⚠️ DEMO MODE\n");
            sb.append("Taskwarrior not found\n\n");
        }
        
        sb.append("Your Tasks:\n");
        sb.append("──────────\n\n");
        
        if (tasks.isEmpty()) {
            sb.append("No tasks found\n");
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                sb.append(i + 1).append(". ").append(tasks.get(i).toString()).append("\n");
            }
        }
        
        sb.append("\n──────────\n");
        sb.append(tasks.size()).append(" tasks");
        
        if (!available) {
            sb.append("\n\nℹ️ Long-press Refresh for debug info");
        }
        
        taskOutput.setText(sb.toString());
        refreshButton.setEnabled(true);
        refreshButton.setText("Refresh (" + tasks.size() + ")");
    }
    
    private void addTask(String description) {
        addButton.setEnabled(false);
        
        executor.execute(() -> {
            String result = taskwarrior.addTask(description);
            
            mainHandler.post(() -> {
                Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
                addButton.setEnabled(true);
                loadTasks();
            });
        });
    }
    
    private void showDebugInfo() {
        executor.execute(() -> {
            String debugInfo = taskwarrior.getDebugInfo();
            
            mainHandler.post(() -> {
                taskOutput.setText("Debug Info\n" +
                                  "══════════\n\n" +
                                  debugInfo + "\n\n" +
                                  "In Termux, check:\n" +
                                  "1. which task\n" +
                                  "2. task --version\n" +
                                  "3. ls -la /data/data/com.termux/files/usr/bin/task");
            });
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
