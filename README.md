README
======

The goal is to build a simple program that counts the following symbols
that represent tasks at various stages.

	[ ] <-- opened
	[X] <-- completed
	[C] <-- cancelled
	[?] <-- deferred

And to do so while iterating through the full git history of a file,
using the git commit time as a timestamp.

The program outputs...

    # secs   days   total    closed    cancelled   deferred   opened
         0      0      10         1            0          0        9

... and so on.

The program is started on the command line as follows...

    $ spm todo.txt

