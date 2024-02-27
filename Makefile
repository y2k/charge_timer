run: build_web build_resources build_kotlin
	docker run --rm -v ${PWD}/.build/temp/android:/root/.android -v ${PWD}/.build/temp/gradle:/root/.gradle -v ${PWD}/.build/android:/target y2khub/cljdroid build && \
	adb install -r .build/android/app/build/outputs/apk/debug/app-debug.apk && \
	adb shell am start -n im.y2k.chargetimer/.MainActivity

build_resources:
	mkdir .build/temp/node || true
	clj2js prelude > .build/temp/node/prelude.js
	clj2js src/main.clj > .build/temp/node/main.js
	clj2js src/main.resources.clj > .build/temp/node/main.resources.js
	# clj2js src/main.resources.manifest.clj > .build/temp/node/main.resources.manifest.js
	echo '{"type":"module"}' > .build/temp/node/package.json
	node .build/temp/node/main.resources.js html > .build/android/app/src/main/assets/index.html
	node .build/temp/node/main.resources.js manifest > .build/android/app/src/main/AndroidManifest.xml

build_interpreter:
	clj2js kt src/interpreter/interpreter.clj > .build/temp/node/interpreter.kt

build_kotlin:
	clj2js kt src/main.android.clj > .build/android/app/src/main/java/im/y2k/chargetimer/GeneratedMain.kt

build_web:
	clj2js prelude > .build/android/app/src/main/assets/js/prelude.js
	clj2js src/main.clj > .build/android/app/src/main/assets/js/main.js

docker_extract:
	cd .build && rm -rf android && docker run --rm -v ${PWD}/.build/android:/target y2khub/cljdroid copy

log:
	clear && adb logcat -T 1 -e FIXME

log_error:
	clear && adb logcat -T 1 *:W | grep 'im.y2k.chargetimer'

.PHONY: run build_resources build_web build_kotlin log log_error
