default: build

build:
	javac SpmMain.java
	jar cvfm spm.jar manifest.txt *.class

install:
	cp -vf spm.jar ~/bin/.

clean:
	rm -vf *~ *.class *.jar
