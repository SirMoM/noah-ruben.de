# noah-ruben.de

## Development mode
1. Start the wiremock server:
    - Using a Idea run configuration or
    - Using the script in the `wm/` folder:
        ```bash
        ./wm/wm.sh
        ```
1. Start gradle with the countinous building mode:
    ```shell
    gradle -t build -x lintKotlin -x test -i
    ```
1. Run the jar with ktor [Auto-reload](https://ktor.io/docs/server-auto-reload.html) enabled:
    ```shell
    cd ./build/libs
    GITHUB_TOKEN="NOT_NEEDED" GITHUB_URL="http://localhost:42069" java -jar website-dud-0.0.1-standalone.jar
    ```
1. [Optional] Rerun Tailwind compilation if changes are made to the css file:
    ```bash
   cd ./tailwindcss
    find . -name "*.css" | entr bash ./run.sh
    ```
