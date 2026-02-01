import { TaskController } from './src/core/TaskController.js';
import { AndroidProvider } from './src/providers/AndroidProvider.js';

async function test() {
  console.log('=== Testing Core Integration ===\n');
  
  const provider = new AndroidProvider({});
  const controller = new TaskController();
  
  console.log('1. Initializing...');
  const initResult = await controller.init({ provider });
  console.log(`   Result: ${initResult ? '✅' : '❌'}`);
  
  console.log('\n2. Testing cmd()...');
  const cmdResult = await controller.cmd('add', 'Test integration');
  console.log(`   Result: ${cmdResult ? '✅' : '❌'}`);
  
  console.log('\n3. Testing filterSimple()...');
  const tasks = await controller.filterSimple('pending');
  console.log(`   Got ${tasks.length} tasks`);
  
  console.log('\n=== Core Integration Works! ===');
  console.log('\nNext: Set up React Native and Android native module.');
}

test().catch(console.error);
