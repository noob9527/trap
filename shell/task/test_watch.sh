# Run this file (with 'entr' installed) to watch all files and rerun tests on changes
BASEDIR=$(dirname "$0")/../
ls -d $BASEDIR/test/**/*.bats | entr $BASEDIR/task/test.sh