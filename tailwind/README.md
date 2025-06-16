# Tailwind CSS

In Tailwind 4 all configuration is made in the css file.

### Problem with Catppuccin
The catppuccin plugin is not v4 ready:
- This is the fix we use:<br>
  https://github.com/catppuccin/tailwindcss/issues/19#issuecomment-2494971455<br>
  It's basically reassigning all the color variables a second time, but it seems to make the classes work
- PR for v4 support: https://github.com/catppuccin/tailwindcss/pull/22

## Compilation
1. Compile css-file into the staticly served folder of the ktor server
   ```shell
    npx @tailwindcss/cli -o ../src/main/resources/static/style.css -i style.css
   ```

1. Copy file into the build output folder that way the started ktor application can serve the new css file:
   ```shell
   cp ../src/main/resources/static/style.css ../build/resources/main/static/
   ```

1. [Optional] Rerun Tailwind compilation if changes are made to the css file:
    ```bash
    find . -name "*.css" | entr bash ./run.sh
    ```
