// Export styles object
const styles = {
  // Empty styles for now
};

function init(css, controller) {
  console.log("stylesInit called");
  if (controller) {
    controller.cssConfig = css || {};
  }
}
