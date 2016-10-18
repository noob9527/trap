/* tslint:disable */

test('泛型约束', () => {
    interface Base {
        value: string;
    }
    interface Foo extends Base {
        foo: string;
    }
    interface Bar extends Base {
        bar: string;
    }
    class Clazz1<T extends Foo | Bar>{
        constructor(arg: T) {
            arg.value
        }
    }
    class Clazz2<T extends Foo & Bar>{
        constructor(arg: T) {
            arg.foo;
            arg.bar;
            arg.value;
        }
    }
});

test('使用类类型', () => {
    class Foo {};
    function createInstance<T>(ctor: { new (): T }): T {
        return new ctor();
    }
    const foo = createInstance(Foo);
    expect(foo instanceof Foo).toBe(true);
});
