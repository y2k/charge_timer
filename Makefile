run: before_run build_reload

before_run: build_web build_resources build_kotlin
	docker run --rm -v ${PWD}/.build/temp/android:/root/.android -v ${PWD}/.build/temp/gradle:/root/.gradle -v ${PWD}/.build/android:/target y2khub/cljdroid build && \
	adb install -r .build/android/app/build/outputs/apk/debug/app-debug.apk && \
	adb shell am start -n im.y2k.chargetimer/.MainActivity

build_resources:
	mkdir .build/temp/node || true
	clj2js js prelude > .build/temp/node/prelude.js
	clj2js js src/main.web.clj > .build/temp/node/main.js
	clj2js js src/main.resources.clj > .build/temp/node/main.resources.js
	# clj2js js src/main.resources.manifest.clj > .build/temp/node/main.resources.manifest.js
	echo '{"type":"module"}' > .build/temp/node/package.json
	node .build/temp/node/main.resources.js html > .build/android/app/src/main/assets/index.html
	node .build/temp/node/main.resources.js manifest > .build/android/app/src/main/AndroidManifest.xml

build_reload: build_kotlin
	@ adb push .build/android/app/src/main/assets/sample.json /data/data/im.y2k.chargetimer/files/sample.tmp
	@ adb shell "cp /data/data/im.y2k.chargetimer/files/sample.tmp /data/data/im.y2k.chargetimer/files/sample.json"

build_kotlin: build_interpreter
	@ clj2js kt src/main.android.clj > .build/android/app/src/main/java/im/y2k/chargetimer/main.android.kt

build_interpreter:
	@ # clj2js kt src/interpreter/interpreter.clj > .build/temp/node/interpreter.kt
	@ clj2js json src/main.shared.clj > .build/android/app/src/main/assets/sample.json
	@ clj2js kt src/interpreter/interpreter.clj > .build/android/app/src/main/java/im/y2k/chargetimer/interpreter.kt

build_web:
	clj2js js prelude > .build/android/app/src/main/assets/js/prelude.js
	clj2js js src/main.web.clj > .build/android/app/src/main/assets/js/main.js

docker_extract:
	cd .build && rm -rf android && docker run --rm -v ${PWD}/.build/android:/target y2khub/cljdroid copy

log:
	clear && adb logcat -T 1 -e FIXME

log_error:
	clear && adb logcat -T 1 *:W | grep 'im.y2k.chargetimer'

.PHONY: run before_run build_reload build_resources build_web build_kotlin log log_error
