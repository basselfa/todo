// test-runner.js
const { execSync } = require('child_process');

try {
  console.log('Running tests...');
  const output = execSync('CI=true npm test', { encoding: 'utf8' });
  console.log(output);
} catch (error) {
  console.error('Test execution failed:');
  console.error(error.message);
  console.error(error.stdout);
}