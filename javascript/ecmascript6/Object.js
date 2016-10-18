import test from 'ava';
import chai from 'chai';

const should = chai.should();

//object直接量
test('直接量简写', t => {
    const foo = 'foo';
    const obj = {
        //属性
        foo,
        //方法
        bar() {
            return 'bar';
        },
        //generator函数
        *gen() {
            yield 'gen';
        }
    }
    obj.foo.should.equal('foo');
    obj.bar().should.equal('bar');
    obj.gen().next().value.should.equal('gen');
});

test('在直接量中使用表达式', t => {
    const bar = 'bar';
    const obj = {
        ['f' + 'oo']: 'foo',
        [bar]: 'bar',
        ['say' + 'Hi']() {
            return 'hello';
        }
    }
    obj.foo.should.equal('foo');
    obj.bar.should.equal('bar');
    obj.sayHi().should.equal('hello');
});

//Object新的静态方法
//Object.is
test('使用Object.is方法判等', t => {
    (+0 === -0).should.true;
    Object.is(+0, -0).should.false;
    (NaN === NaN).should.false;
    Object.is(NaN, NaN).should.true;
});

//Object.assign
test('Object.assign的返回值等于第一个参数', t => {
    const dest = { id: 0 };
    const src = { id: 1 };
    Object.assign(dest, src).should.equal(dest);
});

test('Object.assign只复制自有可枚举属性', t => {
    const dest = { prop1: 'prop1' };
    const src = { prop2: 'prop2' };
    //继承属性
    Object.setPrototypeOf(src, { prop3: 'prop3' });
    //不可枚举属性
    Object.defineProperty(src, 'prop4', {
        value: 'prop4',
        writable: true,
        enumerable: false,
        configurable: true
    });
    Object.assign(dest, src);
    dest.should.eql({
        prop1: 'prop1',
        prop2: 'prop2',
    });
});

test('如果存在同名属性,则根据参数列表中的顺序，后面的属性会覆盖前面的属性', t => {
    const dest = { id: 0 };
    const src1 = { id: 1 };
    const src2 = { id: 2 };
    Object.assign(dest, src1, src2);
    dest.id.should.equal(2);
});

test('可以拷贝属性名为symbol的属性', t => {
    const dest = {};
    const src = { [Symbol.for('foo')]: 'foo' };
    Object.assign(dest, src);
    dest[Symbol.for('foo')].should.equal('foo');
});

test('assign实行浅拷贝', t => {
    const dest = {};
    const src = { foo: {}, bar: [] };
    Object.assign(dest, src);
    src.foo.foo = 'foo';
    src.bar[0] = 'bar';
    dest.foo.foo.should.equal('foo');
    dest.bar[0].should.equal('bar');
});

test('assign可以用来处理数组，但会将数组视为对象', t => {
    Object.assign([1, 2, 3], [4, 5]).should.eql([4, 5, 3]);
});

//Object.getOwnPropertySymbols
test('Object.getOwnPropertySymbols返回对象自身的所有symbol属性,' +
    '包含不可枚举属性', t => {
        const obj = { a: 1 };
        const proto = Object.getPrototypeOf(obj);
        Object.defineProperties(obj, {
            [Symbol.for('b')]: { value: 2 }, //不可枚举属性
        });
        proto[Symbol.for('c')] = 3;          //继承属性
        Object.getOwnPropertySymbols(obj).should
            .eql([Symbol.for('b')]);
    });

//Reflect.ownKeys
test('Reflect.ownKeys返回对象自身的所有属性' +
    '包含symbol属性与不可枚举属性', t => {
        const obj = { a: 1 };
        const proto = Object.getPrototypeOf(obj);
        Object.defineProperties(obj, {
            [Symbol.for('b')]: { value: 2 }, //不可枚举属性
        });
        proto[Symbol.for('c')] = 3;
        proto.d = 4;
        Reflect.ownKeys(obj).should
            .eql(['a', Symbol.for('b')]);
    });

//Object.setPrototypeOf(因为性能的原因，应该尽量避免使用)
test('使用Object.setPrototypeOf代替设置对象的__proto__属性', t => {
    const child = { foo: 'foo' };
    const parent = { bar: 'bar' };
    Object.setPrototypeOf(child, parent);
    Object.getPrototypeOf(child).should.equal(parent);
    child.bar.should.equal('bar');
});

//Object.values ,Object.entries
test('Object.values返回所有对象自身的可枚举属性的值,不包括symbol属性', t => {
    const obj = {
        a: 1,
        [Symbol.for('b')]: 2
    };
    Object.defineProperties(obj, {
        c: { value: 3 } //不可枚举
    });
    Object.setPrototypeOf(obj, { d: 4 });//继承属性
    Object.values(obj).should.eql([1]);
});

test('Object.entries返回所有对象自身的可枚举属性的键值对数组，不包括symbol属性', t => {
    const obj = {
        a: 1,
        [Symbol.for('b')]: 2,
        e: 2
    };
    Object.defineProperties(obj, {
        c: { value: 3 } //不可枚举
    });
    Object.setPrototypeOf(obj, { d: 4 });//继承属性
    Object.entries(obj).should.eql([['a', 1], ['e', 2]]);
});

test('Object.entries的一个应用场景，将对象转换为Map', t => {
    const obj = { foo: 'foo', bar: 'bar' };
    const map = new Map(Object.entries(obj));
    map.size.should.equal(2);
    map.get('foo').should.equal('foo');
    map.get('bar').should.equal('bar');
});

//Object.getOwnPropertyDescriptors
test('Object.getOwnPropertyDescriptors返回对象所有自身属性的描述对象', t => {
    const obj = {};
    const fooDesc = {
        value: 'foo',
        writable: true,
        enumerable: true,
        configurable: true
    }
    const barDesc = {
        get: function () {
            return 'bar';
        },
        set: undefined
    }
    Object.defineProperties(obj, {
        foo: fooDesc,
        bar: barDesc
    });
    const objDesc = Object.getOwnPropertyDescriptors(obj);
    objDesc.foo.should.not.equal(fooDesc);
    objDesc.foo.should.eql(fooDesc);
    objDesc.bar.should.not.eql(barDesc);
    Object.assign(barDesc, {
        enumerable: false,
        configurable: false
    });
    objDesc.bar.should.eql(barDesc);
});

test('Object.getOwnPropertyDescriptors的一个使用场景是替代Object.assign实现对象的浅拷贝' +
    '这样做的优势在于能够正确拷贝getter,setter', t => {
        const src = {
            _name: 'src',
            get name() {
                return this._name;
            }
        };
        let dest = Object.assign({}, src);
        //Object.assign将get属性被拷贝成了value属性
        Object.getOwnPropertyDescriptor(dest, 'name').should.eql({
            value: 'src',
            writable: true,
            enumerable: true,
            configurable: true
        });
        //使用Object.getOwnPropertyDescriptors拷贝
        dest = {};
        Object.defineProperties(dest, Object.getOwnPropertyDescriptors(src));
        dest.name.should.equal('src');
        dest._name = 'dest';
        dest.name.should.equal('dest');
    });