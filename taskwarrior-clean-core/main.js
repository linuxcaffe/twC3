// Main styles initialization
export const styles = {};

export function init(css, controller) {
  console.log('stylesInit called');
  if (controller) {
    controller.cssConfig = css || {};
  }
}
