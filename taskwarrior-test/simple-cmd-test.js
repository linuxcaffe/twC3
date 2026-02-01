import { TaskController } from './app/task/controller.js';
import { TaskProvider } from './app/task/provider.js';

async function test() {
  console.log("=== Simple Command Test ===\n");
  
  try {
    // Create provider
    const provider = new TaskProvider({
      onQuestion: () => Promise.resolve(0),
      onTimer: () => {},
      onState: () => {}
    });
    
    // Create controller
    const controller = new TaskController();
    
    // Simple setup (skip full init)
    controller.provider = provider;
    controller.events = { emit: () => {} };
    controller.fixParams = []; // This might be needed
    
    // Mock the stream classes that are defined in controller
    // The controller defines ToStringEater and ToArrayEater classes internally
    // We just need to ensure they're available
    
    console.log("Testing cmd()...");
    
    // The cmd() method calls this.call(), which needs out and err streams
    // Let's create simple mock streams
    const mockOut = {
      eat: (line) => console.log(`  Output: ${line}`),
      end: () => {}
    };
    
    const mockErr = {
      eat: (line) => console.log(`  Error: ${line}`),
      end: () => {}
    };
    
    // Override the call method to use our mocks
    const originalCall = controller.call;
    controller.call = async function(args, out, err, options) {
      console.log(`  call() called with args: ${args.join(' ')}`);
      return 0; // Success
    };
    
    // Also need streamNotify
    controller.streamNotify = () => mockErr;
    controller.notifyChange = () => console.log("  notifyChange()");
    controller.scheduleSync = () => console.log("  scheduleSync()");
    
    const result = await controller.cmd('add', 'Simple test task');
    console.log(`\nResult: ${result}`);
    
    console.log("\n✓ Simple test passed!");
    
  } catch (error) {
    console.error("\n✗ Error:", error.message);
    
    // Show helpful debug info
    if (error.stack) {
      const lines = error.stack.split('\n');
      console.error("\nRelevant stack:");
      lines.slice(0, 6).forEach(line => console.error("  ", line));
    }
  }
}

test();
