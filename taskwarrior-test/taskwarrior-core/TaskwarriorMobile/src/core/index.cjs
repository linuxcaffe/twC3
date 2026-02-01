// CommonJS entry point for Taskwarrior core

const { TaskController } = require('./lib/controller.cjs');
const { TaskProvider } = require('./lib/provider.cjs');
const formatters = require('./lib/format.cjs');

module.exports = {
  TaskController,
  TaskProvider,
  formatters,
  // Re-export other utilities as needed
};
