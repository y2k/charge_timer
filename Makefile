build:
	clj2js src/main.clj > .android/app/src/main/assets/js/main.js

.PHONY: build
