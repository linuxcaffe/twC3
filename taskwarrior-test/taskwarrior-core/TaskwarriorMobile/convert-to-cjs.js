const fs = require('fs');
const path = require('path');

function convertFile(filePath) {
  console.log(`Converting ${filePath}...`);
  
  let content = fs.readFileSync(filePath, 'utf8');
  
  // Convert imports
  content = content.replace(
    /import\s+{([^}]+)}\s+from\s+['"]([^'"]+)['"]/g,
    'const {$1} = require("$2");'
  );
  
  content = content.replace(
    /import\s+([^{}\s]+)\s+from\s+['"]([^'"]+)['"]/g,
    'const $1 = require("$2");'
  );
  
  content = content.replace(
    /export\s+class\s+(\w+)/g,
    'class $1'
  );
  
  content = content.replace(
    /export\s+{\s*([^}]+)\s*}/g,
    'module.exports = {$1};'
  );
  
  content = content.replace(
    /export\s+const\s+(\w+)\s*=/g,
    'const $1 ='
  );
  
  content = content.replace(
    /export\s+function\s+(\w+)/g,
    'function $1'
  );
  
  // Add module.exports at the end if not present
  if (!content.includes('module.exports')) {
    // Find exported class names
    const classMatch = content.match(/class\s+(\w+)/g);
    if (classMatch) {
      const classNames = classMatch.map(m => m.replace('class ', ''));
      content += `\n\nmodule.exports = {${classNames.join(', ')}};`;
    }
  }
  
  fs.writeFileSync(filePath.replace('.js', '.cjs'), content);
  console.log(`  -> Created ${filePath.replace('.js', '.cjs')}`);
}

// Convert core files
const files = [
  'src/core/lib/controller.js',
  'src/core/lib/provider.js',
  'src/core/lib/format.js',
  'src/tool/events.js',
  'src/styles/style.js',
  'src/styles/main.js'
];

files.forEach(file => {
  if (fs.existsSync(file)) {
    convertFile(file);
  }
});

console.log('\n=== Conversion complete ===');
console.log('Files converted to CommonJS with .cjs extension');
