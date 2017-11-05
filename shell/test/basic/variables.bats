#!./lib/bats/bin/bats

load '../../lib/bats-support/load'
load '../../lib/bats-assert/load'

@test '变量赋值' {
    foo=bar
    [[ $foo='bar' ]]
}

@test '赋值等号旁边不允许有空格' {
    # correct
    foo=bar
    # incorrect
    # foo =bar
    # foo= bar
}

@test '默认执行字符串赋值' {
    foo=1
    bar=$foo+1
    [[ $bar = 1+1 ]]
}

@test '使用双括号或let关键字获取算式表达式的结果' {
    one=1
    let two=$foo+1
    three=$((i+1))
    notFour=$three+1
    [[ one=1 ]]
    [[ two=2 ]]
    [[ three=3 ]]
    [[ notFour='3+1' ]]
}

@test '使用$()或反引号将命令执行结果赋值给变量' {
    foo=$(echo foo)
    bar=`echo bar`
    [[ $foo = 'foo' ]]
    [[ $foo != 'echo foo' ]]
    [[ $bar = 'bar' ]]
    [[ $bar != 'echo bar' ]]
}

@test '应该尽量使用双引号来引用变量，以避免奇怪的变量解释' {
    list="foo bar baz"
    count=$((0))
    for item in $list
    do
        (( ++count ))
    done
    [[ $count -eq 3 ]]

    count=$((0))
    for item in "$list"
    do
        (( ++count ))
    done
    [[ $count -eq 1 ]]
}