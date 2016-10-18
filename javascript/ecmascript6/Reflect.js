import test from 'ava';
import chai from 'chai';

const should = chai.should();
Reflect.defineProperty(Object.prototype, 'log', {
    get: function () {
        console.log(this);
    }
});

//Reflect.get(target,name,receiver)
test('使用Reflect.get获取对象属性', t => {
    const foo = { name: 'foo' };
    const bar = { name: 'bar' };
    Reflect.get(foo, 'name').should.equal('foo');
});

test('使用Reflect.get获取getter属性，允许指定receiver参数来指定this的值', t => {
    const foo = {
        _name: 'foo',
        get name() {
            return this._name;
        }
    }
    const bar = {
        _name: 'bar'
    }
    Reflect.get(foo, 'name').should.equal('foo');
    Reflect.get(foo, 'name', bar).should.equal('bar');
});

//Reflect.set(target,name,value,receiver)
test('使用Reflect.set设置对象属性', t => {
    const foo = { name: 'foo' };
    Reflect.set(foo, 'name', 'bar');
    foo.name.should.equal('bar');
});

test('使用Reflect.set获取setter属性, 允许指定receiver参数来指定this的值', t => {
    const foo = {
        _name: 'foo',
        set name(value) {
            this._name = value;
        }
    }
    const bar = {
        _name: 'bar'
    }
    Reflect.set(foo, 'name', 'newFoo');
    foo._name.should.equal('newFoo');
    Reflect.set(foo, 'name', 'newBar', bar);
    foo._name.should.equal('newFoo');
    bar._name.should.equal('newBar');
});

//Reflect.has(target,name)
test('Reflect.has用于取代in操作符', t => {
    const foo = { name: 'foo' };
    ('name' in foo).should.true;
    Reflect.has(foo, 'name').should.true;
});

// Reflect.deleteProperty(target,name)
test('Reflect.deleteProperty用于取代delete操作符' +
    '删除成功或属性不存在，返回true', t => {
        const obj = {
            foo: 1,
            bar: 1
        };
        Object.defineProperty(obj, 'baz', {});
        (delete obj.foo).should.true;
        (delete obj.whatever).should.true;
        //delete 操作符在严格模式下删除失败会报错
        t.throws(() => delete obj.baz, "Cannot delete property 'baz' of #<Object>");
        Reflect.deleteProperty(obj, 'bar').should.true;
        Reflect.deleteProperty(obj, 'whatever').should.true;
        //Reflect.deleteProperty删除失败返回false
        Reflect.deleteProperty(obj, 'baz').should.false;
    });

// Reflect.construct(target,args)
test('Reflect.construct用于取代new操作符', t => {
    function Person(name) {
        this.name = name;
    }
    Reflect.construct(Person, ['foo']) //注意这里使用参数数组
        .should.eql(new Person('foo'));
});

// Reflect.getPrototypeOf(target)
test('Reflect.getPrototypeOf用于取代Object.getPrototypeOf', t => {
    const parent = {};
    const child = Object.create(parent);
    Object.getPrototypeOf(child).should.equal(parent);
    Reflect.getPrototypeOf(child).should.equal(parent);
    //如果第一个参数不是对象，Reflect.getPrototypeOf会报错
    t.throws(() => Reflect.getPrototypeOf(0), 'Reflect.getPrototypeOf called on non-object');
    //Object.getPrototypeOf会试图将其转换为对象（装箱）
    Object.getPrototypeOf(0).should.equal(Number.prototype);
    t.throws(() => Object.getPrototypeOf(null), 'Cannot convert undefined or null to object');
});

// Reflect.setPrototypeOf(target, prototype)
test('Reflect.setPrototypeOf用于取代Object.setPrototypeOf', t => {
    const parent = {};
    const child1 = {};
    const child2 = {};
    const child3 = 0;
    Object.setPrototypeOf(child1, parent);
    Reflect.setPrototypeOf(child2, parent);
    Reflect.getPrototypeOf(child1).should
        .equal(Reflect.getPrototypeOf(child2))
        .equal(parent);
    //如果第一个参数不是对象,Reflect.setPrototypeOf会报错
    t.throws(() => Reflect.setPrototypeOf(child3, parent), 'Reflect.setPrototypeOf called on non-object');
    //Object.setPrototypeOf会试图将其转为对象（装箱），而在操作完成后，装箱生成的对象会被丢弃，因此相当于什么都没做
    Object.setPrototypeOf(child3, parent);
    Object.getPrototypeOf(child3).should.equal(Number.prototype);
});

// Reflect.apply(target,thisArg,args)
test('Reflect.apply用于取代Function.prototype.apply.call(func, thisArg, args)', t => {
    const foo = { name: 'foo' };
    function fn() {
        return this.name;
    }
    fn.apply(foo).should.equal('foo');//修改this指向
    fn.apply = function () {
        return 'bar';
    }
    //现在如果要调用fn，并将this指向foo，不能直接使用fn.apply，因为它被复写了
    fn.apply(foo).should.equal('bar');
    Function.apply.call(fn, foo).should.equal('foo');
    Reflect.apply(fn, foo, []).should.equal('foo');
});

// Reflect.defineProperty(target,name,desc)
test('Reflect.defineProperty用于取代Object.defineProperty', t => {
    const foo = {};
    Reflect.defineProperty(foo, 'name', { value: 'foo' });//该属性是不可配置的
    foo.name.should.equal('foo');
    //使用Object.defineProperty失败会报错
    t.throws(() => Object.defineProperty(foo, 'name', { value: 'bar' }), 'Cannot redefine property: name');
    //使用Reflect.defineProperty失败会返回false
    Reflect.defineProperty(foo, 'name', { value: 'bar' }).should.false;
});

// Reflect.getOwnPropertyDescriptor(target, name)
test('Reflect.getOwnPropertyDescriptor用于取代Object.getOwnPropertyDescriptor', t => {
    const foo = {}
    Reflect.defineProperty(foo, 'name', {});
    Reflect.getOwnPropertyDescriptor(foo, 'name').should.eql({
        value: undefined,
        writable: false,
        enumerable: false,
        configurable: false
    });
    //Object会尝试将第一个参数转换为对象
    should.not.exist(Object.getOwnPropertyDescriptor(0, 'name'));
    //Reflect第一个参数不是对象则报错
    t.throws(() => Reflect.getOwnPropertyDescriptor(0, 'name'), 'Reflect.getOwnPropertyDescriptor called on non-object');
});

// Reflect.preventExtensions(target)
test('Reflect.preventExtensions用于取代Object.preventExtensions', t => {
    const foo = {};
    const bar = {};
    //Object.preventExtensions返回传入的参数本身
    Object.preventExtensions(foo).should.equal(foo);
    Object.isExtensible(foo).should.false;
    Object.preventExtensions(0).should.equal(0);
    //Reflect.preventExtensions返回布尔值，参数不合法则报错
    Reflect.preventExtensions(bar).should.true;
    Reflect.isExtensible(foo).should.false;
    t.throws(() => Reflect.preventExtensions(0), 'Reflect.preventExtensions called on non-object');
});

// Reflect.isExtensible(target)
test('Reflect.isExtensible用于取代Object.isExtensible', t => {
    Object.isExtensible(0).should.false;
    t.throws(() => Reflect.isExtensible(0), 'Reflect.isExtensible called on non-object');
});

// Reflect.ownKeys(target)
test('Reflect.ownKeys返回对象的所有属性', t => {
    const obj = {
        foo:0,
        [Symbol.for('foo')]:0
    }
    Object.defineProperties(obj, {
        bar:{
            value:0
        },
        [Symbol.for('bar')]:{
            value:0
        }
    });
    //基本等同于Object.getOwnPropertyNames与Object.getOwnPropertySymbols之和
    Reflect.ownKeys(obj)
        .should.eql(['foo','bar',Symbol.for('foo'), Symbol.for('bar')]);
});