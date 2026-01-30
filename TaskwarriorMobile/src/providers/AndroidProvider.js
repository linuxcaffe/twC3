// AndroidProvider - Will use real native module
// For now, we'll check if NativeModules is available

class AndroidProvider {
  constructor(config) {
    this.config = config;
    this.module = null;
    
    // Try to get native module (will only work in React Native)
    if (typeof global !== 'undefined' && global.NativeModules) {
      this.module = global.NativeModules.TaskwarriorModule;
    }
  }
  
  async init() {
    console.log('AndroidProvider.init');
    
    if (this.module) {
      try {
        // Real native module
        this.info = await this.module.init();
        return true;
      } catch (error) {
        console.error('Native module init failed:', error);
      }
    }
    
    // Fallback to mock
    console.log('Using mock provider (no native module)');
    this.info = { version: 'mock-1.0', dataLocation: '/mock/path' };
    return true;
  }
  
  async call(args, out, err, options = {}) {
    console.log('AndroidProvider.call:', args);
    
    if (this.module) {
      try {
        // Use real native module
        const result = await this.module.executeCommand(args);
        
        // Feed output to streams
        if (out && out.eat && result.output) {
          result.output.split('\\n').forEach(line => {
            if (line.trim()) out.eat(line);
          });
        }
        
        if (err && err.eat && result.error) {
          result.error.split('\\n').forEach(line => {
            if (line.trim()) err.eat(line);
          });
        }
        
        return result.exitCode;
        
      } catch (error) {
        console.error('Native module call failed:', error);
      }
    }
    
    // Fallback to mock
    await new Promise(resolve => setTimeout(resolve, 100));
    
    if (args.includes('export')) {
      const mockTasks = [
        {
          id: 1,
          description: 'Mock task (native module not connected)',
          status: 'pending',
          uuid: 'mock-uuid-1'
        }
      ];
      
      if (out && out.eat) {
        mockTasks.forEach(task => out.eat(JSON.stringify(task)));
      }
    }
    
    return 0;
  }
  
  configurePanes(conf) {
    return conf;
  }
  
  schedule(seconds, type) {
    console.log('AndroidProvider.schedule: ' + type + ' in ' + seconds + ' seconds');
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
