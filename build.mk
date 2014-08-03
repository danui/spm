default: build

build: spm.jar

CLASS_FILES=					\
	out/com/apfrank/spm/SpmMain.class	\

out/%.class: src/%.java
	mkdir -p $(dir $@)
	javac -d out $^

spm.jar: $(CLASS_FILES)
	jar cvfm spm.jar manifest.txt -C out com

install:
	cp -vf spm.jar $(INSTALL_DIR)/.

clean:
	find . -name "*~" -exec rm -vf \{\} \+
	find . -name "*.class" -exec rm -vf \{\} \+
	rm -vf spm.jar
	rm -rf out
