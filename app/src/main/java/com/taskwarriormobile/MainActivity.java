package com.taskwarriormobile;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    
    private TextView taskOutput;
    private Button refreshButton;
    private Taskwarrior taskwarrior;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize UI components
        taskOutput = findViewById(R.id.taskOutput);
        refreshButton = findViewById(R.id.refreshButton);
        
        // Initialize Taskwarrior
        taskwarrior = new Taskwarrior(this);
        
        // Load initial tasks
        loadTasks();
        
        // Set up refresh button
        refreshButton.setOnClickListener(v -> loadTasks());
    }

    private void loadTasks() {
        // Get tasks from Taskwarrior
        StringBuilder tasksText = new StringBuilder();
        tasksText.append("Taskwarrior Mobile\n\n");
        tasksText.append("Task List:\n\n");

        for (String task : taskwarrior.getTasks()) {
            tasksText.append("• ").append(task).append("\n");
        }

        // Try to execute a shell command to test
        tasksText.append("\n--- System Info ---\n");
        String systemInfo = taskwarrior.executeCommand("echo 'Hello from Taskwarrior Mobile!'");
        tasksText.append(systemInfo);

        taskOutput.setText(tasksText.toString());
    }
}
