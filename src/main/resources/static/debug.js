/**
 * @typedef {object} HealthData
 * @property {string} version - The version of the application.
 * @property {string} startupTime - The startup time of the application in ISO format.
 */

let startupTime = null;
let intervalId = null;

async function checkHealthAndReload() {
    const response = await fetch('http://localhost:42081/health');
    if (!response.ok) {
        console.log("Automatic reloading for fast development is not possible!");
        return;
    }

    /** @type {HealthData} */
    const data = await response.json();
    const currentStartupTime = data.startupTime;

    if (startupTime === null) {
        startupTime = currentStartupTime;
        console.log("Initial startup time saved:", startupTime);
    } else if (currentStartupTime > startupTime) {
        console.groupCollapsed("Reloading page...");
        console.log("Last saved time:", startupTime)
        console.log("New time:       ", currentStartupTime)
        console.groupEnd()
        clearInterval(intervalId);
        startupTime = null
        window.onload = null;
        window.location.reload();
    }
}

window.onload = async () => {
    await checkHealthAndReload();
    intervalId = setInterval(checkHealthAndReload, 1000); // Save the interval ID
};
