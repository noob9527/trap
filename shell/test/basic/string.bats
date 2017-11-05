#!./lib/bats/bin/bats

load '../../lib/bats-support/load'
load '../../lib/bats-assert/load'

@test '不带引号将被解释成字符串' {
    # correct
    foo=bar
    # incorrect
    # foo=bar whatever
    [[ $foo='bar' ]]
}

@test '使用单引号创建直接量' {
    foo='foo'
    bar='$foo'
    [[ bar='$foo' ]]
    [[ bar!="$foo" ]]
}

@test '使用双引号创建"模板字符串"' {
    foo='foo'
    bar="hello$foo"
    baz="${foo}hello"
    [[ bar='hellofoo' ]]
    [[ bar='foohello' ]]
}