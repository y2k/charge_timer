run: build build_html build_kotlin
	cd .build/.android && ./gradlew installDebug && adb shell am start -n im.y2k.chargetimer/.MainActivity

build_html: build
	node .build/.android/app/src/main/assets/js/main.js > .build/.android/app/src/main/assets/index.html

build_kotlin:
	clj2js kt src/main.android.clj > .build/.android/app/src/main/java/im/y2k/chargetimer/GeneratedMain.kt

build:
	clj2js src/main.web.clj > .build/.android/app/src/main/assets/js/main.js

docker_build:
	cd .build && docker build --platform linux/amd64 .

.PHONY: run build_html build build_kotlin docker_build
