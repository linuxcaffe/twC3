package com.taskwarriormobile;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import java.io.*;

public class SimpleTestActivity extends Activity {
    private static final String TAG = "SimpleTest";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        tv.setText("Testing Taskwarrior...");
        tv.setPadding(50, 50, 50, 50);
        setContentView(tv);
        
        new Thread(() -> {
            try {
                // Manual execution test
                File binary = new File(getFilesDir(), "task");
                File dataDir = new File(getFilesDir(), "taskdata");
                dataDir.mkdirs();
                
                // Copy binary from assets if needed
                if (!binary.exists()) {
                    InputStream in = getAssets().open("bin/arm64-v8a/task");
                    OutputStream out = new FileOutputStream(binary);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0) {
                        out.write(buffer, 0, length);
                    }
                    in.close();
                    out.close();
                    binary.setExecutable(true);
                }
                
                // Test execution
                String cmd = binary.getAbsolutePath() + 
                           " rc.data.location=" + dataDir.getAbsolutePath() + 
                           " --version";
                
                Process p = Runtime.getRuntime().exec(new String[]{"sh", "-c", cmd});
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(p.getInputStream())
                );
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
                p.waitFor();
                
                final String result = "DIRECT TEST:\n" + output.toString().trim();
                runOnUiThread(() -> tv.setText(result));
                
            } catch (Exception e) {
                final String error = "Error: " + e.getMessage();
                Log.e(TAG, error, e);
                runOnUiThread(() -> tv.setText(error));
            }
        }).start();
    }
}
