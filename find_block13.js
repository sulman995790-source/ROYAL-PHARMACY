const fs = require('fs');
const html = fs.readFileSync('web/index.html', 'utf8');

const scriptRegex = /<script\b[^>]*>([\s\S]*?)<\/script>/gi;
let match;
let blockNum = 1;
while ((match = scriptRegex.exec(html)) !== null) {
  if (blockNum === 13) {
    console.log("Block 13 content:\n", match[1]);
  }
  blockNum++;
}
