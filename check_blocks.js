const fs = require('fs');
const html = fs.readFileSync('web/index.html', 'utf8');

const scriptRegex = /<script\b[^>]*>([\s\S]*?)<\/script>/gi;
let match;
let scriptContent = '';
let blockNum = 1;
while ((match = scriptRegex.exec(html)) !== null) {
  try {
    new Function(match[1]);
    console.log(`Block ${blockNum} parsed successfully.`);
  } catch (e) {
    console.error(`Block ${blockNum} syntax error:`, e.message);
  }
  blockNum++;
}
