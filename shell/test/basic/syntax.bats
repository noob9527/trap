#!./lib/bats/bin/bats

load '../../lib/bats-support/load'
load '../../lib/bats-assert/load'

# if statement
@test 'if语句1' {
    function fn() {
        if [[ $1 = 1 ]]; then
            echo 'foo'
        elif [[ $1 = 2 ]]; then
            echo 'bar'
        else
            echo 'baz'
        fi
    }

    assert_equal 'foo' $(fn 1)
    assert_equal 'bar' $(fn 2)
    assert_equal 'baz' $(fn 3)
}

@test 'if语句执行命令返回值为0时执行 true block' {
    local foo
    function fn () {
        return $1
    }
    if fn 0; then
        foo='foo'
    else
        foo='bar'
    fi
    assert_equal $foo 'foo'
    if ! fn 0; then
        foo='foo'
    else
        foo='bar'
    fi
    assert_equal $foo 'bar'
}

@test 'if语句执行取反运算后，无法得到原命令的状态码' {
    function fn1() {
        return 2
    }
    run fn1
    assert_failure

    function fn2() {
        # 这里代码的本意是，如果 fn1 执行失败，则返回 fn1 的执行状态码
        # 但这里其实返回的是 0
        if ! fn1; then
            return $?
        else
            return 1
        fi
    }
    run fn2
    assert_success
}

# case statement
@test 'case语句' {
    function fn() {
        case $1 in
            1)
                echo 'foo'
            ;;
            2|3)
                echo 'bar'
            ;;
            *)
                echo 'baz'
        esac
    }

    assert_equal 'foo' $(fn 1)
    assert_equal 'bar' $(fn 2)
    assert_equal 'bar' $(fn 3)
    assert_equal 'baz' $(fn 4)
}

@test 'case语句 nocasematch' {
    function fn() {
        case $1 in
            foo)
                echo 'foo'
            ;;
            *)
                echo 'baz'
        esac
    }
    assert_equal 'baz' $(fn 'Foo')
    shopt -s nocasematch
    assert_equal 'foo' $(fn 'Foo')
}

# for statement
@test 'for循环1' {
    sum=0
    for i in 1 2 3; do
        let sum+=i
    done
    [[ $sum -eq 6 ]]
}

@test 'for循环2' {
    sum=0
    for (( i = 1; i <= 3; i++)); do
        let sum+=i
    done
    [[ $sum -eq 6 ]]
}

