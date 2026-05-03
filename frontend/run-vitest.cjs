const { spawn } = require('child_process');
const path = require('path');

const frontendDir = __dirname;
const vitestBin = path.join(frontendDir, 'node_modules', 'vitest', 'dist', 'cli.js');

console.log('Running vitest from:', vitestBin);
console.log('Working directory:', frontendDir);

const args = process.argv.slice(2);
const child = spawn('node', [vitestBin, ...args], {
  cwd: frontendDir,
  stdio: 'inherit',
  shell: true
});

child.on('exit', (code) => {
  process.exit(code);
});