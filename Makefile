run: build build_html build_kotlin
	cd .android && ./gradlew installDebug && adb shell am start -n im.y2k.chargetimer/.MainActivity

build_html: build
	node .android/app/src/main/assets/js/main.js > .android/app/src/main/assets/index.html

build_kotlin:
	clj2js kt src/main.android.clj > .android/app/src/main/java/im/y2k/chargetimer/GeneratedMain.kt

build:
	clj2js src/main.web.clj > .android/app/src/main/assets/js/main.js

.PHONY: run build_html build build_kotlin
