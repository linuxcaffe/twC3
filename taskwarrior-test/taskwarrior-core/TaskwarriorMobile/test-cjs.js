// CommonJS test
console.log("=== Testing CommonJS Conversion ===\n");

async function runTest() {
  try {
    const core = require('./src/core/index.cjs');
    console.log("1. Core module loaded successfully");
    console.log("   Exports:", Object.keys(core));
    
    const { TaskController, TaskProvider } = core;
    
    console.log("\n2. Creating instances...");
    const provider = new TaskProvider({});
    const controller = new TaskController();
    
    console.log("3. Testing initialization...");
    
    // Mock provider init
    provider.init = async () => {
      console.log("   Provider init called");
      return true;
    };
    
    // We'll test without full init for now
    controller.provider = provider;
    controller.events = { emit: () => {} };
    
    console.log("4. Testing cmd() method...");
    
    // Mock the call method
    controller.call = async (args) => {
      console.log(`   Would execute: ${args.join(' ')}`);
      return 0;
    };
    
    controller.streamNotify = () => ({ eat: () => {}, end: () => {} });
    controller.notifyChange = () => console.log("   notifyChange()");
    controller.scheduleSync = () => console.log("   scheduleSync()");
    
    const result = await controller.cmd('add', 'Test from CommonJS');
    console.log(`\n   Result: ${result}`);
    
    console.log("\n=== SUCCESS: CommonJS conversion works! ===");
    
  } catch (error) {
    console.error("\n=== ERROR ===");
    console.error("Message:", error.message);
    console.error("\nStack:", error.stack);
  }
}

// Run the async test
runTest();
