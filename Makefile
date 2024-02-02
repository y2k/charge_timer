run: build build_html
	cd .android && ./gradlew installDebug && adb shell am start -n im.y2k.chargetimer/.MainActivity

build_html: build
	node .android/app/src/main/assets/js/main.js > .android/app/src/main/assets/index.html

build:
	clj2js src/main.clj > .android/app/src/main/assets/js/main.js

.PHONY: run build_html build
