.PHONY: test
test: clean build

.PHONY: build
build:
	@ export OCAMLRUNPARAM=b && \
		clj2js compile -target repl -src build/build.clj > .build/build.gen.sh
	@ chmod +x .build/build.gen.sh
	@ .build/build.gen.sh
	@ mkdir -p .build/android/app/src/main/java/y2k
	@ clj2js gen -target java > .build/android/app/src/main/java/y2k/RT.java
	@ docker run --rm \
		-v ${PWD}/.build/temp/android:/root/.android \
		-v ${PWD}/.build/temp/gradle:/root/.gradle \
		-v ${PWD}/.build/android:/target \
		y2khub/cljdroid build

.PHONY: install_apk
install_apk: build
	@ adb install -r .build/android/app/build/outputs/apk/debug/app-debug.apk
	@ adb shell am start -n 'im.y2k.chargetimer/app.main\$$MainActivity'

.PHONY: clean
clean:
	@ rm -rf .build/android/app/src/main/java
	@ rm -rf .build/android/app/src/main/assets/js
	@ rm -f .build/android/app/src/main/assets/index.html
	@ rm -f .build/android/app/src/main/assets/sample.json
	@ rm -f .build/android/app/src/main/AndroidManifest.xml

.PHONY: schedule
schedule:
	@ adb shell "cmd jobscheduler run -f 'im.y2k.chargetimer' 123"
