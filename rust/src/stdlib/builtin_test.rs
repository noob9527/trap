// assert_eq
#[test]
fn test100() {
    let x = 0;
    assert_eq!(0, x);
}


// test error case with #[should_panic]
#[test]
#[should_panic(expected = "divide by zero")]
fn test200() {
    let x = 0;
    1 / x;
}

// include module only when testing
#[cfg(test)]
mod tests {
    #[test]
    fn test100() {
        let x = 0;
        assert_eq!(0, x);
    }
}

///
/// doc test
/// ```
///     use rust::stdlib::builtin_test;
///     assert_eq!(builtin_test::foo(), "foo")
/// ```
pub fn foo() -> &'static str {
    return "foo";
}

