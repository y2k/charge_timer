run: build_web build_resources build_kotlin
	docker run --rm -v ${PWD}/.build/temp/android:/root/.android -v ${PWD}/.build/temp/gradle:/root/.gradle -v ${PWD}/.build/android:/target y2khub/cljdroid build && \
	adb install -r .build/android/app/build/outputs/apk/debug/app-debug.apk && \
	adb shell am start -n im.y2k.chargetimer/.MainActivity

log:
	clear && adb logcat -T 1 -e FIXME

log_error:
	clear && adb logcat -T 1 *:W

build_resources:
	clj2js src/main.resources.clj > .build/android/app/src/main/assets/js/main.resources.js
	node .build/android/app/src/main/assets/js/main.resources.js > .build/android/app/src/main/assets/index.html

build_kotlin:
	clj2js kt src/main.android.clj > .build/android/app/src/main/java/im/y2k/chargetimer/GeneratedMain.kt

build_web:
	clj2js src/main.web.clj > .build/android/app/src/main/assets/js/main.js

docker_extract:
	cd .build && rm -rf android && docker run --rm -v ${PWD}/.build/android:/target y2khub/cljdroid copy

.PHONY: run build_resources build_web build_kotlin log log_error
