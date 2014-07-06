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

        secs         days    total     open    close   cancel    defer
           0     0.000000        2        2        0        0        0
          53     0.000613        2        1        1        0        0
         170     0.001968        4        2        2        0        0
         323     0.003738        4        0        4        0        0
        6635     0.076794        5        1        4        0        0
        6678     0.077292        5        0        5        0        0

... and so on.

The program is started on the command line as follows...

    $ java -jar spm.jar todo.txt
