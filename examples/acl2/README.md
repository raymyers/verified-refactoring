# Verified Refactoring With ACL2

Example code is in subdirectories. Here are some hints on running ACL2. 


## Installing ACL2

See the docs on [installing ACL2](https://www.cs.utexas.edu/~moore/acl2/manuals/current/manual/index.html?topic=ACL2S____ACL2S-INSTALLATION-MACOS). Or on MacOS try the [brew formula](https://github.com/acl2s/homebrew-acl2s):

```sh

brew install sbcl stp
brew install acl2s/acl2s/acl2s
```

STP is a SAT solver, SBCL is a Common Lisp implementation.

## Running ACL2 in batch

`./run-acl2.sh` is a convenience script for passing a complete file into `acl2` and passing error status to the exit code. There are probably better ways, please open an issue or PR if you know them.

## Running ACL2 interactively

You'll have a better time with read-line support on the terminal. Install `rlwrap` and run with:

```sh
rlwrap acl2
```

On MacOS you can `brew install rlwrap`.

## Certifying books

This will certify kestrel, which is used in the simplify-defun example.

```sh
cd $ACL2_SYSTEM_BOOKS
make kestrel -j 8
```

If you're using brew, your ACL2_SYSTEM_BOOKS dir may look like this `/opt/homebrew/Cellar/acl2s/0.1.13/opt/acl2s/acl2/books`.

This will take some time, hence the `-j 8` to build in parallel.
