//! There are three type of pointer: references, boxes, and unsafe pointers
//!

#![allow(unused_variables)]

// reference and dereference
#[test]
fn test100() {
    let x = 1;
    let pt = &x;

    assert_eq!(*pt, 1);
    assert_eq!(x, 1);
}

// mutable reference
#[test]
fn test110() {
    let mut x = 10;
    let y = &mut x;
    *y += 10;
    assert_eq!(*y, 20);
    assert_eq!(x, 20);
}

// . operator implicitly dereferences its left operand
#[test]
fn test120() {
    struct Foo { name: &'static str };
    let foo = Foo { name: "foo" };
    let foo_ref = &foo;

    assert_eq!(foo.name, "foo");
    assert_eq!(foo_ref.name, "foo");
    assert_eq!((*foo_ref).name, "foo");
}

// . operator implicitly borrow a reference to its left operand
#[test]
fn test130() {
    let mut v = vec![1, 3, 2];
    // they are equivalent
    v.sort();
    (&mut v).sort();

    assert_eq!(v, vec![1, 2, 3]);
}

// . operator follow multiple reference
#[test]
fn test140() {
    struct Point { x: i32 };
    let p = Point { x: 10 };
    let r = &p;
    let rr = &r;
    let rrr = &rr;

    assert_eq!(rrr.x, 10)
}

// compare also see through reference
#[test]
fn test150() {
    let x = 10;
    let y = 10;
    let rx = &x;
    let ry = &y;
    let rrx = &rx;
    let rry = &ry;

    assert!(rx == ry);
    assert!(rrx == rry);
    assert!(rrx >= rry);

    // incorrect,
    // compile error because they are different types
//    assert!(rx != rry);
}

// compare reference strictly
#[test]
fn test160() {
    let x = 10;
    let y = 10;
    let rx = &x;
    let ry = &x;

    let rz = &y;

    assert!(std::ptr::eq(rx, ry));
    assert!(!std::ptr::eq(rz, ry));
}

// allocate a value in the heap
#[test]
fn test200() {
    let t = (12, "eggs");
    let b = Box::new(t);
    dbg!(b);
}

// assignment move ownership
#[test]
fn test300() {
    let s = "foo".to_string();
    let t = s;
    // incorrect
//    let u = s;
}

// assignment doesn't move ownership of "Copy" type
#[test]
fn test310() {
    let s = 1;
    let t = s;
    assert_eq!(s, 1);
    assert_eq!(t, 1);
}

// make your own Copy type
#[test]
fn test320() {
    #[derive(Copy, Clone)]
    struct Label { number: u32 }

    let foo = Label { number: 10 };
    let bar = foo;
    assert_eq!(foo.number, bar.number);
}

// cannot move from indexed content
#[test]
fn test400() {
    let v = vec!["1".to_string(), "2".to_string()];
    // incorrect
//    let a = v[0];
    // correct
    let a = &v[0];
}

// cannot move from indexed content
#[test]
fn test410() {
    let v = vec!["1".to_string(), "2".to_string()];
    // move all s from v
    for s in v {}

    // incorrect
//    let a = v[0];
    // also incorrect
//    let a = &v[0];
}

// whenever a reference type appears inside another type's definition, you must write out its lifetime.
#[test]
fn test500() {
    // incorrect
//    struct Foo { x: &i32 }
    // correct
    struct Bar<'a> { x: &'a i32 }
}

