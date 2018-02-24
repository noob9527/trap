import test from 'ava';
import chai from 'chai';

const should = chai.should();
Reflect.defineProperty(Object.prototype, 'log', {
    get: function () {
        console.log(this);
    }
});

test('class关键字只是语法糖，其数据类型是函数', t => {
    class Person { }
    (typeof Person).should.equal('function');
    Person.should.equal(Person.prototype.constructor);
});

test('类的方法都定义在函数的prototype中', t => {
    class Foo {
        foo() {
            return 'foo';
        }
    }
    const foo = new Foo();
    foo.foo().should.equal('foo');
    Foo.prototype.foo().should.equal('foo');
    Reflect.deleteProperty(Foo.prototype, 'foo').should.true;
    Foo.prototype.bar = () => 'bar';
    should.not.exist(foo.foo);
    foo.bar().should.equal('bar');
});

test('类里面定义的方法都是不可枚举的', function () {
    class Foo {
        foo() { }
    }
    Foo.prototype.bar = () => 'bar'; //如果使用这种方法为'类'添加方法，该方法是可枚举属性
    Reflect.getOwnPropertyDescriptor(Foo.prototype, 'foo')
        .enumerable.should.false;
    Reflect.getOwnPropertyDescriptor(Foo.prototype, 'bar')
        .enumerable.should.true;
});

test('类的数据类型虽然是函数，但它必须使用new关键字调用', t => {
    class Foo { }
    (typeof Foo).should.equal('function');
    t.throws(() => Foo(), "Class constructor Foo cannot be invoked without 'new'");
});

test('类的构造函数如果显式返回值，则返回的对象不再是该类的实例，这与直接使用构造函数一致', t => {
    class Foo { }
    class Bar {
        constructor() {
            return new Foo();
        }
    }
    function Baz() {
        return new Foo();
    }
    (new Bar() instanceof Bar).should.false;
    (new Bar() instanceof Foo).should.true;
    (new Baz() instanceof Baz).should.false;
    (new Baz() instanceof Foo).should.true;
});

test('类的定义不存在变量提升', t => {
    should.exist(Foo);
    t.throws(() => Bar, 'Bar is not defined');
    function Foo() { }
    class Bar { }
});

test('与函数一样，可以使用表达式形式定义类', t => {
    const Foo = class { };
    (new Foo() instanceof Foo).should.true;
});

test('使用表达式时也可以为类指定名称，但该名称只能在类的内部使用', t => {
    const Foo = class Bar {
        getClassName() {
            return Bar.name;
        }
    }
    t.throws(() => Bar, 'Bar is not defined');
    (new Foo()).getClassName().should.equal('Bar');
});

test('类与函数一样带有name属性，且该属性优先返回紧跟在class关键字后的类名', t => {
    const Foo1 = class { }
    Foo1.name.should.equal('Foo1');
    const Foo2 = class Bar { }
    Foo2.name.should.equal('Bar');
});

test('使用表达式定义类，可以写出立即执行的类', t => {
    const foo = new class {
        constructor(name) {
            this.name = name;
        }
    }('foo');
    foo.name.should.equal('foo');
});

test('类的方法内部的this指向该类的实例，因此如果用到this, 则该方法不能作为函数调用', t => {
    class Foo {
        foo() {
            return this.bar();
        }
        bar() {
            return 'bar';
        }
    }
    let fn = (new Foo()).foo;
    t.throws(fn, "Cannot read property 'bar' of undefined");
    //一种解决方案是使用bind绑定this
    class Foo1 {
        constructor() {
            this.foo = this.foo.bind(this);
        }
        foo() {
            return this.bar();
        }
        bar() {
            return 'bar';
        }
    }
    fn = (new Foo1()).foo;
    fn().should.equal('bar');
    //另一种方法是使用箭头函数
    class Foo2 {
        constructor() {
            this.foo = () => this.bar();
        }
        bar() {
            return 'bar';
        }
    }
    fn = (new Foo2()).foo;
    fn().should.equal('bar');
});

//class extend
test('使用extend关键字实现继承', t => {
    class Parent {
        constructor(name) {
            this.name = name;
        }
        sayName() {
            return this.name;
        }
    }
    //es5继承
    const Child1 = function (name) {
        this.name = name;
    }
    //Child1的实例继承Parent的实例
    Child1.prototype.__proto__ = Parent.prototype;
    const child1 = new Child1('foo');
    child1.sayName().should.equal('foo');
    (child1 instanceof Parent).should.true;
    //es6
    class Child2 extends Parent {
    }
    const child2 = new Child2('foo');
    child2.sayName().should.equal('foo');
    (child2 instanceof Parent).should.true;
});

test('子类如果有自己的构造函数，则必须在该函数中调用父类构造函数', t => {
    class Parent {
    }
    class Child1 extends Parent {
    }
    //如果子类没有定义constructor ,会有如下的默认构造函
    // constructor(...args) {
    //     super(...args);
    // }
    class Child2 extends Parent {
        constructor() {
        }
    }
    class Child3 extends Parent {
        constructor() {
            super();
        }
    }
    new Child1().should.exist;
    t.throws(() => new Child2());
    new Child3().should.exist;
});

test('子类构造函数只有调用父类构造函数后，才能获取this引用', t => {
    class Parent {
    }
    class Child extends Parent {
        constructor() {
            should.not.exist(this);
            super();
            this.should.exist;
        }
    }
});

test('类继承的实现', t => {
    class Parent {
    }
    class Child extends Parent {
    }
    Reflect.getPrototypeOf(Child.prototype).should.equal(Parent.prototype);
    Reflect.getPrototypeOf(Child).should.equal(Parent);
});

//super关键字
test('子类中的super关键字指向父类的原型对象', t => {
    class Parent {
    }
    class Child extends Parent {
        constructor() {
            super();
            super.should.equal(Parent.prototype);
        }
    }
});

test('使用super调用父类方法时，this指向子类的实例', t => {
    class Parent {
        name() {
            return this.name;
        }
    }
    class Child extends Parent {
        constructor() {
            super();
            this.name = 'child';
        }
        name() {
            super.name().should.equal('child');
        }
    }
});

test('在子类中对super属性赋值，实际上等价于对this赋值', t => {
    class Parent {
    }
    class Child extends Parent {
        constructor() {
            super();
            this.x = 1;
            super.x = 2;
            should.not.exist(super.x)
            this.x.should.equal(2);
        }
    }
    new Child();
});

test('es6 class能够正确继承原生的构造函数', t => {
    function ES5Array() {
        Array.apply(this, arguments);
    }
    ES5Array.prototype.__proto__ = Array.prototype;
    //es5得到的行为与Array不一致
    let arr = new ES5Array();
    arr[0] = 1;
    arr.length.should.equal(0);
    //es6正常
    class ES6Array extends Array { }
    arr = new ES6Array();
    arr[0] = 1;
    arr.length.should.equal(1);
});

//static
test('使用static关键字定义静态方法，静态方法可以被子类继承', t => {
    class Foo {
        static foo() {
            return 'foo';
        }
    }
    class Bar extends Foo { }
    Foo.foo().should.equal('foo');
    should.not.exist((new Foo).foo);
    Bar.foo().should.equal('foo');
});

test('使用static关键字定义静态属性,静态属性同样可以被子类继承', t => {
    class Foo {
        static foo = 'foo';
    }
    class Bar extends Foo { }
    Foo.foo.should.equal('foo');
    should.not.exist((new Foo).foo);
    Bar.foo.should.equal('foo');
});

test('可以通过super关键字调用父类的静态方法', t => {
    class Foo {
        static foo() {
            return 'foo';
        }
    }
    class Bar extends Foo {
        static bar() {
            return super.foo();
        }
    }
    Bar.bar().should.equal('foo');
});

test('实例属性在类的内部使用等式定义，或在类的方法中使用this定义', t => {
    class Foo {
        prop1 = 1;
        static prop2 = 2;
        constructor() {
            this.prop3 = 3;
        }
    }
    Foo.prop4 = 4;
    const foo = new Foo();
    foo.prop1.should.equal(1);
    Foo.prop2.should.equal(2);
    foo.prop3.should.equal(3);
    Foo.prop4.should.equal(4);
});

test('属性赋值可以使用表达式', t =>{
    const prop1 = 'prop1';
    const prop2 = Symbol('prop2');
    const prop3 = Symbol('prop3');
    class Foo{
        [prop1] = 1;
        [prop2] = 2;
        static [prop3] = 3;
    }
    const foo = new Foo();
    foo[prop1].should.equal(1);
    foo[prop2].should.equal(2);
    Foo[prop3].should.equal(3);
});

//new.target
test('使用new.target访问new命令作用的构造函数', t => {
    function Fn1() {
        new.target.should.equal(Fn1);
    }
    function Fn2() {
        should.not.exist(new.target);
    }
    new Fn1();
    Fn2();
});

test('在class中使用new.target访问当前类，如果一个类继承另一个类，则new.target返回子类', t => {
    class Parent {
        constructor(){
            new.target.should.equal(Child);
        }
    }
    class Child extends Parent{
        constructor() {
            super();
            new.target.should.equal(Child);
        }
    }
    new Child();
});
