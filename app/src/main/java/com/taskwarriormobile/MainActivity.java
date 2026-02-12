package com.taskwarriormobile;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ScrollView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "TW_MAIN";
    
    private TextView taskOutput;
    private EditText newTaskInput;
    private Button addButton;
    private Button debugButton;
    private Button refreshButton;
    private ScrollView scrollView;
    private TaskwarriorBundled taskwarrior;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Initialize views with correct IDs from your layout
    taskOutput = findViewById(R.id.taskOutput);
    newTaskInput = findViewById(R.id.newTaskInput);
    addButton = findViewById(R.id.addButton);
    debugButton = findViewById(R.id.debugButton);
    refreshButton = findViewById(R.id.refreshButton);
    scrollView = findViewById(R.id.scrollView);
    
    // Initialize Taskwarrior
    taskwarrior = new TaskwarriorBundled(this);
    
    // FORCE DIAGNOSTIC DISPLAY IMMEDIATELY
    taskOutput.setText("=== TW_C3 INITIALIZING ===\n\n");
    taskOutput.append("TaskwarriorBundled initialized\n");
    taskOutput.append("Getting diagnostic log...\n\n");
    taskOutput.append(taskwarrior.getDiagnosticLog());
    taskOutput.append("\n=== READY ===\n");
    
    // Set up buttons
    addButton.setOnClickListener(v -> {
        String taskDesc = newTaskInput.getText().toString().trim();
        if (!taskDesc.isEmpty()) {
            runTaskwarriorCommand("add", taskDesc);
            newTaskInput.setText("");
        }
    });
    
    debugButton.setOnClickListener(v -> {
        taskOutput.setText("=== DIAGNOSTIC LOG ===\n\n");
        taskOutput.append(taskwarrior.getDiagnosticLog());
        taskOutput.append("\n=== RUNNING VERSION ===\n\n");
        runTaskwarriorCommand("version");
    });
    
    refreshButton.setOnClickListener(v -> {
        runTaskwarriorCommand("next");
    });
    
    // Initial test
    runTaskwarriorCommand("version");
}

    
    private void showDiagnosticLog() {
        taskOutput.setText("=== TW_C3 DIAGNOSTIC LOG ===\n\n");
        taskOutput.append(taskwarrior.getDiagnosticLog());
        taskOutput.append("\n=== READY ===\n\n");
    }
    
    private void runTaskwarriorCommand(String... commands) {
        String cmdString = String.join(" ", commands);
        taskOutput.append("$ task " + cmdString + "\n");
        
        new Thread(() -> {
            try {
                ProcessBuilder pb = taskwarrior.createProcessBuilder(commands);
                
                Log.d(TAG, "Running: " + String.join(" ", pb.command()));
                
                Process process = pb.start();
                
                StringBuilder output = new StringBuilder();
                String line;
                
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
                
                BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()));
                while ((line = errorReader.readLine()) != null) {
                    output.append("ERROR: ").append(line).append("\n");
                }
                
                int exitCode = process.waitFor();
                output.append("Exit code: ").append(exitCode).append("\n");
                
                // Update UI on main thread
                new Handler(Looper.getMainLooper()).post(() -> {
                    taskOutput.append(output.toString());
                    taskOutput.append("\n---\n");
                    
                    // Scroll to bottom
                    scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
                });
                
            } catch (Exception e) {
                Log.e(TAG, "Error running taskwarrior", e);
                new Handler(Looper.getMainLooper()).post(() -> {
                    taskOutput.append("ERROR: " + e.getMessage() + "\n");
                    taskOutput.append("\n=== DIAGNOSTIC LOG ===\n");
                    taskOutput.append(taskwarrior.getDiagnosticLog());
                    taskOutput.append("\n---\n");
                });
            }
        }).start();
    }
}
