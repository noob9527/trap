import test from 'ava';
import chai from 'chai';

const should = chai.should();

test('Symbol是js中第六种数据类型', t => {
    //前5种是 undefined,String,Boolean,Number,Object
    const s = Symbol();
    (typeof s).should.equal('symbol');
    t.throws(() => s.foo = 'foo');
});

test('由于Symbol是原始数据类型，因此无法使用new操作符创建，也无法添加属性', t => {
    const s = Symbol();
    t.throws(() => new Symbol(), 'Symbol is not a constructor');
    t.throws(() => s.foo = 'foo', "Cannot create property 'foo' on symbol 'Symbol()'");
});

test('Symbol函数可以接收一个字符串,作为该Symbol的描述', t => {
    const foo = Symbol('foo');
    //如果参数是对象，则会调用器toString方法获取字符串描述
    const bar = Symbol({
        toString() {
            return 'bar';
        }
    });
    foo.toString().should.equal('Symbol(foo)');
    bar.toString().should.equal('Symbol(bar)');
});

test('相同描述的Symbol值并不相等', t => {
    const [s1, s2] = [Symbol(), Symbol()];
    const [s3, s4] = [Symbol('foo'), Symbol('foo')];
    (s1 == s2).should.false;
    (s3 == s4).should.false;
});

//convert
test('Symbol值不允许隐式转换为字符串，只允许显示转换', t => {
    const foo = Symbol('foo');
    String(foo).should
        .equal(foo.toString())
        .equal('Symbol(foo)');
    t.throws(() => `${foo}`, 'Cannot convert a Symbol value to a string');
});

test('Symbol值允许隐式转换为boolean', t => {
    const s = Symbol();
    (!!s).should.true;
    Boolean(s).should.true;
});

test('Symbol值不允许转换为Number', t => {
    const s = Symbol();
    t.throws(() => Number(s), 'Cannot convert a Symbol value to a number');
});

//symbol as key
test('symbol最标准的用法是作为对象的key,以保证属性不会被其他人覆盖' +
    '使用Symbol作为属性key,需要使用方括号赋值与访问', t => {
        const s = Symbol();
        const obj = {};
        obj.s = 'foo'; //这样赋值的属性key并不是symbol值，而是字符串s
        should.not.exist(obj[s]);
        obj['s'].should.equal('foo');
    });

test('Object.keys,Object.getOwnPropertyNames,JSON.stringify等方法会跳过Symbol属性', t => {
    const obj = {
        [Symbol('foo')]: 'foo',
        bar: 'bar'
    }
    Object.keys(obj).should
        .eql(Object.getOwnPropertyNames(obj))
        .eql(['bar']);
    JSON.stringify(obj).should.equal('{"bar":"bar"}');
});

test('使用Object.getOwnPropertySymbols获取对象所有Symbol属性', t => {
    const obj = {
        [Symbol.for('foo')]: 'foo',
        bar: 'bar'
    }
    Object.getOwnPropertySymbols(obj)
        .should.eql([Symbol.for('foo')]);
});

//Symbol.for,Symbol.keyFor
test('使用Symbol.for创建可重用的Symbol值', t => {
    const [s1, s2] = [Symbol('foo'), Symbol('foo')];
    const [s3, s4] = [Symbol.for('bar'), Symbol.for('bar')];
    (s1 == s2).should.false;
    (s3 === s4).should.true;
});

test('Symbol.keyFor返回已使用Symbol.for注册的symbol值的描述字符串', t => {
    const [s1, s2] = [Symbol('foo'), Symbol.for('foo')];
    should.not.exist(Symbol.keyFor(s1));
    Symbol.keyFor(s2).should.equal('foo');
});

//内置Symbol值
test('对象的Symbol.hasInstance方法,决定了instanceof运算符的执行结果', t => {
    const obj = {
        [Symbol.hasInstance](value) {
            return value === 'foo';
        }
    };
    ('bar' instanceof obj).should.false;
    ('foo' instanceof obj).should.true;
});

test('Symbol.toStringTag决定Object.prototype.toString的返回结果', t => {
    const obj = {
        [Symbol.toStringTag]: 'foo'
    }
    obj.toString().should.equal('[object foo]');
});

test('Symbol.toPrimitive绝对该对象如何被转换成原始值', t => {
    const foo = {
        [Symbol.toPrimitive](hint) {
            switch (hint) {
                case 'number':
                    return 999;
                case 'string':
                    return 'foo';
                case 'default':
                    return 'default';
            }
        }
    }
    Number(foo).should.equal(999);
    String(foo).should.equal('foo');
    (foo == 'default').should.true;
});

test('Symbol.iterator,决定该对象默认的遍历器方法', t => {
    const obj = {
        *[Symbol.iterator]() {
            yield 'foo';
            yield 'bar';
            yield 'baz';
        }
    };
    [...obj].should.eql(['foo','bar','baz']);
});

//Symbol.match,Symbol.replace,Symbol.search,Symbol.split
test('对象的Symbol.match决定了执行str.match(obj)的返回结果' +
    'Symbol.replace,Symbol.search,Symbol.split同理', t => {
        //String.prototype.match(regexp) 等同于 regexp[Symbol.match](this)
        const obj = {
            [Symbol.match](str) {
                return str.length;
            }
        };
        'foo'.match(obj).should.equal(3);
    });