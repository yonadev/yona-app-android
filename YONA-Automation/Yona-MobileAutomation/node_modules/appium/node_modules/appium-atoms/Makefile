current_dir = $(shell pwd)

DEFAULT: clone_selenium atoms

clone_selenium:
	mkdir -p tmp
	rm -rf tmp/selenium
	git clone https://code.google.com/p/selenium tmp/selenium
	cd tmp/selenium && git checkout selenium-2.39.0

atoms:
	rm -rf atoms
	cd tmp/selenium && ./go clean
	mkdir atoms
	minify=true ./import_atoms.sh $(current_dir)/tmp/selenium $(current_dir)/atoms

.PHONY: \
	DEFAULT \
	clone_selenium \
	atoms
