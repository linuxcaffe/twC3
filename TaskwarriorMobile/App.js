import React, { useState, useEffect } from 'react';
import { View, Text, StyleSheet } from 'react-native';

const { TaskController } = require('./src/core/TaskController');
const { AndroidProvider } = require('./src/providers/AndroidProvider');

export default function App() {
  const [status, setStatus] = useState('Initializing...');
  const [ready, setReady] = useState(false);

  useEffect(() => {
    initialize();
  }, []);

  const initialize = async () => {
    try {
      setStatus('Creating provider...');
      const provider = new AndroidProvider({});
      
      setStatus('Creating controller...');
      const controller = new TaskController();
      
      setStatus('Initializing...');
      const success = await controller.init({ provider });
      
      if (success) {
        setStatus('Ready!');
        setReady(true);
        
        // Test it works
        const tasks = await controller.filterSimple('pending');
        console.log('Initial tasks loaded:', tasks.length);
      } else {
        setStatus('Initialization failed');
      }
    } catch (error) {
      setStatus('Error: ' + error.message);
      console.error('Initialization error:', error);
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Taskwarrior Mobile</Text>
      <Text style={styles.status}>{status}</Text>
      
      {ready && (
        <View style={styles.readyContainer}>
          <Text style={styles.readyText}>✅ Core logic working</Text>
          <Text style={styles.nextSteps}>
            Next:{"\n"}
            1. Android native module{"\n"}
            2. Real provider implementation{"\n"}
            3. Task list UI
          </Text>
        </View>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#f8f9fa',
    padding: 20,
  },
  title: {
    fontSize: 28,
    fontWeight: 'bold',
    marginBottom: 20,
    color: '#212529',
  },
  status: {
    fontSize: 16,
    color: '#6c757d',
    marginBottom: 30,
    textAlign: 'center',
  },
  readyContainer: {
    alignItems: 'center',
    marginTop: 20,
  },
  readyText: {
    fontSize: 18,
    color: '#198754',
    fontWeight: '600',
    marginBottom: 15,
  },
  nextSteps: {
    fontSize: 14,
    color: '#495057',
    lineHeight: 20,
    textAlign: 'center',
  },
});
