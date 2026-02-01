import { TaskController } from './lib/controller.js';
import { TaskProvider } from './lib/provider.js';

console.log("=== Taskwarrior Core Test Suite ===\n");

async function runTests() {
  const tests = [];
  
  // Test 1: Basic instantiation
  tests.push({
    name: "Controller instantiation",
    run: async () => {
      const controller = new TaskController();
      return controller instanceof TaskController;
    }
  });
  
  // Test 2: Provider instantiation
  tests.push({
    name: "Provider instantiation", 
    run: async () => {
      const provider = new TaskProvider({});
      return provider instanceof TaskProvider;
    }
  });
  
  // Test 3: Controller initialization
  tests.push({
    name: "Controller initialization",
    run: async () => {
      const provider = new TaskProvider({});
      const controller = new TaskController();
      const result = await controller.init({ provider });
      return result === true;
    }
  });
  
  // Test 4: Command execution
  tests.push({
    name: "Basic command execution",
    run: async () => {
      const provider = new TaskProvider({});
      const controller = new TaskController();
      await controller.init({ provider });
      const result = await controller.cmd('add', 'Test task');
      return result === true;
    }
  });
  
  // Run all tests
  let passed = 0;
  let failed = 0;
  
  for (const test of tests) {
    try {
      const result = await test.run();
      if (result) {
        console.log(`✓ ${test.name}`);
        passed++;
      } else {
        console.log(`✗ ${test.name} (returned false)`);
        failed++;
      }
    } catch (error) {
      console.log(`✗ ${test.name} (error: ${error.message})`);
      failed++;
    }
  }
  
  console.log(`\n=== Summary: ${passed} passed, ${failed} failed ===`);
  return failed === 0;
}

runTests().then(success => {
  process.exit(success ? 0 : 1);
});
