import os

env = Environment()

classpath = []
for i in os.listdir("dependencies"):
	classpath.append("dependencies/" + i)

Java(target = 'out', source = 'src', JAVACFLAGS = [
	"-cp" , ":".join(["out"] + classpath)
])

Java(target = 'test-out', source = 'test-src', JAVACFLAGS = [
	"-cp" , ":".join(["out", "test-out"] + classpath)
])
