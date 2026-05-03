import { spawn } from 'child_process';
import { fileURLToPath } from 'url';
import { dirname, resolve } from 'path';

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

// Run vitest using the locally installed version
const vitestPath = resolve(__dirname, 'node_modules', 'vitest', 'dist', 'cli.js');

const args = process.argv.slice(2);
const child = spawn('node', [vitestPath, ...args], {
  cwd: __dirname,
  stdio: 'inherit',
  env: {
    ...process.env,
    NODE_ENV: 'test',
  },
});

child.on('exit', (code) => {
  process.exit(code);
});