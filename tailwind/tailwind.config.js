const catppuccin = require('@catppuccin/tailwindcss');

module.exports = {
    content: [
        '../src/main/kotlin/de/noah_ruben/misc/CssClasses.kt',
    ],
    theme: {
        extend: {
            fontFamily: {
                'cascadia': ['"Cascadia Code"', 'monospace'],
            },
        },
    },
    plugins: [
        catppuccin({
            prefix: '',
            defaultFlavour: 'mocha',
        }),
    ],
}
