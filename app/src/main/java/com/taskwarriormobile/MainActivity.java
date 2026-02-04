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
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Find views
        taskOutput = findViewById(R.id.taskOutput);
        refreshButton = findViewById(R.id.refreshButton);
        addButton = findViewById(R.id.addButton);
        newTaskInput = findViewById(R.id.newTaskInput);
        
        // Welcome message
        taskOutput.setText("✅ Taskwarrior Mobile Ready!\n\n" +
                          "Click 'Refresh Tasks' to load your tasks");
        
        // Button actions
        refreshButton.setOnClickListener(v -> loadTasks());
        
        addButton.setOnClickListener(v -> {
            String desc = newTaskInput.getText().toString().trim();
            if (!desc.isEmpty()) {
                addTask(desc);
                newTaskInput.setText("");
            } else {
                Toast.makeText(this, "Enter task description", Toast.LENGTH_SHORT).show();
            }
        });
        
        // Auto-load after 1 second
        taskOutput.postDelayed(this::loadTasks, 1000);
    }
    
    private void loadTasks() {
        refreshButton.setEnabled(false);
        refreshButton.setText("Loading...");
        
        executor.execute(() -> {
            try {
                Taskwarrior taskwarrior = new Taskwarrior(this);
                List<Taskwarrior.Task> tasks = taskwarrior.getPendingTasks();
                
                mainHandler.post(() -> displayTasks(tasks));
                
            } catch (Exception e) {
                mainHandler.post(() -> {
                    taskOutput.setText("Error: " + e.getMessage() + "\n\nUsing sample tasks...");
                    Taskwarrior taskwarrior = new Taskwarrior(this);
                    displayTasks(taskwarrior.getPendingTasks());
                });
            }
        });
    }
    
    private void displayTasks(List<Taskwarrior.Task> tasks) {
        StringBuilder sb = new StringBuilder();
        sb.append("📋 Your Tasks\n");
        sb.append("══════════════════\n\n");
        
        if (tasks.isEmpty()) {
            sb.append("No pending tasks!\n\n");
            sb.append("Add your first task above ↑");
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                Taskwarrior.Task task = tasks.get(i);
                sb.append(i + 1).append(". ");
                if (task.priority != null && !task.priority.isEmpty()) {
                    sb.append("[").append(task.priority).append("] ");
                }
                sb.append(task.description);
                if (task.project != null && !task.project.isEmpty()) {
                    sb.append(" (+").append(task.project).append(")");
                }
                sb.append("\n");
            }
        }
        
        sb.append("\n══════════════════\n");
        sb.append("Total: ").append(tasks.size()).append(" pending tasks");
        
        taskOutput.setText(sb.toString());
        refreshButton.setEnabled(true);
        refreshButton.setText("Refresh (" + tasks.size() + " tasks)");
    }
    
    private void addTask(String description) {
        executor.execute(() -> {
            try {
                Taskwarrior taskwarrior = new Taskwarrior(this);
                String result = taskwarrior.addTask(description);
                
                mainHandler.post(() -> {
                    Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
                    loadTasks(); // Refresh the list
                });
                
            } catch (Exception e) {
                mainHandler.post(() -> {
                    Toast.makeText(MainActivity.this, 
                        "Added to sample tasks: " + description, 
                        Toast.LENGTH_SHORT).show();
                    loadTasks();
                });
            }
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
