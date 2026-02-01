// TaskController - CommonJS version

class TaskController {
  constructor() {
    this.data = [];
    this.events = {
      emit: (event, data) => console.log(`Event: ${event}`, data)
    };
    this.fixParams = ['rc.confirmation=off', 'rc.color=off', 'rc.verbose=nothing'];
    this.multiline = {};
    this.multilineSep = '\\n';
  }
  
  async cmd(command, input, tasks = [], silent = false) {
    console.log(`TaskController.cmd: ${command} "${input}"`);
    
    let cmds = [];
    const ids = tasks.map(task => task.id || task.uuid_ || task.uuid);
    if (ids.length) {
      cmds.push(ids.join(','));
    }
    cmds.push(command);
    cmds.push(input);
    
    // For now, simulate success
    if (!silent) {
      this.notifyChange();
      this.scheduleSync('commit');
    }
    
    return true;
  }
  
  async filterSimple(report) {
    console.log(`TaskController.filterSimple: ${report}`);
    
    return [
      {
        id: 1,
        description: 'Test task 1',
        status: 'pending',
        uuid: '123e4567-e89b-12d3-a456-426614174000'
      },
      {
        id: 2,
        description: 'Test task 2',
        status: 'pending',
        uuid: '223e4567-e89b-12d3-a456-426614174001'
      }
    ];
  }
  
  async init(config = {}) {
    console.log('TaskController.init');
    this.provider = config.provider;
    
    if (this.provider && this.provider.init) {
      const success = await this.provider.init();
      if (!success) return false;
    }
    
    this.udas = {};
    this.timers = { normal: 0, error: 0, commit: 0, extra: {} };
    this.panesConfig = { limit: 0 };
    this.calendarConfig = { start: 0, weekends: [0, 6] };
    this.reportExtra = {};
    this.cssConfig = {};
    this.reportsSublist = null;
    this.dueDays = 7;
    this.defaultCmd = 'next';
    
    return true;
  }
  
  streamNotify(evt = 'notify:error') {
    return {
      eat: (line) => console.log(`[${evt}] ${line}`),
      end: () => {}
    };
  }
  
  notifyChange() {
    this.events.emit('change');
  }
  
  scheduleSync(type = 'normal') {
    console.log(`scheduleSync: ${type}`);
    if (this.provider && this.provider.schedule) {
      this.provider.schedule(60, type);
    }
  }
  
  async sync() {
    console.log('sync');
    return true;
  }
  
  async undo() {
    console.log('undo');
    return true;
  }
}

module.exports = { TaskController };
