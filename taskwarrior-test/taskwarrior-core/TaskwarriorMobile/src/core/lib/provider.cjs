class TaskProvider {
  constructor(config) {
    this.config = config;
  }
  
  async init() {
    console.log("TaskProvider.init() called");
    this.info = { version: "mock-1.0" };
    return true;
  }
  
  async call(args, out, err, options = {}) {
    console.log("TaskProvider.call() args:", args);
    
    // Handle different command types
    if (args.includes('export')) {
      // Return mock task data
      const mockTasks = [
        {
          id: 1,
          description: "Mock task 1",
          status: "pending",
          uuid: "11111111-1111-1111-1111-111111111111",
          entry: "2024-01-01T00:00:00Z"
        },
        {
          id: 2,
          description: "Mock task 2", 
          status: "pending",
          uuid: "22222222-2222-2222-2222-222222222222",
          entry: "2024-01-02T00:00:00Z"
        }
      ];
      
      if (out && out.eat) {
        mockTasks.forEach(task => out.eat(JSON.stringify(task)));
      }
    }
    
    // Simulate successful execution
    setTimeout(() => {
      if (options.flush && out && out.data) {
        options.flush();
      }
    }, 10);
    
    return 0; // Success exit code
  }
  
  configurePanes(conf) {
    console.log("configurePanes called");
    return conf;
  }
  
  schedule(seconds, type, interval, conf) {
    console.log(`schedule: ${type} in ${seconds} seconds`);
  }
  
  editConfig() {
    console.log("editConfig called");
    return Promise.resolve();
  }
  
  backup() {
    console.log("backup called");
    return Promise.resolve();
  }
}


module.exports = {TaskProvider};