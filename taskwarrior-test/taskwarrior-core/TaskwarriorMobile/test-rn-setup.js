// Test if our core logic works in the React Native project context
console.log("=== Testing React Native Setup ===\n");

// Mock React Native globals that our code might expect
global.console = console;

// We'll test the controller directly
import { TaskController } from './src/core/lib/controller.js';
import { TaskProvider } from './src/core/lib/provider.js';

async function test() {
  console.log("1. Testing imports...");
  
  const provider = new TaskProvider({});
  const controller = new TaskController();
  
  console.log("2. Initializing controller...");
  const initResult = await controller.init({ provider });
  console.log(`   Init result: ${initResult}`);
  
  if (initResult) {
    console.log("\n3. Testing basic operations...");
    
    console.log("   a) Adding task...");
    const addResult = await controller.cmd('add', 'Test from React Native setup');
    console.log(`      Result: ${addResult}`);
    
    console.log("\n   b) Loading tasks...");
    const tasks = await controller.filterSimple('pending');
    console.log(`      Loaded ${Array.isArray(tasks) ? tasks.length : 0} tasks`);
    
    if (tasks && tasks.length > 0) {
      console.log("\n   Sample task:");
      console.log(`      ID: ${tasks[0].id}`);
      console.log(`      Description: ${tasks[0].description}`);
    }
  }
  
  console.log("\n=== Setup Test Complete ===");
  console.log("\nNext: Run the app with:");
  console.log("  npx react-native start");
  console.log("  npx react-native run-android");
}

test().catch(console.error);
