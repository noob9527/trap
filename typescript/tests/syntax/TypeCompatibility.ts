/* tslint:disable */
test('参数兼容', () => {
    let x = (a: number) => 0;
    let y = (b: number, s: string) => 0;
    // ok
    y = x;
    // error
    // x = y;
});

test('返回值兼容', () => {
    class Parent {
        parent: string;
    }
    class Child extends Parent {
        child: string;
    }
    let x = () => new Child();
    let y = () => new Parent();
    // ok
    y = x;
    // error
    // x = y;
});

test('枚举与数字兼容，但是不同枚举之间不兼容', () => {
    enum Enum1 { e };
    enum Enum2 { e };
    let foo: Enum1;
    let bar: number;
    // correct
    foo = 5;
    bar = Enum1.e;
    // error
    // foo = Enum2.e;
});
