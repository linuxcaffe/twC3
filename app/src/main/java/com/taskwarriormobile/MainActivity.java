package com.taskwarriormobile;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "!!!!!!!!!! SIMPLE APP STARTED !!!!!!!!!!");
        
        // Create the simplest possible UI
        android.widget.TextView tv = new android.widget.TextView(this);
        tv.setText("SIMPLE TEST - IT WORKS!");
        setContentView(tv);
        
        Log.d(TAG, "!!!!!!!!!! UI SET !!!!!!!!!!");
    }
}
