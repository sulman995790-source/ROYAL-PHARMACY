const fs = require('fs');
const html = fs.readFileSync('web/index.html', 'utf8');

// Extract all event handler attributes like onclick="foo(...)"
const regex = /\b(onclick|onchange|oninput|onsubmit|onkeydown|onkeyup|onchange)\s*=\s*["']([^"']+)["']/gi;
let match;
let handlers = new Set();
while ((match = regex.exec(html)) !== null) {
  let code = match[2];
  // extract function name (first word before '(' or space)
  let funcMatch = code.match(/^([a-zA-Z0-9_$]+)/);
  if (funcMatch) {
    handlers.add(funcMatch[1]);
  }
}

// Extract all defined function names in script blocks
const scriptRegex = /<script\b[^>]*>([\s\S]*?)<\/script>/gi;
let scriptContent = '';
while ((match = scriptRegex.exec(html)) !== null) {
  scriptContent += match[1] + '\n';
}

let missing = [];
for (let fn of handlers) {
  // check if function is defined via function fn or const fn = or window.fn =
  let defRegex1 = new RegExp(`\\bfunction\\s+${fn}\\b`);
  let defRegex2 = new RegExp(`\\bconst\\s+${fn}\\s*=`);
  let defRegex3 = new RegExp(`\\bwindow\\.${fn}\\s*=`);
  let defRegex4 = new RegExp(`\\bvar\\s+${fn}\\s*=`);
  let defRegex5 = new RegExp(`\\blet\\s+${fn}\\s*=`);
  if (!defRegex1.test(scriptContent) && !defRegex2.test(scriptContent) && !defRegex3.test(scriptContent) && !defRegex4.test(scriptContent) && !defRegex5.test(scriptContent)) {
    missing.push(fn);
  }
}

console.log("Missing functions called in event handlers:", Array.from(new Set(missing)));
