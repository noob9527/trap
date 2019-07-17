// use backslash as escape sequence
#[test]
fn test100() {
    let foo = "\"foo\"";

    assert!(foo.contains('"'));
}

// string span multiline
#[test]
fn test200() {
    let str1 = "foo
        bar";
    let str2 = "foo\
        bar";

    assert!(str1.contains('\n'));
    assert!(!str2.contains('\n'));
}

// raw string
// characters inside a raw string are included verbatim in the string
#[test]
fn test300() {
    let str = "C:\\Program FIles\\";
    let raw = r"C:\Program FIles\";
    assert_eq!(raw, str);
}

// double quote in raw string
// characters inside a raw string are included verbatim in the string
#[test]
fn test400() {
    // correct
    let raw = r###"she wask like "oh my god""###;
    assert!(raw.contains("\"oh my god\""));
    // incorrect
//    let raw = r"she wask like "oh my god"";
}
