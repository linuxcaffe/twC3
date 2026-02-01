import React, { useState, useEffect } from 'react';
import { 
  View, 
  Text, 
  Button, 
  FlatList, 
  StyleSheet,
  ActivityIndicator 
} from 'react-native';

// Import our core logic
import { TaskController } from './src/core/lib/controller.js';
import { TaskProvider } from './src/core/lib/provider.js';

export default function App() {
  const [controller, setController] = useState(null);
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Initialize controller on app start
  useEffect(() => {
    async function initialize() {
      try {
        console.log('Initializing TaskController...');
        
        // Create provider
        const provider = new TaskProvider({
          onQuestion: (text, choices) => {
            console.log('Question:', text, choices);
            return Promise.resolve(0); // Always choose first option
          },
          onTimer: (type) => console.log('Timer:', type),
          onState: (state, mode) => console.log('State:', state, mode)
        });
        
        // Create controller
        const ctrl = new TaskController();
        
        // Initialize
        const success = await ctrl.init({ provider });
        if (!success) {
          throw new Error('Failed to initialize controller');
        }
        
        setController(ctrl);
        console.log('Controller initialized successfully');
        
        // Load initial tasks
        await loadTasks(ctrl);
        
      } catch (err) {
        console.error('Initialization error:', err);
        setError(err.message);
      } finally {
        setLoading(false);
      }
    }
    
    initialize();
  }, []);

  // Load tasks from controller
  const loadTasks = async (ctrl) => {
    try {
      console.log('Loading tasks...');
      const taskList = await ctrl.filterSimple('pending');
      if (taskList && Array.isArray(taskList)) {
        setTasks(taskList);
        console.log(`Loaded ${taskList.length} tasks`);
      }
    } catch (err) {
      console.error('Error loading tasks:', err);
      setError(err.message);
    }
  };

  // Add a new task
  const addTask = async () => {
    if (!controller) return;
    
    try {
      setLoading(true);
      const description = `Task ${tasks.length + 1} at ${new Date().toLocaleTimeString()}`;
      const success = await controller.cmd('add', description);
      
      if (success) {
        console.log('Task added successfully');
        await loadTasks(controller);
      } else {
        setError('Failed to add task');
      }
    } catch (err) {
      console.error('Error adding task:', err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  // Render loading state
  if (loading) {
    return (
      <View style={styles.centerContainer}>
        <ActivityIndicator size="large" color="#0000ff" />
        <Text style={styles.loadingText}>Initializing Taskwarrior...</Text>
      </View>
    );
  }

  // Render error state
  if (error) {
    return (
      <View style={styles.centerContainer}>
        <Text style={styles.errorText}>Error: {error}</Text>
        <Button title="Retry" onPress={() => window.location.reload()} />
      </View>
    );
  }

  // Render main UI
  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Taskwarrior Mobile</Text>
        <Text style={styles.subtitle}>{tasks.length} tasks</Text>
      </View>
      
      <View style={styles.controls}>
        <Button 
          title="Add Test Task" 
          onPress={addTask}
          disabled={!controller}
        />
        <Button 
          title="Refresh" 
          onPress={() => controller && loadTasks(controller)}
          disabled={!controller}
        />
      </View>
      
      <FlatList
        data={tasks}
        keyExtractor={(item) => item.uuid || item.id?.toString() || Math.random().toString()}
        renderItem={({ item }) => (
          <View style={styles.taskItem}>
            <Text style={styles.taskDescription}>{item.description}</Text>
            <View style={styles.taskMeta}>
              <Text style={styles.taskId}>ID: {item.id}</Text>
              <Text style={styles.taskStatus}>{item.status}</Text>
            </View>
          </View>
        )}
        ListEmptyComponent={
          <View style={styles.emptyContainer}>
            <Text style={styles.emptyText}>No tasks found</Text>
            <Text style={styles.emptySubtext}>Add a task to get started</Text>
          </View>
        }
        style={styles.taskList}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
    paddingTop: 50,
  },
  centerContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
  },
  header: {
    padding: 20,
    backgroundColor: '#fff',
    borderBottomWidth: 1,
    borderBottomColor: '#ddd',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#333',
  },
  subtitle: {
    fontSize: 16,
    color: '#666',
    marginTop: 5,
  },
  controls: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    padding: 15,
    backgroundColor: '#fff',
  },
  taskList: {
    flex: 1,
  },
  taskItem: {
    backgroundColor: '#fff',
    padding: 15,
    marginHorizontal: 10,
    marginVertical: 5,
    borderRadius: 8,
    borderWidth: 1,
    borderColor: '#eee',
  },
  taskDescription: {
    fontSize: 16,
    color: '#333',
    marginBottom: 5,
  },
  taskMeta: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  taskId: {
    fontSize: 12,
    color: '#666',
  },
  taskStatus: {
    fontSize: 12,
    color: '#2196F3',
    fontWeight: 'bold',
  },
  emptyContainer: {
    padding: 40,
    alignItems: 'center',
  },
  emptyText: {
    fontSize: 18,
    color: '#999',
    marginBottom: 10,
  },
  emptySubtext: {
    fontSize: 14,
    color: '#ccc',
  },
  loadingText: {
    marginTop: 20,
    fontSize: 16,
    color: '#666',
  },
  errorText: {
    fontSize: 18,
    color: '#f44336',
    marginBottom: 20,
    textAlign: 'center',
  },
});
