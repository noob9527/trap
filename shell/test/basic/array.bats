#!./lib/bats/bin/bats

load '../../lib/bats-support/load'
load '../../lib/bats-assert/load'

@test '声明数组' {
    arr1[0]='foo'
    arr1[1]='bar'

    arr2=('foo' 'bar')
    declare -a arr3=('foo' 'bar')

    assert_equal ${arr1[0]} foo
    assert_equal ${arr1[1]} bar
    assert_equal ${arr2[0]} foo
    assert_equal ${arr2[1]} bar
    assert_equal ${arr3[0]} foo
    assert_equal ${arr3[1]} bar
}

# $* and $@
# Unquoted, the results are unspecified. In Bash,
# both expand to separate args and then wordsplit and globbed.
# Quoted, "$@" expands each element as a separate argument,
# while "$*" expands to the args merged into one argument:
# "$1c$2c..."(where c is the first char of IFS).
@test '引用数组所有成员' {
    join_by (){
        local IFS="$1";
        shift;
        echo "$*";
    }
    arr=('foo' 'bar')
    assert_equal "$(join_by ' ' ${arr[@]})" 'foo bar'
    assert_equal "$(join_by ' ' ${arr[*]})" 'foo bar'
}
