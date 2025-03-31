const importPlugin = require('eslint-plugin-import');
const depthOfInheritanceTreeRule = require('./depth-of-inheritance-tree');
const sonarjsPlugin = require('eslint-plugin-sonarjs');

module.exports = [
  {
    files: ['**/*.js'],
    plugins: {
      import: importPlugin,
      sonarjs: sonarjsPlugin,
      'custom-rules': { rules: { 'depth-of-inheritance-tree': depthOfInheritanceTreeRule } },
    },
    rules: {
      'no-unused-vars': 'warn',
      'no-console': 'off',
      'import/no-cycle': 'warn',
      'import/max-dependencies': ['warn', { max: 2 }],
    },
  },
];
