README
======

SPM counts todo items in text-based todo files that have been stored in
a Git repository and presents summary statistics.

A todo item is written in `.todo` file as follows.

    [ ] Write README.md.

A completed todo item is written as follows.

    [X] Write README.md.

SPM uses the Author timestamp of Git commits to associate time with todo
item counts of each `.todo` file.  With this information SPM provides
summaries for each `.todo` file that includes statistics such as
completion percentage, and duration.

SPM encodes its output in HTML so that it may present plots of
statistics over time.

Example Usage
-------------

Suppose that we have installed `spm.jar` at `~/bin/spm.jar`.  Suppose
that we have purposed a Git repository for todo file tracking at
`~/myproject`.  Then the following should generate a HTML file at
`~/index.html`.

	$ java -jar ~/bin/spm.jar ~/myproject > ~/index.html
