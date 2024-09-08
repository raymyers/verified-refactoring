
# AC2 Gilded Rose with Comby script

This demonstrates a refactoring workflow on the Gilded Rose kata that is both scripted ("hands free") and formally verified.

## Setup

* [ACL2](https://www.cs.utexas.edu/~moore/acl2)
* [Comby](https://comby.dev/docs/get-started)
* sh (e.g. bash or zsh)

## Run
```sh
./driver.sh
```

This will start with `original.lisp`, use comby to recreate the steps in `output/` and verify equivelence with ACL2 based on the theorem in `wrapper.lisp`.

To see what this looks like without running yourself, check `example-run.txt`.

## Example Comby steps

```sh
comby '(equal name "Aged Brie")' 't' comby.lisp -in-place
comby '(equal name :[a])' 'nil' comby.lisp -in-place

comby '(not t)' 'nil' comby.lisp -in-place
comby '(not nil)' 't' comby.lisp -in-place
comby '(and nil :[a])' 'nil' comby.lisp -in-place
comby '(and :[a] nil)' 'nil' comby.lisp -in-place
comby '(and t :[a])' ':[a]' comby.lisp -in-place
comby '(and :[a] t)' ':[a]' comby.lisp -in-place
comby '(if t :[a] :[b])' ':[a]' comby.lisp -in-place
comby '(if nil :[a] :[b])' ':[b]' comby.lisp -in-place

comby '(let ((:[a] :[a])) :[body])' ':[body]' comby.lisp -in-place
comby '(let ((:[a] :[a]) (:[b] :[c])) :[body])' '(let ((:[b] :[c])) :[body])' comby.lisp -in-place

```