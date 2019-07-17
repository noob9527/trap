// Rust consistently permits an extra trailing comma everywhere commas are used.
#[test]
fn test100() {
    assert_eq!((1, 2), (1, 2, ));
}