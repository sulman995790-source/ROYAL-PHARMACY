const fs = require('fs');
const html = fs.readFileSync('web/index.html', 'utf8');

const scriptRegex = /<script\b[^>]*>([\s\S]*?)<\/script>/gi;
let match;
let blockNum = 1;
while ((match = scriptRegex.exec(html)) !== null) {
  try {
    new Function(match[1]);
    console.log(`Block ${blockNum} OK`);
  } catch (e) {
    console.error(`Block ${blockNum} ERROR:`, e.message);
    console.error(match[1].slice(-200));
  }
  blockNum++;
}
