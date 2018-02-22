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

@test 'assert_output --partial(-p)' {
  # to match substring
  run echo 'foo'
  assert_output -p 'f'
  run assert_output -p 'b'
  assert_failure
}

@test 'assert_output --regexp(-e) ' {
  # to test output match extended regular expression
  run echo 'foo'
  run assert_output -e '^[a-z]{3}'
  assert_success
  run assert_output -e '^[a-z]{4}'
  assert_failure
}
