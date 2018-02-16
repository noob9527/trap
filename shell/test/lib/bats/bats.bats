#!./lib/bats/bin/bats

load '../../../lib/bats-support/load'
load '../../../lib/bats-assert/load'

@test 'fail' {
    run fail 'whatever'
    assert_equal $status 1
}

@test 'assert_equal' {
    assert_equal 1 1
    assert_equal 'a' 'a'
}

@test 'assert_success' {
    run exit 0
    assert_success
}

@test 'assert_failure' {
    run exit 1
    assert_failure
    assert_failure 1

    run assert_failure 2
    assert_equal $status 1
}

@test 'assert_output' {
  run echo 'foo'
  assert_output --partial 'f'
  run assert_output --partial 'b'
  assert_equal $status 1
}