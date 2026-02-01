export class MockProvider {
  constructor(config) {
    this.config = config;
  }
  
  async init() {
    console.log("MockProvider: initialized");
    return true;
  }
  
  async call(args, out, err, options = {}) {
    console.log("MockProvider: executing", args);
    
    // Simulate task export output
    if (args.includes('export')) {
      const mockTask = {
        id: 1,
        description: "Test task from mock provider",
        status: "pending",
        uuid: "12345678-1234-1234-1234-123456789012",
        entry: "2024-01-01T00:00:00Z"
      };
      
      if (out && out.eat) {
        out.eat(JSON.stringify(mockTask));
      }
    }
    
    // Always return success (exit code 0)
    return 0;
  }
  
  configurePanes(conf) {
    return conf; // Just return the config
  }
  
  schedule(seconds, type, interval, conf) {
    console.log(`MockProvider: scheduled ${type} in ${seconds} seconds`);
  }
}
