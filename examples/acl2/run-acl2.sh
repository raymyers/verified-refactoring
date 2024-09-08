#!/bin/sh

# Run ACL2, and scan output for "Error" and use exit code accordingly

if [ -z "$1" ]; then
    echo "Usage $0 <file>"
    exit 1
fi
file="$1"

output=$( cat "$file" | acl2 )
status=$?
set -euo pipefail

echo "$output"

if echo "$output" | grep -q '\bError\b'; then
    status=1
fi
exit $status
