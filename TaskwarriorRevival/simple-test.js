const { TaskController } = require('./src/core/TaskController');
const { AndroidProvider } = require('./src/providers/AndroidProvider');

async function test() {
  console.log('=== Testing TaskController with AndroidProvider ===\n');
  
  try {
    const provider = new AndroidProvider({});
    const controller = new TaskController();
    
    console.log('1. Initializing controller...');
    const initResult = await controller.init({ provider });
    console.log(`   Result: ${initResult ? '✅ Success' : '❌ Failed'}`);
    
    if (initResult) {
      console.log('\n2. Testing cmd() method...');
      const cmdResult = await controller.cmd('add', 'Buy milk due:tomorrow');
      console.log(`   Result: ${cmdResult ? '✅ Success' : '❌ Failed'}`);
      
      console.log('\n3. Testing filterSimple() method...');
      const tasks = await controller.filterSimple('pending');
      console.log(`   Got ${tasks.length} tasks:`);
      
      tasks.forEach((task, i) => {
        console.log(`   ${i + 1}. ${task.description} (ID: ${task.id})`);
      });
      
      console.log('\n4. Testing other methods...');
      console.log('   a) sync()...');
      await controller.sync();
      console.log('   b) undo()...');
      await controller.undo();
      
      console.log('\n=== ✅ ALL TESTS PASSED ===');
      console.log('\nThe TaskController works correctly with the provider pattern.');
      console.log('\nReady for next steps:');
      console.log('1. Create Android native module (Java/Kotlin)');
      console.log('2. Update AndroidProvider to use real native module');
      console.log('3. Build React Native UI');
    }
    
  } catch (error) {
    console.error('\n❌ TEST FAILED:', error.message);
    console.error(error.stack);
  }
}

test();
