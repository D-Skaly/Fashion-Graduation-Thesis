const { execSync } = require('child_process');
const path = require('path');

const frontendDir = __dirname;
const rootDir = path.resolve(frontendDir, '..');

// Run vitest from the frontend directory, using the root's vitest
try {
  const result = execSync('npx vitest run --config vitest.config.ts', {
    cwd: frontendDir,
    stdio: 'inherit',
    env: {
      ...process.env,
      NODE_ENV: 'test',
      // Force using the root's node_modules but with frontend as cwd
      PATH: `${rootDir}\\node_modules\\.bin;${process.env.PATH}`,
    },
  });
} catch (error) {
  process.exit(error.status || 1);
}