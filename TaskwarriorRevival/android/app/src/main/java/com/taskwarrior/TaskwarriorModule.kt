package com.taskwarrior

import com.facebook.react.bridge.*
import java.io.*

class TaskwarriorModule(reactContext: ReactApplicationContext) : 
    ReactContextBaseJavaModule(reactContext) {
    
    override fun getName() = "TaskwarriorModule"
    
    @ReactMethod
    fun executeCommand(args: ReadableArray, promise: Promise) {
        try {
            // Build command: "task add 'description' due:tomorrow"
            val cmd = StringBuilder("task")
            for (i in 0 until args.size()) {
                cmd.append(" ").append(args.getString(i))
            }
            
            val process = Runtime.getRuntime().exec(cmd.toString())
            
            // Read output
            val output = BufferedReader(InputStreamReader(process.inputStream))
                .readText()
            val error = BufferedReader(InputStreamReader(process.errorStream))
                .readText()
            val exitCode = process.waitFor()
            
            // Return result
            val result = Arguments.createMap().apply {
                putInt("exitCode", exitCode)
                putString("output", output)
                putString("error", error)
            }
            
            promise.resolve(result)
            
        } catch (e: Exception) {
            promise.reject("EXECUTION_ERROR", e.message, e)
        }
    }
    
    @ReactMethod  
    fun init(promise: Promise) {
        try {
            val info = Arguments.createMap().apply {
                putString("version", "2.6.0")
                putString("dataLocation", "/data/data/com.taskwarrior/files")
            }
            promise.resolve(info)
        } catch (e: Exception) {
            promise.reject("INIT_ERROR", e.message, e)
        }
    }
}
