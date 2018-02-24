import test from 'ava';
import chai from 'chai';

const should = chai.should();
Reflect.defineProperty(Object.prototype, 'log', {
    get: function () {
        console.log(this);
    }
});

// @decorator
// class A {}
// // 等同于
// class A {}
// A = decorator(A) || A;

test('class decorator', t => {
    @Bar
    class Foo {
        static foo = 'foo';
    }
    function Bar(target) {
        target.bar = 'bar';
    }
    Foo.foo.should.equal('foo');
    Foo.bar.should.equal('bar');
});

test('修饰器可以返回完全不同的对象', t => {
    @Bar
    class Foo {
    }
    function Bar(target) {
        return {
            bar: 'bar'
        };
    }
    Foo.should.eql({ bar: 'bar' });
});

test('修饰器可以带有参数', t => {
    @Bar('super bar')
    class Foo {
        static foo = 'foo';
    }
    function Bar(bar) {
        return function (target) {
            target.bar = bar
        }
    }
    Foo.foo.should.equal('foo');
    Foo.bar.should.equal('super bar');
});

test('修饰器可以为类新增实例属性', t => {
    @Bar
    class Foo {
        foo = 'foo';
    }
    function Bar(target) {
        target.prototype.bar = 'bar';
    }
    const foo = new Foo;
    foo.foo.should.equal('foo');
    foo.bar.should.equal('bar');
});

test('修饰类的方法', t => {
    class Foo {
        @Bar
        foo() {
        }
    }

    function Bar(target, name, descriptor) {
        name.should.equal('foo');
        descriptor.value = 'foo';
        return descriptor;
    }

    const foo = new Foo();
    foo.foo.should.equal('foo');
});

test('修饰静态成员', t => {
    class Foo {
        @Bar
        static foo = 'foo'
    }
    function Bar(target, name, descriptor) {
        (typeof target).should.equal('function');
        target.name.should.equal('Foo');
        name.should.equal('foo');
        (typeof descriptor).should.equal('object');
    }
});

test('修饰类的属性', t => {
    class Foo {
        @Bar
        foo;
    }

    function Bar(target, name, descriptor) {
        target.should.ok;
        name.should.equal('foo');

        descriptor.enumerable = true;
        descriptor.writable = true;
        descriptor.configurable = true;

        return descriptor;
    }

    const foo = new Foo();
    Object.getOwnPropertyDescriptor(foo, 'foo').should
        .eql({
            value: undefined,
            writable: true,
            configurable: true,
            enumerable: true,
        });
});

test('修饰器的执行顺序', t => {
    const list = [];

    function Bar(order) {
        list.push(`enter Bar${order}`);
        return (target, property, descriptor) => {
            list.push(`executed Bar${order}`)
            return descriptor;
        };
    }

    class Foo {
        @Bar(1)
        @Bar(2)
        foo() {
        }
    }

    list.should.eql(['enter Bar1', 'enter Bar2', 'executed Bar2', 'executed Bar1']);
});

