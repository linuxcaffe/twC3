import { TaskController } from './controller.js';
import { MockProvider } from './mock-provider.js';

// Mock the missing dependencies
global.console = console;

// Simple stream classes
class MockStream {
  constructor() {
    this.data = [];
  }
  eat(line) {
    this.data.push(line);
  }
  end() {}
  str() {
    return this.data.join('\n');
  }
}

// Mock formatters (from format.js)
const formatters = {
  description: (task) => task.description,
  id: (task) => task.id.toString(),
  uuid: (task) => task.uuid ? task.uuid.substr(0, 8) : '',
};

// Run the test
async function main() {
  console.log("=== Testing TaskController ===");
  
  const provider = new MockProvider({});
  const controller = new TaskController();
  
  // Manually set the provider (skip init for now)
  controller.provider = provider;
  controller.events = { emit: () => {} };
  controller.multiline = {};
  controller.multilineSep = '\\n';
  
  console.log("\n1. Testing cmd() method...");
  const result = await controller.cmd('add', 'Buy milk due:tomorrow');
  console.log("Add task result:", result ? "Success" : "Failed");
  
  console.log("\n2. Testing filterSimple()...");
  controller.providerInfo = () => ({});
  const tasks = await controller.filterSimple('pending');
  console.log("Filtered tasks:", tasks ? `${tasks.length} tasks` : "Failed");
  
  console.log("\n=== Test Complete ===");
}

main().catch(console.error);
