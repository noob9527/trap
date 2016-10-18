import test from 'ava';
import chai from 'chai';

const should = chai.should();

//参数默认值
test('参数默认值', t => {
    function fn(foo = 'foo') {
        return foo;
    }
    fn().should.eql('foo');
    fn('bar').should.eql('bar');
});

test('参数默认值在调用时计算，而不是在定义时计算', t => {
    let x = 0;
    function fn(a = x + 1) {
        return a;
    }
    fn().should.equal(1);
    x = 1;
    fn().should.equal(2);
});

test('如果定义默认值的参数不是该函数最后一个参数,则需要使用undefined调用来应用该默认值', t => {
    function fn(foo = 'foo', bar) {
        return [foo, bar];
    }
    fn('bar').should.eql(['bar', undefined]);
    fn(undefined, 'bar').should.eql(['foo', 'bar']);
});

test('带默认值的参数已经其后续参数不会被计入函数的length属性', t => {
    function fn(a, b = 2, c) { }
    fn.length.should.equal(1);
});

test('不定项参数也不会计入length属性', t => {
    function fn(a, ...args) { }
    fn.length.should.equal(1);
});

//rest参数
test('rest参数用于替换arguments对象', t => {
    const add = (...numbers) => numbers.reduce((accu, curr) => accu + curr, 0);
    add(1, 2, 3).should.equal(6);
});

test('可以使用剩余参数将函数转换为接收参数列表', t => {
    //es5
    function es5unSplat(func) {
        return function () {
            return func.call(null, [].slice.call(arguments));
        };
    }
    //es6
    function es6unSplat(func) {
        return (...rest) => func(rest);
    }
    function sum(array) {
        return array.reduce((a, c) => a + c, 0);
    }
    es5unSplat(sum)(1, 2, 3).should.equal(6);
    es6unSplat(sum)(1, 2, 3).should.equal(6);
});

test('可以使用扩展运算符将函数转换为接收参数数组', t => {
    //es5
    function es5splat(func) {
        return array => func.apply(null, array);
    }
    function es6splat(func) {
        return array => func(...array);
    }
    function sum(...rest) {
        return rest.reduce((a, c) => a + c, 0);
    }
    sum([1, 2, 3]).should.NaN;
    es5splat(sum)([1, 2, 3]).should.equal(6);
    es6splat(sum)([1, 2, 3]).should.equal(6);
});

test('箭头函数中的this总是指向函数定义生效时所在的对象', t => {
    function Foo() {
        this.name = 'foo';
        this.func1 = function () {
            return this.name;
        };
        this.func2 = () => this.name;
    }
    const foo = new Foo();
    const bar = { name: 'bar' };
    foo.func1().should.equal('foo');
    foo.func2().should.equal('foo');
    foo.func1.call(bar).should.equal('bar');
    foo.func2.call(bar).should.equal('foo');
});

test('箭头函数本身内部不存在arguments对象,箭头函数内的arguments对象总是指向上级函数作用域的arguments', t => {
    function fn1() {
        const arg = arguments;
        return () => arguments.should.equal(arg);
    }
    fn1()();
});

//async function
test('async函数的返回值都是promise', async t => {
    async function fn() { }
    async function foo() {
        return 'foo';
    }
    (fn() instanceof Promise).should.true;
    (foo() instanceof Promise).should.true;
    (await fn() === undefined).should.true;
    (await foo() == 'foo').should.true;
});

test('await命令后的promise如果reject,则async函数返回的promise也会reject', async t => {
    async function foo() {
        await Promise.reject('foo');
        throw new Error('bar');//这行不会执行
    }
    await foo().catch(e => e.should.equal('foo'));
});

test('async函数体内抛出的错误，将作为返回的promise对象的catch方法的参数', async t => {
    const err = new Error('foo');
    async function foo() {
        throw err;
    }
    await foo().catch(e => e.should.equal(err));
});

test('await命令后面可以不带异步操作', async t => {
    const obj = {};
    const foo = await 'foo';
    const bar = await obj;
    foo.should.equal('foo');
    bar.should.equal(obj);
});

test('如果await命令后的操作互相独立，则应该使用Promise.all同时执行', async t => {
    let [foo, bar] = await Promise.all([1, 2]);
    [foo, bar].should.eql([1, 2]);
});
