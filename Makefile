run: build build_html build_kotlin
	docker run -v ${PWD}/.build/temp/android:/root/.android -v ${PWD}/.build/temp/gradle:/root/.gradle -v ${PWD}/.build/android:/target y2khub/cljdroid build && \
	adb install -r .build/android/app/build/outputs/apk/debug/app-debug.apk && \
	adb shell am start -n im.y2k.chargetimer/.MainActivity

log:
	clear && adb logcat -T 1 -e FIXME

log_error:
	clear && adb logcat -T 1 *:W

build_html: build
	node .build/android/app/src/main/assets/js/main.js > .build/android/app/src/main/assets/index.html

build_kotlin:
	clj2js kt src/main.android.clj > .build/android/app/src/main/java/im/y2k/chargetimer/GeneratedMain.kt

build:
	clj2js src/main.web.clj > .build/android/app/src/main/assets/js/main.js

docker_build:
	cd .build && docker build --platform linux/amd64 -t y2khub/cljdroid .

docker_extract:
	cd .build && rm -rf android && docker run -v ${PWD}/.build/android:/target y2khub/cljdroid copy

.PHONY: run build_html build build_kotlin docker_build log log_error
