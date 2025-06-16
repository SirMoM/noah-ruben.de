#npx tailwindcss -i style.css -o ../src/main/resources/static/style.css -c ./tailwind.config
date +"%H:%M:%S.%3N"
npx @tailwindcss/cli -o ../src/main/resources/static/style.css -i style.css
cp ../src/main/resources/static/style.css ../build/resources/main/static/
