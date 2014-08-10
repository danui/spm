
find src/com/apfrank/spm -name "*.java"  | cut -d. -f1 | cut -d'/' -f2- | awk '{ print "\tout/" $1 ".class\t\t\\" }'
