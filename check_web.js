const fs = require('fs');
const html = fs.readFileSync('web/index.html', 'utf8');

// Extract all script content
const scriptRegex = /<script\b[^>]*>([\s\S]*?)<\/script>/gi;
let match;
let scriptContent = '';
while ((match = scriptRegex.exec(html)) !== null) {
  scriptContent += match[1] + '\n';
}

fs.writeFileSync('combined.js', scriptContent);
console.log('Combined JS written, length:', scriptContent.length);

try {
  require('./combined.js');
  console.log('JS syntax is valid!');
} catch (e) {
  console.error('JS Syntax/Runtime Error during load:', e.message);
  console.error(e.stack);
}
