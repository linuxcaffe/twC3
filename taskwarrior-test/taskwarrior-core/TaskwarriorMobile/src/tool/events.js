// Minimal EventEmitter implementation
export class EventEmitter {
  constructor() {
    this.events = {};
  }
  
  emit(event, ...args) {
    if (this.events[event]) {
      this.events[event].forEach(fn => fn(...args));
    }
  }
  
  on(event, fn) {
    if (!this.events[event]) this.events[event] = [];
    this.events[event].push(fn);
  }
  
  removeListener(event, fn) {
    if (this.events[event]) {
      this.events[event] = this.events[event].filter(f => f !== fn);
    }
  }
}
