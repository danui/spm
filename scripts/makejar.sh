#!/bin/bash

DEP_DIR=dependencies
SRC_DIR=src
OUT_DIR=out

TOP_DIR=$(pwd)

# Verify that the script is being executed at the top-level directory of
# the project.
function verifyAtTopLevel {
    local i
    for i in $SRC_DIR $DEP_DIR; do
	if [[ ! -d $i ]]; then
	    echo "Not at top-level directory."
	    exit 1
	fi
    done
}

# Extract files from the specified jar.
function extractJar { # jarFile
    (cd $OUT_DIR; jar xf $TOP_DIR/$1;)
}

function extractAllJars {
    local i
    for i in $DEP_DIR/*.jar; do
	extractJar $i
    done
    rm -vrf $OUT_DIR/META-INF
}

function main {
    verifyAtTopLevel
    extractAllJars
    (cd $OUT_DIR; jar cvfm $TOP_DIR/spm.jar $TOP_DIR/manifest.txt *)
}

main
