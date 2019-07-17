// redeclaring a variable in the same scope is allowed in rust
#[test]
fn test100() {
    let x = 0;
    assert_eq!(0, x);
    let x = "foo";
    assert_eq!("foo", x);
}

// variable reference and variable contents are either both mutable or both immutable
#[test]
fn test200() {
    // mutation is disallowed
    let _arr1 = [0, 0, 0];
    // arr1[0] = 1;
    // arr1 = [1, 1, 1];

    // mutation is allowed
    let mut arr2 = [0, 0, 0];
    arr2[0] = 1;
    assert_eq!(arr2, [1, 0, 0]);
    arr2 = [1, 1, 1];
    assert_eq!(arr2, [1, 1, 1]);
}
