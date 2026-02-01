// AndroidProvider - CommonJS version

class AndroidProvider {
  constructor(config) {
    this.config = config;
  }
  
  async init() {
    console.log('AndroidProvider.init (mock)');
    this.info = { version: 'mock-1.0', dataLocation: '/mock/path' };
    return true;
  }
  
  async call(args, out, err, options = {}) {
    console.log('AndroidProvider.call (mock):', args);
    
    await new Promise(resolve => setTimeout(resolve, 100));
    
    if (args.includes('export')) {
      const mockTasks = [
        {
          id: 1,
          description: 'Mock task from provider',
          status: 'pending',
          uuid: 'mock-uuid-1',
          entry: '2024-01-01T00:00:00Z'
        }
      ];
      
      if (out && out.eat) {
        mockTasks.forEach(task => out.eat(JSON.stringify(task)));
      }
    }
    
    return 0;
  }
  
  configurePanes(conf) {
    console.log('AndroidProvider.configurePanes:', conf);
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
