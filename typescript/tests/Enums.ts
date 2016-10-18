/* tslint:disable */
test('enum 的实际值是 number', () => {
    enum Color { Red, Green, Blue }
    expect(Color.Red).toBe(0);
});
test('可以手动对enum赋值', () => {
    enum Color { Red = 2, Green = -1, Blue }
    expect(Color.Red).toBe(2);
    expect(Color.Green).toBe(-1);
    expect(Color.Blue).toBe(0);
});
test('可以使用枚举值值得到枚举名', () => {
    enum Color { Red = 2, Green = -1, Blue }
    expect(Color[2]).toBe('Red');
    expect(Color[-1]).toBe('Green');
    expect(Color[Color.Blue]).toBe('Blue');
});

test('常量枚举不允许有计算成员（所有成员必须能在编译期求值）', () => {
    // correct
    const enum Foo {
        Red = 2+0,
    }
    // error
    // const enum Bar {
    //     Red = [].length,
    // }
});

test('常量枚举只存在于编译时，因此无法在运行时获取枚举名', ()=>{
    enum Foo { Red };
    const enum Bar { Red }
    // correct
    Foo[0]
    // error
    // Bar[0]
});
