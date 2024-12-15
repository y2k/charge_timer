PRELUDE_JS_PATH := $(shell realpath vendor/prelude/js/src/prelude.clj)
PRELUDE_JAVA_PATH := $(shell realpath vendor/prelude/java/src/prelude.clj)

.PHONY: build_web
build_java: build_web
	@ mkdir -p .build/android/app/src/main/java/y2k
	@ mkdir -p .build/android/app/src/main/java/im/y2k/chargetimer
	@ cat vendor/prelude/java/src/RT.java > .build/android/app/src/main/java/y2k/RT.java
	@ clj2js java src/main.android.clj $(PRELUDE_JAVA_PATH) > .build/android/app/src/main/java/im/y2k/chargetimer/Main_android.java
	@ clj2js java src/main.shared.clj $(PRELUDE_JAVA_PATH) > .build/android/app/src/main/java/im/y2k/chargetimer/Main_shared.java

.PHONY: install_apk
run: install_apk
	@ adb shell am start -n 'im.y2k.chargetimer/.Main_android\$$MainActivity'

.PHONY: clean
clean:
	@ rm -rf .build/temp
	@ rm -rf .build/android/app/src/main/java
	@ rm -rf .build/android/app/src/main/assets/js
	@ rm -f .build/android/app/src/main/assets/index.html
	@ rm -f .build/android/app/src/main/assets/sample.json
	@ rm -f .build/android/app/src/main/AndroidManifest.xml

.PHONY: build_dex_reload
build_dex_reload: build_dex
	@ adb root
	@ adb shell "rm -f /data/data/im.y2k.chargetimer/files/classes.dex"
	@ adb push .build/temp/dex/classes.dex /data/data/im.y2k.chargetimer/files/sample.tmp
	@ adb shell "cp /data/data/im.y2k.chargetimer/files/sample.tmp /data/data/im.y2k.chargetimer/files/classes.dex"
	@ adb shell "rm -f /data/data/im.y2k.chargetimer/files/sample.tmp"

.PHONY: build_dex
build_dex:
	@ rm -rf .build/temp/dex_local
	@ mkdir -p .build/temp/dex_local/y2k
	@ cp $(shell dirname $(PRELUDE_JAVA_PATH))/RT.java .build/temp/dex_local/y2k
	@ clj2js java src/main.shared.clj $(PRELUDE_JAVA_PATH) > .build/temp/dex_local/Main_shared.java
	@ rm -rf .build/temp/dex
	@ mkdir -p .build/temp/dex
	@ javac -cp ~/Library/Android/sdk/platforms/android-34/android.jar -sourcepath .build/temp/dex_local -d .build/temp/dex .build/temp/dex_local/Main_shared.java
	@ jar cfv .build/temp/dex/out.jar -C .build/temp/dex .
	@ ~/Library/Android/sdk/build-tools/34.0.0/d8 --min-api 31 --output .build/temp/dex .build/temp/dex/out.jar

.PHONY: install_apk
install_apk: build_web build_resources build_java
	@ docker run --rm -v ${PWD}/.build/temp/android:/root/.android -v ${PWD}/.build/temp/gradle:/root/.gradle -v ${PWD}/.build/android:/target y2khub/cljdroid build
	@ adb install -r .build/android/app/build/outputs/apk/debug/app-debug.apk

.PHONY: build_resources
build_resources:
	@ mkdir -p .build/temp/node && mkdir -p .build/temp/node/runtime
	@ clj2js js src/main.shared.clj $(PRELUDE_JS_PATH) > .build/temp/node/main.shared.js
	@ clj2js js src/runtime/tools.web.clj $(PRELUDE_JS_PATH) > .build/temp/node/runtime/tools.web.js
	@ clj2js js src/main.resources.clj $(PRELUDE_JS_PATH) > .build/temp/node/main.resources.js
	@ echo '{"type":"module"}' > .build/temp/node/package.json
	@ node .build/temp/node/main.resources.js html > .build/android/app/src/main/assets/index.html
	@ node .build/temp/node/main.resources.js manifest > .build/android/app/src/main/AndroidManifest.xml

.PHONY: build_web
build_web:
	@ mkdir -p .build/android/app/src/main/assets/js
	@ cp -f vendor/prelude/js/src/prelude.clj .build/android/app/src/main/assets/js/
	@ cp -f src/main.web.clj .build/android/app/src/main/assets/js/
	@ cd .build/android/app/src/main/assets/js && clj2js js main.web.clj $(PRELUDE_JS_PATH) > main.js && rm *.clj

.PHONY: docker_extract
docker_extract:
	cd .build && rm -rf android && docker run --rm -v ${PWD}/.build/android:/target y2khub/cljdroid copy

.PHONY: log
log:
	clear && adb logcat -T 1 -e FIXME

.PHONY: log_error
log_error:
	clear && adb logcat -T 1 *:W | grep 'im.y2k.chargetimer'

.PHONY: schedule
schedule:
	@ adb shell "cmd jobscheduler run -f 'im.y2k.chargetimer' 123"
