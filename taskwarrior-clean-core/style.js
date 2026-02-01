// Style initialization
export function init(css) {
  console.log('styleInit:', css ? Object.keys(css).length + ' properties' : 'no css');
  return css || {};
}

export const colors = {
  red: '#ff0000', green: '#00ff00', blue: '#0000ff',
  yellow: '#ffff00', cyan: '#00ffff', magenta: '#ff00ff'
};
