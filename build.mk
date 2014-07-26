default: build

build: spm.jar

out/%.class: src/%.java
	mkdir -p $(dir $@)
	javac -d out $^

spm.jar: out/com/spm/SpmMain.class
	jar cvfm spm.jar manifest.txt -C out com

install:
	cp -vf spm.jar $(INSTALL_DIR)/.

clean:
	find . -name "*~" -exec rm -vf \{\} \+
	find . -name "*.class" -exec rm -vf \{\} \+
	rm -vf spm.jar
	rm -rf out
