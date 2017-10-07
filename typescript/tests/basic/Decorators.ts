
test('修饰器第一个实参对静态成员来说是类的构造函数，对实例成员来说则是类的原型对象', () => {
    let staticTarget;
    let instanceTarget;

    class Foo {
        @Static
        static foo1() { }
        @Instance
        foo2() { }
    }

    function Static(target: new () => Foo, name: string) {
        staticTarget = target;
    }
    function Instance(target: object, name: string) {
        instanceTarget = target;
    }

    expect(staticTarget).toBe(Foo);
    expect(instanceTarget).toBe(Foo.prototype);
});

test('如果方法装饰器有返回值，该值将被作为方法的属性描述符', () => {
    class Foo {
        @Bar
        foo() { }
    }

    function Bar(
        target: object,
        name: string,
        descriptor: TypedPropertyDescriptor<() => void>,
    ): TypedPropertyDescriptor<() => void> {
        descriptor.writable = false;
        descriptor.enumerable = false;
        descriptor.configurable = false;
        return {
            value: descriptor.value,
            writable: true,
            enumerable: true,
            configurable: true,
        };
    }

    expect(Object.getOwnPropertyDescriptor(Foo.prototype, 'foo'))
        .toEqual({
            value: Foo.prototype.foo,
            writable: true,
            enumerable: true,
            configurable: true,
        });
});

test('修饰存取器属性', () => {
    class Foo {
        _foo: string = 'foo';

        @Bar
        get foo(): string {
            return this._foo;
        }

        set foo(value: string) {
            this._foo = value;
        }
    }

    function Bar(target: object, name: string, descriptor: TypedPropertyDescriptor<string>) {
        return {
            ...descriptor,
            get: () => 'bar',
            enumerable: false,
            configurable: false,
        };
    }

    // 注意存取器属性跟普通方法一样定义在父类
    const res = Object.getOwnPropertyDescriptor(Foo.prototype, 'foo');
    expect(res.enumerable).toBe(false);
    expect(res.configurable).toBe(false);
    expect((new Foo()).foo).toBe('bar');
});

test('修饰普通属性', () => {
    class Foo {
        @Bar
        foo: string = 'foo';
    }

    function Bar(target: object, name: string) {
        expect(target).toBeTruthy();
        expect(name).toBe('foo');
    }
});

test('修饰方法参数', () => {
    class Foo {
        foo(param0: string, @Bar param1: string) { }
    }

    function Bar(target: object, name: string, index: number) {
        expect(target).toBeTruthy();
        expect(name).toBe('foo');
        expect(index).toBe(1);
    }
});
