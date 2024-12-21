PRELUDE_JS_PATH := $(shell realpath vendor/prelude/js/src/prelude.clj)
PRELUDE_JAVA_PATH := $(shell realpath vendor/prelude/java/src/prelude.clj)

.PHONY: build
build: gen_build
	@ export PRELUDE_JAVA=$(PRELUDE_JAVA_PATH) \
		&& export PRELUDE_JS=$(PRELUDE_JS_PATH) \
		&& .build/build.gen.sh
	@ node .build/bin/build/build.js html > .build/android/app/src/main/assets/index.html
	@ node .build/bin/build/build.js manifest > .build/android/app/src/main/AndroidManifest.xml
	@ mkdir -p .build/android/app/src/main/java/y2k \
		&& cp $(shell dirname $(PRELUDE_JAVA_PATH))/RT.java .build/android/app/src/main/java/y2k/RT.java

.PHONE: gen_build
gen_build:
	@ export OCAMLRUNPARAM=b && clj2js make_build_script \
		-lang java \
		-path app \
		-target .build/android/app/src/main/java \
		> .build/build.gen.sh
	@ export OCAMLRUNPARAM=b && clj2js make_build_script \
		-lang js \
		-path web \
		-target .build/android/app/src/main/assets \
		>> .build/build.gen.sh
	@ export OCAMLRUNPARAM=b && clj2js make_build_script \
		-lang js \
		-path build \
		-target .build/bin \
		>> .build/build.gen.sh
	@ chmod +x .build/build.gen.sh

.PHONY: install_apk
install_apk: build
	@ docker run --rm \
		-v ${PWD}/.build/temp/android:/root/.android \
		-v ${PWD}/.build/temp/gradle:/root/.gradle \
		-v ${PWD}/.build/android:/target \
		y2khub/cljdroid build
	@ adb install -r .build/android/app/build/outputs/apk/debug/app-debug.apk
	@ adb shell am start -n 'im.y2k.chargetimer/app.main\$$MainActivity'

.PHONY: clean
clean:
	@ rm -rf .build/temp
	@ rm -rf .build/android/app/src/main/java
	@ rm -rf .build/android/app/src/main/assets/js
	@ rm -f .build/android/app/src/main/assets/index.html
	@ rm -f .build/android/app/src/main/assets/sample.json
	@ rm -f .build/android/app/src/main/AndroidManifest.xml

.PHONY: schedule
schedule:
	@ adb shell "cmd jobscheduler run -f 'im.y2k.chargetimer' 123"
