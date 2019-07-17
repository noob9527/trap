use std::mem;

// number literal
#[test]
fn test100() {
    assert_eq!(10, 1_0);
}

// number literal
#[test]
fn test200() {
    let x = 42_u8;
    assert_eq!(mem::size_of_val(&x), mem::size_of::<u8>());
    assert_eq!(mem::size_of_val(&x), 1);
}

// number literal
#[test]
fn test300() {
    assert_eq!(b'A', 65);
}

// bool to int
#[test]
fn test400() {
    assert_eq!(true as i32, 1);
    assert_eq!(false as i32, 0);
}

// char to int
#[test]
fn test500() {
    assert_eq!('A' as i32, 65);
}

