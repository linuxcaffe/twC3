import { TaskController } from './app/task/controller.js';
import { TaskProvider } from './app/task/provider.js';

// Mock globals
global.dayNames = ['sunday', 'monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday'];
global.specialReports = ['calendar', 'ghistory', 'history', 'stats'];

async function main() {
  console.log("=== TEST: Controller with Correct Structure ===");
  
  try {
    // Create provider with required callbacks
    const provider = new TaskProvider({
      onQuestion: (text, choices) => {
        console.log(`[Question] ${text} -> ${choices}`);
        return Promise.resolve(0); // Choose first option
      },
      onTimer: (type) => {
        console.log(`[Timer] ${type}`);
      },
      onState: (state, mode) => {
        console.log(`[State] ${state} ${mode || ''}`);
      }
    });
    
    // Create controller
    const controller = new TaskController();
    
    console.log("\n1. Initializing...");
    const initResult = await controller.init({ provider });
    console.log("Init result:", initResult);
    
    if (initResult) {
      console.log("\n2. Testing basic commands...");
      
      console.log("\n  a) Adding task...");
      const addResult = await controller.cmd('add', 'Test task from fixed setup');
      console.log("  Add result:", addResult);
      
      console.log("\n  b) Getting tasks...");
      const tasks = await controller.filterSimple('pending');
      console.log(`  Got ${Array.isArray(tasks) ? tasks.length : 'no'} tasks`);
      
      if (tasks && Array.isArray(tasks) && tasks.length > 0) {
        console.log("\n  First task:");
        console.log("    ID:", tasks[0].id);
        console.log("    Description:", tasks[0].description);
        console.log("    Status:", tasks[0].status);
      }
    } else {
      console.log("\nInit failed, testing with manual setup...");
      
      // Manual setup
      controller.provider = provider;
      controller.events = {
        emit: (event, data) => console.log(`[Event] ${event}:`, data)
      };
      
      // Test cmd directly
      console.log("\nTesting cmd() with manual setup...");
      const result = await controller.cmd('add', 'Manual test task');
      console.log("Result:", result);
    }
    
    console.log("\n=== TEST COMPLETE ===");
    
  } catch (error) {
    console.error("\n=== ERROR ===");
    console.error("Message:", error.message);
    
    // Provide helpful debug info
    if (error.stack) {
      const lines = error.stack.split('\n');
      console.error("\nStack trace (first 5 lines):");
      lines.slice(0, 5).forEach(line => console.error("  ", line));
    }
  }
}

main();
