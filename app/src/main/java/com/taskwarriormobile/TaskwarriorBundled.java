package com.taskwarriormobile;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TaskwarriorBundled {
    private static final String TAG = "TW_BUNDLED";
    private static final String BINARY_NAME = "task";
    private static final String[] SUPPORTED_ABIS = {
        "arm64-v8a",  // 64-bit ARM
        "armeabi-v7a", // 32-bit ARM
        "x86",        // 32-bit x86
        "x86_64"      // 64-bit x86
    };
    
    private final Context context;
    private String currentArch;
    private File binaryDir;
    private File taskFile;
    private StringBuilder diagnosticLog;
    
    public TaskwarriorBundled(Context context) {
        this.context = context;
        this.diagnosticLog = new StringBuilder();
        logDiag("=== TaskwarriorBundled Initializing ===");
        logDiag("Android API Level: " + Build.VERSION.SDK_INT);
        logDiag("Device: " + Build.MANUFACTURER + " " + Build.MODEL);
        
        // Use getDir() instead of getFilesDir() - better permissions on Android 10+
        this.binaryDir = context.getDir("taskbin", Context.MODE_PRIVATE);
        this.taskFile = new File(binaryDir, BINARY_NAME);
        logDiag("Binary directory: " + binaryDir.getAbsolutePath());
        logDiag("Binary path: " + taskFile.getAbsolutePath());
        
        detectArchitecture();
    }
    
    public String getDiagnosticLog() {
        return diagnosticLog.toString();
    }
    
    private void logDiag(String message) {
        Log.d(TAG, message);
        diagnosticLog.append(message).append("\n");
        // Keep log size manageable
        if (diagnosticLog.length() > 5000) {
            diagnosticLog.delete(0, 2000);
        }
    }
    
    private void detectArchitecture() {
        String[] abis;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            abis = Build.SUPPORTED_ABIS;
        } else {
            abis = new String[]{Build.CPU_ABI, Build.CPU_ABI2};
        }
        
        logDiag("Device ABIs: " + String.join(", ", abis));
        
        for (String abi : abis) {
            for (String supported : SUPPORTED_ABIS) {
                if (abi.startsWith(supported) || supported.startsWith(abi)) {
                    currentArch = supported;
                    logDiag("✓ Selected architecture: " + currentArch);
                    return;
                }
            }
        }
        
        // Default to arm64-v8a as fallback
        currentArch = "arm64-v8a";
        logDiag("⚠ No matching ABI found, defaulting to: " + currentArch);
    }
    
    public String ensureTaskwarriorBinary() throws IOException {
        logDiag("\n=== Ensuring Taskwarrior Binary ===");
        
        // Create binary directory if it doesn't exist
        if (!binaryDir.exists()) {
            binaryDir.mkdirs();
            logDiag("Created binary directory: " + binaryDir.getAbsolutePath());
        }
        
        // Check directory permissions
        logDiag("Binary dir can read: " + binaryDir.canRead());
        logDiag("Binary dir can write: " + binaryDir.canWrite());
        logDiag("Binary dir can execute: " + binaryDir.canExecute());
        
        // Check if binary already exists and is executable
        if (taskFile.exists()) {
            logDiag("Binary already exists at: " + taskFile.getAbsolutePath());
            logDiag("Size: " + taskFile.length() + " bytes");
            logDiag("Can read: " + taskFile.canRead());
            logDiag("Can write: " + taskFile.canWrite());
            logDiag("Can execute: " + taskFile.canExecute());
            
            if (taskFile.canExecute()) {
                logDiag("✓ Binary is already executable");
                if (verifyBinary(taskFile)) {
                    return taskFile.getAbsolutePath();
                } else {
                    logDiag("✗ Existing binary failed verification, will re-extract");
                    taskFile.delete();
                }
            } else {
                logDiag("⚠ Binary exists but not executable, fixing permissions...");
                if (makeExecutableWithExtremePrejudice(taskFile)) {
                    return taskFile.getAbsolutePath();
                } else {
                    logDiag("✗ Failed to make existing binary executable, will re-extract");
                    taskFile.delete();
                }
            }
        }
        
        // Extract binary from assets
        String assetPath = "bin" + File.separator + currentArch + File.separator + BINARY_NAME;
        logDiag("Extracting binary from assets: " + assetPath);
        
        try (InputStream is = context.getAssets().open(assetPath)) {
            copyFile(is, taskFile);
            logDiag("✓ Binary extracted to: " + taskFile.getAbsolutePath());
            logDiag("Extracted size: " + taskFile.length() + " bytes");
        } catch (IOException e) {
            logDiag("✗ Failed to extract binary: " + e.getMessage());
            throw new IOException("Could not extract Taskwarrior binary for " + currentArch, e);
        }
        
        // Make executable with EXTREME PREJUDICE
        if (!makeExecutableWithExtremePrejudice(taskFile)) {
            logDiag("✗ ALL 7 METHODS FAILED TO MAKE BINARY EXECUTABLE");
            throw new IOException("Failed to make Taskwarrior binary executable after 7 attempts");
        }
        
        // Verify the binary works
        if (!verifyBinary(taskFile)) {
            logDiag("✗ Binary verification failed");
            throw new IOException("Extracted binary failed verification");
        }
        
        logDiag("✓ Taskwarrior binary ready at: " + taskFile.getAbsolutePath());
        return taskFile.getAbsolutePath();
    }
    
    private boolean makeExecutableWithExtremePrejudice(File file) {
        logDiag("\n=== Attempting to make executable: " + file.getName() + " ===");
        
        // METHOD 1: Java's setExecutable
        try {
            logDiag("Method 1: file.setExecutable(true)");
            if (file.setExecutable(true)) {
                logDiag("  ✓ setExecutable returned true");
                if (file.canExecute()) {
                    logDiag("  ✓ File is executable after method 1");
                    if (verifyBinaryQuick(file)) {
                        logDiag("  ✓ Binary verification passed");
                        return true;
                    }
                }
            } else {
                logDiag("  ✗ setExecutable returned false");
            }
        } catch (SecurityException e) {
            logDiag("  ✗ Method 1 exception: " + e.getMessage());
        }
        
        // METHOD 2: chmod 755 via Runtime
        try {
            logDiag("Method 2: Runtime.exec('chmod 755')");
            Process p = Runtime.getRuntime().exec(new String[]{"chmod", "755", file.getAbsolutePath()});
            int exitCode = p.waitFor();
            logDiag("  Exit code: " + exitCode);
            if (file.canExecute() && verifyBinaryQuick(file)) {
                logDiag("  ✓ Method 2 succeeded");
                return true;
            }
        } catch (Exception e) {
            logDiag("  ✗ Method 2 exception: " + e.getMessage());
        }
        
        // METHOD 3: chmod with full system path
        try {
            logDiag("Method 3: /system/bin/chmod 755");
            Process p = Runtime.getRuntime().exec(new String[]{"/system/bin/chmod", "755", file.getAbsolutePath()});
            int exitCode = p.waitFor();
            logDiag("  Exit code: " + exitCode);
            if (file.canExecute() && verifyBinaryQuick(file)) {
                logDiag("  ✓ Method 3 succeeded");
                return true;
            }
        } catch (Exception e) {
            logDiag("  ✗ Method 3 exception: " + e.getMessage());
        }
        
        // METHOD 4: chmod 777 (desperate)
        try {
            logDiag("Method 4: chmod 777 (desperate)");
            Process p = Runtime.getRuntime().exec(new String[]{"chmod", "777", file.getAbsolutePath()});
            int exitCode = p.waitFor();
            logDiag("  Exit code: " + exitCode);
            if (file.canExecute() && verifyBinaryQuick(file)) {
                logDiag("  ✓ Method 4 succeeded");
                return true;
            }
        } catch (Exception e) {
            logDiag("  ✗ Method 4 exception: " + e.getMessage());
        }
        
        // METHOD 5: run-as chmod (Android 10+ workaround)
        try {
            logDiag("Method 5: run-as chmod");
            String packageName = context.getPackageName();
            logDiag("  Package name: " + packageName);
            Process p = Runtime.getRuntime().exec(new String[]{
                "run-as", packageName,
                "chmod", "755", file.getAbsolutePath()
            });
            int exitCode = p.waitFor();
            logDiag("  Exit code: " + exitCode);
            
            // Read error stream
            BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String line;
            while ((line = err.readLine()) != null) {
                logDiag("  run-as stderr: " + line);
            }
            
            if (file.canExecute() && verifyBinaryQuick(file)) {
                logDiag("  ✓ Method 5 succeeded");
                return true;
            }
        } catch (Exception e) {
            logDiag("  ✗ Method 5 exception: " + e.getMessage());
        }
        
        // METHOD 6: The nuclear option - copy to a different location
        try {
            logDiag("Method 6: Nuclear alt location");
            File altDir = new File(context.getFilesDir(), "taskbin_alt");
            altDir.mkdirs();
            File altFile = new File(altDir, BINARY_NAME);
            logDiag("  Alt path: " + altFile.getAbsolutePath());
            
            // Copy the file
            java.nio.file.Files.copy(file.toPath(), altFile.toPath(), 
                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            logDiag("  Copy complete");
            
            // Try all chmod methods on the alt file
            Runtime.getRuntime().exec(new String[]{"chmod", "755", altFile.getAbsolutePath()}).waitFor();
            Runtime.getRuntime().exec(new String[]{"/system/bin/chmod", "755", altFile.getAbsolutePath()}).waitFor();
            
            if (altFile.canExecute() && verifyBinaryQuick(altFile)) {
                logDiag("  ✓ Method 6 succeeded - using alt location");
                taskFile = altFile;
                binaryDir = altDir;
                return true;
            }
        } catch (Exception e) {
            logDiag("  ✗ Method 6 exception: " + e.getMessage());
        }
        
        // METHOD 7: Termux chmod (since you have Termux!)
        try {
            logDiag("Method 7: Termux chmod");
            File termuxChmod = new File("/data/data/com.termux/files/usr/bin/chmod");
            logDiag("  Termux chmod exists: " + termuxChmod.exists());
            
            if (termuxChmod.exists()) {
                Process p = Runtime.getRuntime().exec(new String[]{
                    termuxChmod.getAbsolutePath(),
                    "755", file.getAbsolutePath()
                });
                int exitCode = p.waitFor();
                logDiag("  Exit code: " + exitCode);
                
                if (file.canExecute() && verifyBinaryQuick(file)) {
                    logDiag("  ✓ Method 7 (Termux) succeeded!");
                    return true;
                }
            }
        } catch (Exception e) {
            logDiag("  ✗ Method 7 exception: " + e.getMessage());
        }
        
        logDiag("✗ ALL METHODS FAILED");
        return file.canExecute();
    }
    
    private boolean verifyBinaryQuick(File file) {
        try {
            Process process = new ProcessBuilder(file.getAbsolutePath(), "--version")
                .redirectErrorStream(true)
                .start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            logDiag("  Quick verify failed: " + e.getMessage());
            return false;
        }
    }
    
    private boolean verifyBinary(File file) {
        logDiag("\n=== Verifying binary ===");
        try {
            Process process = new ProcessBuilder(file.getAbsolutePath(), "--version")
                .redirectErrorStream(true)
                .start();
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
            String version = reader.readLine();
            
            int exitCode = process.waitFor();
            logDiag("  Version: " + version);
            logDiag("  Exit code: " + exitCode);
            
            return exitCode == 0;
        } catch (Exception e) {
            logDiag("  Verification failed: " + e.getMessage());
            return false;
        }
    }
    
    private void copyFile(InputStream is, File destination) throws IOException {
        try (OutputStream os = new FileOutputStream(destination)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            int totalBytes = 0;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
                totalBytes += bytesRead;
            }
            os.flush();
            logDiag("  Copied " + totalBytes + " bytes");
        }
    }
    
    public ProcessBuilder createProcessBuilder(String... commands) throws IOException {
        String taskPath = ensureTaskwarriorBinary();
        logDiag("\n=== Creating ProcessBuilder ===");
        logDiag("Task path: " + taskPath);
        
        ProcessBuilder pb = new ProcessBuilder(taskPath);
        
        // Add any additional commands
        for (int i = 1; i < commands.length; i++) {
            pb.command().add(commands[i]);
            logDiag("Command arg: " + commands[i]);
        }
        
        // Set up Taskwarrior environment
        File taskDataDir = new File(context.getFilesDir(), ".task");
        if (!taskDataDir.exists()) {
            taskDataDir.mkdirs();
            logDiag("Created .task directory: " + taskDataDir.getAbsolutePath());
        }
        
        File taskRcFile = new File(context.getFilesDir(), ".taskrc");
        if (!taskRcFile.exists()) {
            try {
                taskRcFile.createNewFile();
                java.io.FileWriter fw = new java.io.FileWriter(taskRcFile);
                fw.write("data.location=" + taskDataDir.getAbsolutePath() + "\n");
                fw.close();
                logDiag("Created .taskrc");
            } catch (IOException e) {
                logDiag("Failed to create .taskrc: " + e.getMessage());
            }
        }
        
        pb.directory(binaryDir);
        pb.environment().put("TASKDATA", taskDataDir.getAbsolutePath());
        pb.environment().put("TASKRC", taskRcFile.getAbsolutePath());
        pb.environment().put("HOME", context.getFilesDir().getAbsolutePath());
        
        // Set PATH to include our binary directory
        String path = pb.environment().get("PATH");
        if (path == null) {
            path = "/sbin:/system/sbin:/system/bin:/system/xbin";
        }
        pb.environment().put("PATH", binaryDir.getAbsolutePath() + ":" + path);
        
        logDiag("TASKDATA: " + taskDataDir.getAbsolutePath());
        logDiag("TASKRC: " + taskRcFile.getAbsolutePath());
        logDiag("PATH: " + pb.environment().get("PATH"));
        
        return pb;
    }
}
