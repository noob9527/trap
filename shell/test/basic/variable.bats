#!./lib/bats/bin/bats

load '../../lib/bats-support/load'
load '../../lib/bats-assert/load'

# 被名称引用的参数称为变量
# 被数字引用的参数称为位置参数
# 特殊符号引用Bash特殊变量
@test '变量赋值' {
    foo='bar'
    [[ $foo = 'bar' ]]
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
    [[ $bar = "1+1" ]]
}

@test '使用双括号或let关键字获取算式表达式的结果' {
    one=1
    let two=$one+1
    three=$(($two+1))
    notFour=$three+1
    [[ $one -eq 1 ]]
    [[ $two -eq 2 ]]
    [[ $three -eq 3 ]]
    [[ $notFour = "3+1" ]]
}

# Command Substitution
# https://www.gnu.org/software/bash/manual/bash.html#Command-Substitution
@test '使用$()或反引号将命令执行结果赋值给变量' {
    foo=$(echo foo)
    bar=`echo bar`
    [[ $foo = 'foo' ]]
    [[ $foo != 'echo foo' ]]
    [[ $bar = 'bar' ]]
    [[ $bar != 'echo bar' ]]
}

@test '应该尽量使用双引号来引用变量，以避免奇怪的变量解释1' {
    list="foo bar baz"
    count=$((0))
    # $list
    for item in $list
    do
        (( ++count ))
    done
    [[ $count -eq 3 ]]

    # "$list"
    count=$((0))
    for item in "$list"
    do
        (( ++count ))
    done
    [[ $count -eq 1 ]]
}

@test '在函数中应该尽可能使用local关键字定义变量' {
    foo='foo'
    bar='bar'
    function fn() {
        foo='whatever'
        local bar='whatever'
    }
    fn
    assert_equal $foo 'whatever'
    assert_equal $bar 'bar'
}

@test '注意函数的返回值与函数的执行结果的区别' {
    foo() {
        echo 'foo'
        return 0
    }
    # $(command) captures the output of command
    [[ $(foo) = 'foo' ]]
    # ${paramter} subsititutes the paramter with its value
    [[ -z ${foo} ]]
    run foo
    assert_equal $status 0
}
