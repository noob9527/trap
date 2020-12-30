#!./lib/bats/bin/bats

load '../../lib/bats-support/load'
load '../../lib/bats-assert/load'

@test '使用${N}引用位置参数，如果N只有一位，可以省略大括号' {
    set 10 9 8 7 6 5 4 3 2 1 a
    [[ $1 -eq 10 ]]
    [[ ${10} -eq 1 ]]
    [[ ${11} = 'a' ]]

    i=1
    echo $i
    echo ${!i}
}

@test 'special paramter' {
    set 1 2 3
    assert_equal "$*" "1 2 3"
    # assert_equal "$@" "1 2 3"
    echo "$@" # 1 2 3

    # https://unix.stackexchange.com/questions/92978/what-does-this-2-mean-in-shell-scripting
    echo "${@:1}" # 1 2 3
    echo "${@:2}" # 2 3

    assert_equal $? 0 # 最近一条命令的返回状态码
    assert_equal $# 3 # 参数个数
    [[ $$ -gt 1 ]]  # shell pid
    echo $! # 最近一次执行的后台进程的pid
    echo $0 # shell 或 shell 脚本的名称
}

# built-in parameter
@test 'built-in paramter' {
    assert_equal $BASH '/bin/bash'
    assert_equal $HOME "/home/$USER"
    echo $UID   #用户真实id 不会被 su 命令修改
    echo $IFS   #单词分隔符
    echo $OSTYPE #操作系统类型
    echo $SECONDS #脚本已运行的秒数
    echo $BASH_SOURCE #the paths to called scripts
}

# 参数扩展 Shell Parameter Expansion
@test '使用${!parameter}, 引用“间接”参数. (zsh 不支持该用法)' {
    # ex1
    bar='baz'
    foo=bar
    [[ $foo = 'bar' ]]
    [[ ${!foo} = 'baz' ]]

    # ex2
    set 2
    i=1
    [[ $i -eq 1 ]]
    [[ ${!i} -eq 2 ]]
}

@test '修改大小写' {
    foo='foo'
    bar='BAR'
    assert_equal ${foo^} 'Foo'
    assert_equal ${foo^^} 'FOO'
    assert_equal ${foo~} 'Foo'
    assert_equal ${foo~~} 'FOO'
    assert_equal ${bar,} 'bAR'
    assert_equal ${bar,,} 'bar'
    assert_equal ${bar~} 'bAR'
    assert_equal ${bar~~} 'bar'
}

@test '变量名扩展' {
    APP_FOO='foo'
    APP_BAR='bar'
    assert_equal "${!APP*}" "APP_BAR APP_FOO"
    # assert_equal "${!APP@}" "APP_BAR APP_FOO"
}

@test '字符串移除' {
    str='aa bb cc dd'
    # 从开头移除匹配模式的最短文本
    assert_equal "${str#* }" 'bb cc dd'
    # 从开头移除匹配模式的最长文本
    assert_equal "${str##* }" 'dd'
    # 从结尾移除匹配模式的最短文本
    assert_equal "${str% *}" 'aa bb cc'
    # 从结尾移除匹配模式的最长文本
    assert_equal "${str%% *}" 'aa'
}

@test 'search & replace' {
    str='aaabbb'
    # 替换一次
    assert_equal "${str/a/b}" "baabbb"
    # 替换多次
    assert_equal "${str//a/b}" "bbbbbb"
    # 移除一次
    assert_equal "${str/a}" "aabbb"
    # 移除多次
    assert_equal "${str//a}" "bbb"
}

@test '获取字符串长度' {
    foo='foo'
    assert_equal ${#foo} 3
}

@test '获取subString ${parameter:offset:length}' {
    str='abcde'
    assert_equal ${str:1} 'bcde'
    assert_equal ${str:1:2} 'bc'
}

# ${parameter:-word} 未定义或为空时使用word
# ${parameter-word} 未定义时使用word
@test '默认值1' {
    foo='foo'
    bar=''
    # 值存在
    assert_equal ${foo:-'aaa'} 'foo'
    assert_equal ${foo-'aaa'} 'foo'
    # 值为空
    assert_equal ${bar:-'aaa'} 'aaa'
    assert_equal ${bar-'aaa'} ''
    # 值不存在
    assert_equal ${baz:-'aaa'} 'aaa'
    assert_equal ${baz-'aaa'} 'aaa'
}

# ${parameter:+word}
# ${parameter+word}
@test '默认值2' {
    foo='foo'
    bar=''
    # 值存在
    assert_equal ${foo:+'aaa'} 'aaa'
    assert_equal ${foo+'aaa'} 'aaa'
    # 值为空
    assert_equal ${bar:+'aaa'} ''
    assert_equal ${bar+'aaa'} 'aaa'
    # 值不存在
    assert_equal ${baz:+'aaa'} ''
    assert_equal ${baz+'aaa'} ''
}


