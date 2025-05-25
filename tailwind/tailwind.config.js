const catppuccin = require('@catppuccin/tailwindcss');

module.exports = {
    content: [
        '../src/main/kotlin/de/noah_ruben/misc/CssClasses.kt',
        // Add any other paths to Kotlin files or HTML templates where you use Tailwind classes
        // e.g., './src/main/kotlin/**/*.kt',
        // e.g., './src/main/resources/templates/**/*.html', // if you have any static HTML shells
    ],
    theme: {
        extend: {
            // You can extend the Catppuccin theme or add other customizations here if needed
            // For example, to add custom fonts if Catppuccin doesn't override them:
            fontFamily: {
                'cascadia': ['"Cascadia Code"', 'monospace'], // From your <link> tag
            },
        },
    },
    plugins: [
        catppuccin({
            prefix: 'ctp', // Optional: You can prefix all Catppuccin classes, e.g., ctp-pink. Or set to false or empty string '' for no prefix.
            defaultFlavour: 'mocha', // Choose your preferred Catppuccin flavour: latte, frappe, macchiato, mocha
        }),
    ],
}
