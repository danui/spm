default: build

CLASS_FILES=						\
	out/com/apfrank/spm/Main.class			\
	out/com/apfrank/spm/BasicPresenter.class	\
	out/com/apfrank/spm/Html5Presenter.class	\
	out/com/apfrank/spm/Commit.class		\
	out/com/apfrank/spm/CommitLog.class		\
	out/com/apfrank/spm/DataPoint.class		\
	out/com/apfrank/spm/FileTools.class		\
	out/com/apfrank/spm/GitTools.class		\
	out/com/apfrank/spm/Path.class			\
	out/com/apfrank/spm/Presenter.class		\
	out/com/apfrank/spm/Project.class		\
	out/com/apfrank/spm/ScrumFilenameFilter.class	\
	out/com/apfrank/spm/SymbolFilter.class		\
	out/com/apfrank/spm/TodoFile.class		\
	out/com/apfrank/spm/UsageException.class	\

build: $(CLASS_FILES)

out/%.class: src/%.java
	mkdir -p $(dir $@)
	javac -sourcepath src -d out -cp out:dependencies/org.eclipse.jgit-3.4.1.201406201815-r.jar $^

spm.jar: build
	bash scripts/makejar.sh
#	jar cvfm spm.jar manifest.txt -C out com

install: spm.jar
	cp -vf spm.jar $(INSTALL_DIR)/.

clean:
	find . -name "*~" -exec rm -vf \{\} \+
	find . -name "*.class" -exec rm -vf \{\} \+
	rm -vf spm.jar
	rm -rf out

doc: build
	javadoc 			\
		-d javadoc-out		\
		-link http://docs.oracle.com/javase/6/docs/api	\
		-classpath out		\
		-sourcepath src		\
		com.apfrank.spm		\
		com.apfrank.json	\
