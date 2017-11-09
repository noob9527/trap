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

test('使用字符串作为枚举值必须初始化所有枚举值', () => {
    //correct
    enum Color1 { Red = 'RED', Green = 'GREEN', Blue = 'BLUE' }
    //error
    // enum Color2 { Red='RED', Green, Blue }
});

test('字符串枚举无法通过"reverse-mapped"来获取原始枚举名', () => {
    enum Color { Red = 'RED', Green = 'GREEN', Blue = 'BLUE' }
    expect(Color[Color.Blue]).toBeUndefined();
});

test('字符串枚举不能直接分配对应的枚举值', () => {
    enum NumberEnum { Red, Green, Blue }
    enum ConstEnum { Red, Green, Blue }
    enum StringEnum { Red = 'RED', Green = 'GREEN', Blue = 'BLUE' }
    // correct
    const numberEnum1:NumberEnum = NumberEnum.Red;
    const numberEnum2:NumberEnum = 1024;
    const constEnum1:ConstEnum = ConstEnum.Red;
    const constEnum2:ConstEnum = 1024
    const stringEnum1:StringEnum = StringEnum.Red;
    // error
    // const stringEnum2:StringEnum = 'RED';
});

test('常量枚举不允许有计算成员（所有成员必须能在编译期求值）', () => {
    // 常量枚举会在编译器删除,所有成员在使用处内联
    // correct
    const enum Foo {
        Red = 2 + 0,
    }
    // error
    // const enum Bar {
    //     Red = [].length,
    // }
});

test('常量枚举只存在于编译时，因此无法在运行时获取枚举名', () => {
    enum Foo { Red };
    const enum Bar { Red }
    // correct
    Foo[0]
    // error
    // Bar[0]
});
