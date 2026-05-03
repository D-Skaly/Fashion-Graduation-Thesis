// Change to the frontend directory
process.chdir(__dirname);

// Run vitest from local node_modules
require('./node_modules/vitest/dist/cli.js');