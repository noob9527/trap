# Run this file to run all the tests, once
BASEDIR=$(dirname "$0")/../
$BASEDIR/lib/bats/bin/bats $BASEDIR/test/**/*.bats