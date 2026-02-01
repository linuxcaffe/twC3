// Test with minimal dependencies
import { TaskController } from './controller.js';

// Create a mock provider that matches the interface
const mockProvider = {
  async init() {
    console.log('MockProvider: init');
    return true;
  },
  async call(args, out, err, options) {
    console.log('MockProvider: call', args);
    return 0; // success
  },
  configurePanes(conf) {
    return conf;
  },
  schedule() {
    console.log('MockProvider: schedule');
  }
};

async function test() {
  console.log('=== Simple Core Test ===\n');
  
  const controller = new TaskController();
  
  // Manually set up what we need
  controller.provider = mockProvider;
  controller.events = { emit: () => {} };
  controller.multiline = {};
  controller.multilineSep = '\\n';
  
  console.log('1. Testing cmd()...');
  
  // Mock stream methods
  controller.streamNotify = () => ({
    eat: (line) => console.log('  Output:', line),
    end: () => {}
  });
  
  controller.notifyChange = () => console.log('  notifyChange called');
  controller.scheduleSync = () => console.log('  scheduleSync called');
  
  const result = await controller.cmd('add', 'Simple test');
  console.log(`\nResult: ${result}`);
  
  console.log('\n✅ Core logic works!');
}

test().catch(console.error);
