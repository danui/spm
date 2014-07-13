README
======

Text files are highly expressive and free-form.  They are great tools
for organising ones thoughts.  TODO items may be embedded within these
thoughts.  For instance:

    Would it not be cool if...  Yes that would be very nice...  We
    should try it!

        [ ] Try doing...   It would be awesome!

Git can be used to manage and synchronise these text files across
systems.  In addtion, a timestamped history of when TODO items are
opened, closed, cancelled, or deferred is automatically maintained by
Git; for each Git commit has a date.

For this to work, however, a consistent set of symbols is required.  As
a proof-of-concept, this program is hard coded to understand the
following symbols:

    [ ] <-- open, not done, todo
    [X] <-- close, done, compelted
    [C] <-- cancelled
    [?] <-- deferred, maybe

When executed, the program steps through the Git history of a file to
count the above symbols to produce output similar to the following.

    #commit         days    total     open    close   cancel    defer
    e5b8bde     0.000000        2        2        0        0        0
    5b10ad0     0.005567        3        2        0        1        0
    e7c4a37     0.006354        4        3        0        1        0
    c8f1f2b     0.018056        6        4        1        1        0
    1c166bc     0.021644        7        5        1        1        0
    973a7d6     3.524097        7        4        2        1        0
    55418df     5.613657        9        5        3        1        0

The above is actually the output of running this program on its own
`TODO` file.  To build and run the program on its `TODO` file, first
build the program using `make`:

    $ make

And then execute `spm.jar` on the `TODO` file.

    $ java -jar spm.jar TODO
