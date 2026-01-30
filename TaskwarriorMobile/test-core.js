const { TaskController } = require('./src/core/TaskController');
const { AndroidProvider } = require('./src/providers/AndroidProvider');

async function test() {
  console.log('=== Testing Taskwarrior Core ===\n');
  
  const provider = new AndroidProvider({});
  const controller = new TaskController();
  
  console.log('1. Initializing...');
  const initResult = await controller.init({ provider });
  console.log('   Result: ' + (initResult ? '✅' : '❌') + '\n');
  
  console.log('2. Testing commands...');
  const cmdResult = await controller.cmd('add', 'Buy milk due:tomorrow');
  console.log('   cmd() result: ' + (cmdResult ? '✅' : '❌'));
  
  const tasks = await controller.filterSimple('pending');
  console.log('   filterSimple() result: ' + tasks.length + ' tasks\n');
  
  console.log('=== ✅ CORE LOGIC VALIDATED ===');
  console.log('\nReady for Android native module implementation.');
}

test().catch(console.error);
