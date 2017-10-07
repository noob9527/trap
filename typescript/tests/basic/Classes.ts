/* tslint:disable */
test('访问修饰符', () => {
    class Foo {
        static foo: string; // 默认为 public
        public static bar: string;
        protected static baz: string;
        private static qux: string;

        fun() {
            // correct
            Foo.foo
            Foo.bar
            Foo.baz
            Foo.qux
        }
    }

    class Bar extends Foo {
        fun() {
            // correct
            Foo.foo
            Foo.bar
            Foo.baz
            // error
            // Foo.qux
        }
    }

    // correct
    Foo.foo
    Foo.bar
    // error
    // Foo.baz
    // Foo.qux

});

test('如果所有成员类型兼容，则认为两个类型兼容', () => {
    class Foo { name: string; }
    class Bar { name: string; }
    const foo: Foo = new Bar();
});

test(`类型兼容会受到私有与受保护字段的影响,
    这时只有私有或受保护字段来自同一处声明`, () => {
        class Foo {
            private whatever: string;
            name: string;
        }
        class Bar extends Foo {
            name: string;
        }
        class Baz {
            private whatever: string;
            name: string;
        }
        let foo: Foo;
        // correct
        foo = new Bar(); // Bar中的whatever属性来自Foo
        // error
        // foo = new Baz();
    });

test('类的构造函数可以不是公开的', () => {
    class SingleTon {
        private static instance: SingleTon;
        static getInstance(): SingleTon {
            return SingleTon.instance || new SingleTon();
        }
        private constructor() { }
    }
    let singleTon: SingleTon;
    // correct
    singleTon = SingleTon.getInstance();
    // error
    // singleTon = new SingleTon();
});

test('只读属性只能在声明时或在构造函数中赋值', () => {
    class Foo {
        readonly foo = 'foo';
        readonly bar: string;
        readonly baz: string;
        constructor() {
            this.bar = 'bar';
        }
    }
    const foo = new Foo();
    expect(foo.foo).toBe('foo');
    expect(foo.bar).toBe('bar');
    expect(foo.baz).toBeUndefined();
    // error
    // foo.foo = '';
    // foo.baz = '';
});

test('参数属性', () => {
    class Foo {
        foo: string;
        constructor(foo: string) {
            this.foo = foo;
        }
    }
    class Bar {
        constructor(public bar: string) { }
    }
    expect((new Foo('foo')).foo).toBe('foo');
    expect((new Bar('bar')).bar).toBe('bar');
});

test('abstract class(just like java)', () => {
    abstract class Animal {
        abstract makeSound(): void;
        move(): void {
            console.log('roaming the earch...');
        }
    }
});

test('类可以当做接口来使用', ()=>{
    class Foo {
        foo(){ return 'foo'; }
    }
    // correct
    class Bar implements Foo {
        foo(){ return 'bar'; }
    }
    // error
    // class Bar implements Foo {}
});

test('如果一个类有私有成员，那么无法将其作为接口使用', ()=> {
    class Foo {
        private foo(){ return 'foo'; }
    }
    // error
    // class Bar implements Foo {
    //     private foo(){ return 'bar'; }
    // }
});
