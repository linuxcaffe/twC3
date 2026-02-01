import { TaskController } from './app/task/controller.js';
import { TaskProvider } from './app/task/provider.js';

async function main() {
  console.log("=== Better Controller Test ===\n");
  
  try {
    // 1. Create provider with proper interface
    const provider = new TaskProvider({
      onQuestion: (text, choices) => {
        console.log(`  [Question] ${text}`);
        return Promise.resolve(0);
      },
      onTimer: (type) => console.log(`  [Timer] ${type}`),
      onState: (state, mode) => console.log(`  [State] ${state} ${mode || ''}`)
    });
    
    // 2. Create controller
    console.log("1. Creating controller...");
    const controller = new TaskController();
    
    // 3. Set up minimal properties before init
    console.log("2. Setting up properties...");
    
    // The controller extends EventEmitter, so it should have emit/on methods
    // Let's verify
    console.log("  - Has emit method:", typeof controller.emit === 'function');
    console.log("  - Has on method:", typeof controller.on === 'function');
    
    // 4. Try initialization
    console.log("\n3. Initializing...");
    
    // We need to mock the provider's init to return true
    provider.init = async () => {
      console.log("  Provider init called");
      this.info = { version: "test" };
      return true;
    };
    
    // The init method expects a config object
    const initResult = await controller.init({ provider });
    console.log(`  Init result: ${initResult}`);
    
    if (initResult) {
      console.log("\n4. Testing methods...");
      
      // Test cmd() - this is the main method
      console.log("\n  a) Testing cmd('add', 'Test task')...");
      try {
        const result = await controller.cmd('add', 'Test task from better test');
        console.log(`  Result: ${result}`);
      } catch (cmdError) {
        console.log(`  cmd() error: ${cmdError.message}`);
      }
      
      // Test filterSimple
      console.log("\n  b) Testing filterSimple('pending')...");
      try {
        const tasks = await controller.filterSimple('pending');
        console.log(`  Got ${Array.isArray(tasks) ? tasks.length : 'no'} tasks`);
        if (tasks && tasks.length > 0) {
          console.log(`  First task: ${tasks[0].description}`);
        }
      } catch (filterError) {
        console.log(`  filterSimple() error: ${filterError.message}`);
      }
    }
    
    console.log("\n=== TEST COMPLETE ===");
    
  } catch (error) {
    console.error("\n✗ MAIN ERROR:", error.message);
    console.error("\nStack:");
    const stackLines = error.stack.split('\n');
    stackLines.slice(0, 8).forEach(line => console.error("  ", line));
  }
}

main();
