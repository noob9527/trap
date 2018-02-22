#!./lib/bats/bin/bats

load '../../lib/bats-support/load'
load '../../lib/bats-assert/load'

# test
@test '[](bracket) 等价于 test 命令' {
    run test 1 = 2
    res1="$status"
    run [ 1 = 2 ]
    res2="$status"
    [ $res1 = $res2 ]
}

@test 'bash shell建议使用[[]]替代test命令' {
    [[ 'a' = 'a' ]]
    [[ 'a' != 'b' ]]
}

@test 'string test' {
    foo='foo'
    bar=''
    # 为空或未定义则返回0
    run [ -z $foo ]
    assert_failure
    run [ -z $bar ]
    assert_success
    run [ -z $baz ]
    assert_success

    # 不为空则返回0, dosn't work here, I have no idea
    # run [ -n $foo ]
    # assert_success
    # run [ -n $bar ]
    # assert_failure
    # run [ -n $baz ]
    # assert_failure
}

@test 'regular expression test [[ "string" =~ pattern ]]' {
    [[ "foo" =~ [a-z]{3} ]]
    ! [[ "f-oo" =~ [a-z]{3} ]]
}

@test 'regular expression capture by BASH_REMATCH' {
    [[ "foo-bar" =~ (.*)-(.*) ]]
    assert_equal ${BASH_REMATCH[0]} "foo-bar"
    assert_equal ${BASH_REMATCH[1]} "foo"
    assert_equal ${BASH_REMATCH[2]} "bar"
}

@test 'file test' {
    # [[ -e <FILE> ]] 文件存在
    # [[ -f <FILE> ]] 文件存在且是常规文件
    # [[ -d <FILE> ]] 文件存在且是目录
    # [[ -h <FILE> ]] 文件存在且是符号链接
    # [[ -r <FILE> ]] 文件存在且可读
    # [[ -w <FILE> ]] 文件存在且可写
    # [[ -x <FILE> ]] 文件存在且可执行
    # [[ -s <FILE> ]] 文件存在且不为空
}

# exit
@test '使用exit指定返回值' {
    # 按照约定，脚本成功执行应该返回状态码 0
    run exit 0
    assert_success
    run exit 1
    assert_failure
}
