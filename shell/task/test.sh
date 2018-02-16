#!/bin/bash
shopt -s globstar

# Run this file to run all the tests, once
BASEDIR=$(dirname $(dirname "$0"))
$BASEDIR/lib/bats/bin/bats $BASEDIR/test/**/*.bats