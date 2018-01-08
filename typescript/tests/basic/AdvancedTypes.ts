/* tslint:disable */
test('intersection types', () => {
    class Foo { foo: string; }
    class Bar { bar: string; }
    let foo = new Foo();
    let bar = new Bar();
    let fb: Foo & Bar;
    // correct
    fb = Object.assign({}, foo, bar);
    // error
    // fb = foo;
    // fb = bar;
});

describe('union types', () => {
    class Foo { foo: string; basic: string }
    class Bar { bar: string; basic: string }
    let foo = new Foo();
    let bar = new Bar();
    let fb: Foo | Bar;

    it('basic usage', () => {
        // correct
        fb = foo;
        fb = bar;
        // error
        // fb = {}
    });

    it('联合类型中只允许使用所有类型共有的属性', () => {
        // correct
        fb.basic;
        (fb as Foo).foo
        // error
        // fb.foo;
    });

    it('type guards', () => {
        function isFoo(fb: Foo | Bar): fb is Foo {
            return !!(fb as Foo).foo;
        }
        // correct
        if (isFoo(fb)) fb.foo;
        // error
        // fb.foo;
    });
});


test('typeof 类型保护', () => {
    const randomType = (): string | number => Math.random() ? '' : 1;
    let foo = randomType();
    // correct
    if (typeof foo === 'string') foo.slice;
    if (typeof foo !== 'number') foo.slice;
    // error
    // foo.slice;
});

test('instanceof 类型保护', () => {
    class Foo { foo: string; basic: string }
    class Bar { bar: string; basic: string }
    const randomType = (): Foo | Bar => Math.random() ? new Foo() : new Bar();
    const fb = randomType();
    // correct
    if (fb instanceof Foo) fb.foo;
    // error
    // fb.foo;
});

test('string literal types', () => {
    type Color = 'red' | 'green' | 'blue';
    let color: Color;
    // correct
    color = 'red';
    // error
    // color = 'black';
});

test('discriminated unions', () => {
    interface Foo {
        whatever: 'foo';    //可辨识特征（普通字符串字面量属性）
        foo: string;
    }
    interface Bar {
        whatever: 'bar';
        bar: string
    }
    interface Baz {
        whatever: 'baz';
        baz: string;
    }
    type T = Foo | Bar | Baz;
    function fun(s: T) {
        switch (s.whatever) {
            case 'foo': return s.foo;
            case 'bar': return s.bar;
            case 'baz': return s.baz;
        }
    }
});

test('索引类型', () => {
    function select<T, K extends keyof T>(target: T, key: K): T[K] {
        // keyof   // index type query operator
        // T[K]    // indexed access operator

        // correct
        return target[key];
        // error
        // return '';
    }

    const foo = {
        foo: 'foo',
        bar: 1,
    }
    // correct
    select(foo, 'foo').slice;
    select(foo, 'bar').toFixed;
    // error
    // select(foo, 'baz');
});

test('mapped types', () => {
    type Readonly<T> = {
        readonly [P in keyof T]: T[P];
    }
    type Partial<T> = {
        [P in keyof T]?: T[P];
    }
    interface Person {
        name: string;
        age: number;
    }
    type PartialPerson = Partial<Person>;
    type ReadonlyPerson = Readonly<Person>;
});

test('ts允许将this作为类型使用', () => {
    abstract class Parent {
        copy(patch?: (target: this) => void): this {
            const ctor = this.constructor as (new () => this);
            const res = new ctor();
            Object.assign(res, this);
            if (patch) patch(res);
            return res;
        }
    }
    class Child extends Parent {
        constructor(
            public foo: string,
            public bar: string,
        ) {
            super();
        }
    }
    const child1 = new Child('foo', 'bar');
    const child2 = child1.copy();
    const child3 = child1.copy(child => child.foo = 'bar');
    expect(child2).not.toBe(child1);
    expect(child2).toEqual(child1);
    expect(child3.foo).toBe('bar');
    expect(child3.bar).toBe('bar');
});
