// AndroidProvider - Real implementation for React Native

class AndroidProvider {
  constructor(config) {
    this.config = config;
  }
  
  async init() {
    console.log('AndroidProvider.init');
    
    // For now, mock implementation
    // Later: await NativeModules.TaskwarriorModule.init();
    this.info = {
      version: 'mock-1.0',
      dataLocation: '/data/data/com.taskwarrior/files'
    };
    
    return true;
  }
  
  async call(args, out, err, options = {}) {
    console.log('AndroidProvider.call:', args);
    
    // For now, mock implementation
    // Later: const result = await NativeModules.TaskwarriorModule.executeCommand(args);
    
    // Simulate delay
    await new Promise(resolve => setTimeout(resolve, 100));
    
    // Handle export command
    if (args.includes('export')) {
      const mockTasks = [
        {
          id: 1,
          description: 'Real task would come from Taskwarrior CLI',
          status: 'pending',
          uuid: 'real-uuid-123',
          entry: '2024-01-01T00:00:00Z'
        }
      ];
      
      if (out && out.eat) {
        mockTasks.forEach(task => out.eat(JSON.stringify(task)));
      }
    }
    
    return 0; // Success exit code
  }
  
  configurePanes(conf) {
    console.log('AndroidProvider.configurePanes');
    return conf;
  }
  
  schedule(seconds, type) {
    console.log(`AndroidProvider.schedule: ${type} in ${seconds} seconds`);
  }
  
  editConfig() {
    console.log('AndroidProvider.editConfig');
    return Promise.resolve();
  }
  
  backup() {
    console.log('AndroidProvider.backup');
    return Promise.resolve();
  }
}

module.exports = { AndroidProvider };
