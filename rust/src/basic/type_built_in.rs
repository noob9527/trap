#![allow(unused_variables)]

// tuple only allow constants as index
#[test]
fn test100() {
    let x = (1, 2, 3);
    let i = 1;
    // correct
    let y = x.1;
    // incorrect
//    let y = x.i;
//    let y = x[i];
}

// tuple destruct
#[test]
fn test200() {
    let (x, y, z) = (1, 2, 3);
    assert_eq!(x, 1);
    // incorrect
//    (x, y, z) = (4, 5, 6);
//    assert_eq!(x, 4);
}


// zero tuple (aka unit)
// default return type of function is a zero tuple
#[test]
fn test300() {
    fn foo() {}
    fn bar() -> () {}

    assert_eq!(foo(), ());
    assert_eq!(bar(), ());
}

// array literal
#[test]
fn test400() {
    let arr = [1, 2, 3];
    assert_eq!(arr[1], 2);
    assert_eq!(arr.len(), 3);
}

// init array to a specified value
#[test]
fn test500() {
    let arr = [1; 3];
    assert_eq!(arr[0], 1);
    assert_eq!(arr[1], 1);
    assert_eq!(arr[2], 1);
}

// vec! macro is equivalent to call Vec::new then push
#[test]
fn test600() {
    let mut v = Vec::new();
    v.push(1);
    v.push(2);
    v.push(3);
    assert_eq!(v, vec![1, 2, 3]);
}

// create vector with collect method
#[test]
fn test700() {
    let v: Vec<i32> = (1..4).collect();
    assert_eq!(v, vec![1, 2, 3]);
}

// capacity method returns the number of elements a vector could hold
// without reallocation
#[test]
fn test800() {
    let mut v = Vec::with_capacity(10);
    v.push(1);
    v.push(2);

    assert_eq!(v.len(), 2);
    assert_eq!(v.capacity(), 10);
}

// rust automatically convert vec and array reference to slice reference
#[test]
fn test900() {
    let v: Vec<i32> = (1..4).collect();
    let a = [1, 2, 3];
    let sv = &v;
    let sa = &a;

    assert_eq!(sv, sa)
}

// get a range reference to a slice
#[test]
fn test1000() {
    let v: Vec<i32> = (1..6).collect();
    let a = [1, 2, 3, 4, 5];
    let sv = &v;
    let sa = &a;

    assert_eq!(&v[0..3], &a[0..3]);
    assert_eq!(&sv[0..3], &sa[0..3]);
}

// more slice example
#[test]
fn test1100() {
    let a = [1, 2, 3, 4, 5];

    assert_eq!(&a[0..3], [1, 2, 3]);
    assert_eq!(&a[..3], [1, 2, 3]);
    assert_eq!(&a[3..], [4, 5]);
}

// out of range
#[test]
#[should_panic(expected = "out of range")]
fn test1200() {
    let a = [1, 2, 3];
    let s = &a[4..5];
}
