// Just test imports
console.log("=== Testing imports ===");

async function testImport(modulePath) {
  try {
    console.log(`\nTrying to import: ${modulePath}`);
    const module = await import(modulePath);
    console.log(`✓ Success! Exports: ${Object.keys(module).join(', ')}`);
    return true;
  } catch (error) {
    console.log(`✗ Failed: ${error.message}`);
    return false;
  }
}

async function main() {
  console.log("Testing each module...");
  
  await testImport('./app/task/provider.js');
  await testImport('./app/tool/events.js');
  await testImport('./app/styles/style.js');
  await testImport('./app/styles/main.js');
  await testImport('./app/task/format.js');
  
  console.log("\n=== Now trying controller ===");
  try {
    const controllerModule = await import('./app/task/controller.js');
    console.log("✓ Controller imported successfully!");
    console.log("Exports:", Object.keys(controllerModule));
    
    if (controllerModule.TaskController) {
      console.log("\n✓ TaskController class found!");
      console.log("Attempting to create instance...");
      
      const instance = new controllerModule.TaskController();
      console.log("✓ Instance created!");
      
      // Check a few methods
      console.log("\nChecking methods:");
      const proto = Object.getPrototypeOf(instance);
      const methods = Object.getOwnPropertyNames(proto);
      
      methods.filter(m => m !== 'constructor').slice(0, 10).forEach(m => {
        console.log(`  - ${m}`);
      });
    }
    
  } catch (error) {
    console.error("✗ Controller import failed:", error.message);
    
    // Show what import is failing
    if (error.message.includes('Cannot find module')) {
      const match = error.message.match(/Cannot find module '([^']+)'/);
      if (match) {
        console.error(`\nMissing module path: ${match[1]}`);
        console.error("Expected location: app/task/" + match[1].replace('./', ''));
      }
    }
  }
}

main();
