import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  Button,
  FlatList,
  StyleSheet,
  ActivityIndicator,
  SafeAreaView
} from 'react-native';

// Import our modules (CommonJS style)
const { TaskController } = require('./src/core/TaskController');
const { AndroidProvider } = require('./src/providers/AndroidProvider');

const App = () => {
  const [controller, setController] = useState(null);
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [status, setStatus] = useState('Initializing...');

  useEffect(() => {
    initializeApp();
  }, []);

  const initializeApp = async () => {
    try {
      setStatus('Creating provider...');
      const provider = new AndroidProvider({});

      setStatus('Creating controller...');
      const ctrl = new TaskController();

      setStatus('Initializing controller...');
      const success = await ctrl.init({ provider });
      
      if (!success) {
        throw new Error('Failed to initialize controller');
      }

      setController(ctrl);
      setStatus('Ready');

      // Load initial tasks
      await loadTasks(ctrl);

    } catch (error) {
      setStatus(`Error: ${error.message}`);
      console.error('Initialization error:', error);
    } finally {
      setLoading(false);
    }
  };

  const loadTasks = async (ctrl) => {
    try {
      setLoading(true);
      const taskList = await ctrl.filterSimple('pending');
      if (Array.isArray(taskList)) {
        setTasks(taskList);
      }
    } catch (error) {
      console.error('Error loading tasks:', error);
    } finally {
      setLoading(false);
    }
  };

  const addTask = async () => {
    if (!controller) return;

    try {
      setLoading(true);
      const description = `Task added at ${new Date().toLocaleTimeString()}`;
      const success = await controller.cmd('add', description);
      
      if (success) {
        setStatus('Task added successfully');
        await loadTasks(controller);
      } else {
        setStatus('Failed to add task');
      }
    } catch (error) {
      setStatus(`Error: ${error.message}`);
      console.error('Error adding task:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading && tasks.length === 0) {
    return (
      <SafeAreaView style={styles.center}>
        <ActivityIndicator size="large" color="#0000ff" />
        <Text style={styles.status}>{status}</Text>
      </SafeAreaView>
    );
  }

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Taskwarrior Revival</Text>
        <Text style={styles.subtitle}>{status}</Text>
        <Text style={styles.taskCount}>{tasks.length} tasks</Text>
      </View>

      <View style={styles.controls}>
        <Button
          title="Add Task"
          onPress={addTask}
          disabled={!controller || loading}
        />
        <Button
          title="Refresh"
          onPress={() => controller && loadTasks(controller)}
          disabled={!controller || loading}
        />
      </View>

      <FlatList
        data={tasks}
        keyExtractor={(item) => item.uuid || item.id.toString()}
        renderItem={({ item }) => (
          <View style={styles.task}>
            <Text style={styles.taskDescription}>{item.description}</Text>
            <View style={styles.taskMeta}>
              <Text style={styles.taskId}>ID: {item.id}</Text>
              <Text style={styles.taskStatus}>{item.status}</Text>
            </View>
          </View>
        )}
        ListEmptyComponent={
          <View style={styles.empty}>
            <Text style={styles.emptyText}>No tasks found</Text>
            <Text style={styles.emptyHint}>Add a task to get started</Text>
          </View>
        }
        style={styles.list}
      />

      <View style={styles.footer}>
        <Text style={styles.footerText}>
          ✅ Core logic working | ⏳ Android module next
        </Text>
      </View>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  center: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
  },
  header: {
    paddingTop: 20,
    paddingHorizontal: 16,
    paddingBottom: 16,
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
    fontSize: 14,
    color: '#666',
    marginTop: 4,
  },
  taskCount: {
    fontSize: 12,
    color: '#888',
    marginTop: 8,
  },
  controls: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    padding: 16,
    backgroundColor: '#fff',
    borderBottomWidth: 1,
    borderBottomColor: '#ddd',
  },
  list: {
    flex: 1,
  },
  task: {
    backgroundColor: '#fff',
    padding: 16,
    marginHorizontal: 16,
    marginVertical: 8,
    borderRadius: 8,
    borderWidth: 1,
    borderColor: '#eee',
  },
  taskDescription: {
    fontSize: 16,
    color: '#333',
    marginBottom: 8,
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
    fontWeight: '500',
  },
  empty: {
    padding: 40,
    alignItems: 'center',
  },
  emptyText: {
    fontSize: 16,
    color: '#999',
  },
  emptyHint: {
    fontSize: 14,
    color: '#ccc',
    marginTop: 8,
  },
  footer: {
    padding: 12,
    backgroundColor: '#f0f0f0',
    borderTopWidth: 1,
    borderTopColor: '#ddd',
  },
  footerText: {
    textAlign: 'center',
    color: '#666',
    fontSize: 12,
  },
  status: {
    marginTop: 16,
    fontSize: 14,
    color: '#666',
    textAlign: 'center',
  },
});

export default App;
