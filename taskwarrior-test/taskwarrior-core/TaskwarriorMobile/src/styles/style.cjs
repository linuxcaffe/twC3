// Export both colors and init function
const colors = {
  red: '#ff0000',
  green: '#00ff00',
  blue: '#0000ff',
  yellow: '#ffff00',
  cyan: '#00ffff',
  magenta: '#ff00ff',
  black: '#000000',
  white: '#ffffff',
  gray: '#808080'
};

// The init function that controller expects
function init(css) {
  console.log("styleInit called with CSS config");
  // Just accept and return the CSS config
  return css || {};
}
