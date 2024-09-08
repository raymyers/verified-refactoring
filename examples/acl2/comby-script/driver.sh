#!/bin/sh

# strict mode
set -euo pipefail
IFS=$'\n\t'

mkdir -p output

reference=output/0-reference.lisp

function renameFunction() {
    file=$1
    old=$2
    new=$3
    echo "Rename in $file: \"$old\"->\"$new\""
    runComby "$file" "(defun $old :[body])" "(defun $new :[body])"
}

function runComby() {
    file="$1"
    bakFile="$1.bak"
    a=$2
    b=$3
    cp "$file" "$bakFile"
    comby "$a" "$b" "$file" -in-place
    if ! cmp -s "$file" "$bakFile"; then
        echo "  changed: $a => $b"
    fi
    rm "$bakFile"
}

function specializeName() {
    file=$1
    name=$2
    echo "Specializing $file for name \"$name\""
    runComby $file "(equal name \"$name\")" 't'
    runComby $file '(equal name :[a])' 'nil'
    runComby $file '(not t)' 'nil'
    runComby $file '(not nil)' 't'
    runComby $file '(and nil :[a])' 'nil'
    runComby $file '(and :[a] nil)' 'nil'
    runComby $file '(and t :[a])' ':[a]'
    runComby $file '(and :[a] t)' ':[a]'
   

    runComby $file '(if t :[a] :[b])' ':[a]'
    runComby $file '(if nil :[a] :[b])' ':[b]'
    runComby $file '(if :[cond] :[a] :[a])' ':[a]'

    runComby $file '(let ((:[a] :[a])) :[body])' ':[body]'
    runComby $file '(let ((:[a] :[a]) (:[b] :[c])) :[body])' '(let ((:[b] :[c])) :[body])'
}

function createSpecializedFunction() {
    file=$1
    newFunction=$2
    name=$3
    cp $reference $file
    renameFunction $file update-item $newFunction
    specializeName $file "$name"
    specializeName $file "$name"
    specializeName $file "$name"
    echo
}

function proof() {
    echo "; Refactored code"
    cat output/{0,1,2}*.lisp
    echo
    echo "Verifying with ACL2..."
    output=$( cat output/{0,1,2}*.lisp | acl2 )
    echo "$output"
    if echo "$output" | grep -q '\bError\b'; then
        exit 1
    fi
    echo "Success!"
}

cp original.lisp "$reference"
cp wrapper.lisp "output/2-wrapper.lisp"

createSpecializedFunction "output/1-update-normal.lisp" update-normal "..normal.."
createSpecializedFunction "output/1-update-brie.lisp" update-brie "Aged Brie"
createSpecializedFunction "output/1-update-sulfuras.lisp" update-sulfuras "Sulfuras, Hand of Ragnaros"
createSpecializedFunction "output/1-update-passes.lisp" update-passes "Backstage passes to a TAFKAL80ETC concert"
proof
