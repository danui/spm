default: build

build: spm.jar

CLASS_FILES=					\
	out/com/apfrank/spm/SpmMain.class	\
	out/com/apfrank/spm/SpmData.class	\
	out/com/apfrank/spm/SpmFile.class	\
	out/com/apfrank/spm/SpmCounts.class	\
	out/com/apfrank/util/FileTools.class	\

out/%.class: src/%.java
	mkdir -p $(dir $@)
	javac -sourcepath src -d out -cp out:dependencies/org.eclipse.jgit-3.4.1.201406201815-r.jar $^

spm.jar: $(CLASS_FILES)
	bash scripts/makejar.sh
#	jar cvfm spm.jar manifest.txt -C out com

install:
	cp -vf spm.jar $(INSTALL_DIR)/.

clean:
	find . -name "*~" -exec rm -vf \{\} \+
	find . -name "*.class" -exec rm -vf \{\} \+
	rm -vf spm.jar
	rm -rf out
