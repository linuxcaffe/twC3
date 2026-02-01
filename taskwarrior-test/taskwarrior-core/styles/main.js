// Export styles object
export const styles = {
  // Empty styles for now
};

export function init(css, controller) {
  console.log("stylesInit called");
  if (controller) {
    controller.cssConfig = css || {};
  }
}
