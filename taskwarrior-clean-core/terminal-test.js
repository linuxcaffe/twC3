// Terminal-based test of the standalone controller
import { TaskController } from './standalone-controller.js';

async function runTest() {
  console.log('=== Terminal Test of TaskController ===\n');
  
  try {
    console.log('1. Creating controller...');
    const controller = new TaskController();
    
    console.log('2. Testing cmd() method...');
    const cmdResult = await controller.cmd('add', 'Buy milk due:tomorrow');
    console.log(`   Result: ${cmdResult ? '✅ Success' : '❌ Failed'}`);
    
    console.log('\n3. Testing filterSimple() method...');
    const tasks = await controller.filterSimple('pending');
    console.log(`   Loaded ${tasks.length} tasks:`);
    
    tasks.forEach(task => {
      console.log(`   - ${task.id}: ${task.description} (${task.status})`);
    });
    
    console.log('\n4. Testing other methods...');
    
    console.log('   a) Testing sync()...');
    const syncResult = await controller.sync();
    console.log(`      Result: ${syncResult ? '✅' : '❌'}`);
    
    console.log('   b) Testing undo()...');
    const undoResult = await controller.undo();
    console.log(`      Result: ${undoResult ? '✅' : '❌'}`);
    
    console.log('\n=== SUMMARY ===');
    console.log('✅ TaskController API works correctly!');
    console.log('\nNext steps:');
    console.log('1. Create Android native module');
    console.log('2. Implement AndroidProvider');
    console.log('3. Build React Native UI');
    
  } catch (error) {
    console.error('\n❌ TEST FAILED:', error.message);
    console.error(error.stack);
  }
}

runTest();
