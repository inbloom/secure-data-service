#!/usr/bin/bash
output=$(git diff --relative --name-only HEAD..HEAD^)
echo "$output"
if [ -n "$output" ]; then
    echo moo
fi