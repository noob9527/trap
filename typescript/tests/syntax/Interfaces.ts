/* tslint:disable */
test('basic usage', () => {
    interface Foo { foo: string; }
    const selectFoo = (foo: Foo): string => foo.foo;
    expect(selectFoo({ foo: 'foo' })).toBe('foo');
});

test('optional properties', () => {
    interface Foo { foo?: () => string; }
    // 下面这行代码应该先判断 foo 是否存在，ts 编译器对此没有任何提示
    const selectFoo = (foo: Foo): string => foo.foo();
    expect(selectFoo({ foo: () => 'foo' })).toBe('foo');
    // 下面这行能通过编译但是执行会报错
    // selectFoo({});
});

test('readonly properties', () => {
    interface Foo { readonly foo: string; }
    const foo: Foo = { foo: 'foo' };
    // error
    // foo.foo = 'bar'
});

describe('ReadonlyArray', () => {
    it('ReadonlyArray as immutable', () => {
        const arr: ReadonlyArray<number> = [1, 2, 3, 4];
        // correct
        arr.slice
        // error
        // arr.splice
    });
    it('ReadonlyArray无法隐式转换到普通数组', () => {
        let arr = [1, 2, 3, 4];
        let roArr: ReadonlyArray<number> = arr;

        // correct
        arr = roArr as number[];
        // error
        // arr = roArr;
    });
});

describe('excess property checks', () => {
    interface Foo { foo: string; }
    let foo: Foo;
    it('当使用对象字面量作为接口实现时，会进行额外的属性检查', () => {
        // correct
        foo = { foo: 'foo' };
        // error
        // foo = { foo: 'foo', bar: 'bar' };
    });
    it('使用类型断言绕开检查', () => {
        foo = { foo: 'foo', bar: 'bar' } as Foo;
    });
    it('使用中间变量绕开检查（不推荐）', () => {
        const tmp = { foo: 'foo', bar: 'bar' };
        foo = tmp;
    });
    it('使用索引签名重定义接口', () => {
        interface Foo {
            foo: string;
            [propName: string]: any;
        }
        let foo: Foo = { foo: 'foo', bar: 'bar' };
    });
});

test('function types', () => {
    interface StringComparator {
        (a: string, b: string): number;
    }
    let byLength: StringComparator = (foo, bar) => foo.length - bar.length;
});

describe('indexable types', () => {
    it('basic sample', () => {
        interface Indexable {
            [index: string]: string;
            [index: number]: string;
        }
        const indexable: Indexable = { 0: 'foo', bar: 'bar' };
        let foo: string = indexable[0];
        let bar: string = indexable['bar']
    });
    it('数字索引必须是字符串索引的子类型', () => {
        class Parent { foo: string }
        class Child extends Parent { bar: string }
        // correct
        interface Foo {
            [index: string]: Parent;
            [index: number]: Child;
        }
        // error
        // interface Bar {
        //     [index: string]: Child;
        //     [index: number]: Parent;
        // }
    });
    it('如果指定了字符串索引，那么对象的所有属性都将与索引类型匹配', () => {
        interface NumberDictionary {
            [index: string]: number;
            length: number;    // 可以，length是number类型
            // name: string       // 错误，`name`的类型与索引类型返回值的类型不匹配
        }
    });
    it('索引可以是只读的', () => {
        interface Readonly {
            readonly [index: number]: string;
        }
        const arr: Readonly = ['foo', 'bar'];
        // error
        // arr[2] = 'baz';
    });
    it('只读的字符串索引没什么用，依然可以使用点操作符写入属性', () => {
        interface Readonly {
            readonly [index: string]: string;
        }
        const obj: Readonly = { foo: 'foo' };
        // correct
        obj.foo = 'bar';
        // error
        // obj['foo'] = 'bar';
        expect(obj.foo).toBe('bar');
    });
});

test('使用new关键字约束构造函数', () => {
    interface PersonConstructor {
        new (name: string): any;
    }
    let ctor: PersonConstructor = class { };
    // correct
    new ctor('1')
    // error
    // new ctor(1);
});

describe('接口继承', () => {
    test('接口继承接口', () => {
        interface Foo { foo: string; }
        interface Bar extends Foo { bar: string; }
        let bar: Bar;
        // correct
        bar = { foo: 'foo', bar: 'bar' }
        // error
        // bar = { bar: 'bar' }
    });
    test('接口继承类', () => {
        class Control {
            private state: any;
        }
        interface SelectableControl extends Control {
            select(): void;
        }
        // correct
        class Button extends Control implements SelectableControl {
            select() { }
        }
        class TextBox extends Control { }
        // error
        // class Image implements SelectableControl {
        //     select() { }
        // }
    });
})

test('hybrid types', () => {
    interface Foo {
        (): string;
        foo: string
        bar(): string;
    }
    let foo: Foo = <Foo>function() { return 'foo'; };
    foo.foo = 'foo';
    foo.bar = () => 'bar';
    expect(foo()).toBe('foo');
});
