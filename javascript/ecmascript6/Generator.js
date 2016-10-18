import test from 'ava';
import chai from 'chai';

const should = chai.should();
Reflect.defineProperty(Object.prototype, 'log', {
    get: function () {
        console.log(this);
    }
});

test('如果生成器函数没有return语句，则最后一次调用返回的value值为undefined', t => {
    function* gen1() {
        yield 'foo';
    }
    function* gen2() {
        yield 'foo';
        return 'bar';
    }
    const iter1 = gen1();
    const iter2 = gen2();
    iter1.next().should.eql({ value: 'foo', done: false });
    iter1.next().should.eql({ value: undefined, done: true });
    iter2.next().should.eql({ value: 'foo', done: false });
    iter2.next().should.eql({ value: 'bar', done: true });
});

test('return语句的返回值不会被遍历到', t=>{
    function* gen1() {
        yield 'foo';
    }
    function* gen2() {
        yield 'foo';
        return 'bar';
    }
    [...gen1()].should.eql(['foo']);
    [...gen2()].should.eql(['foo']);
});

test('普通函数调用就会执行，但生成器函数在生成的迭代器调用next时才开始执行', t => {
    let bool = false;
    function* gen() {
        bool = true;
    }
    const iter = gen();
    bool.should.false;
    iter.next();
    bool.should.true;
});

test('next方法第一次调用时传入的参数将被忽略', t => {
    function* generator() {
        return yield 'foo';
    }
    const iter = generator();
    iter.next('whatever') //这个参数没意义
        .value.should.equal('foo');
    iter.next('bar').should.eql({ value: 'bar', done: true });
});

test('生成器函数返回对象带有Symbol.iterator属性，调用该属性返回自身', t => {
    function* gen() { }
    const iter = gen();
    iter[Symbol.iterator]().should.equal(iter);
});

test('生成器函数的常用场景是为对象实现遍历器接口', t => {
    const obj = {
        *[Symbol.iterator]() {
            yield* [1, 2, 3];
        }
    };
    [...obj].should.eql([1, 2, 3]);
});

//Generator.prototype.throw
test('通过Generator.prototype.throw方法可以在函数体内捕获函数体外的错误', t => {
    function* gen() {
        try {
            yield;
        } catch (e) {
            e.message.should.equal('foo');
        }
    }
    const iter = gen();
    iter.next();
    try {
        iter.throw(new Error('foo'));
        iter.throw(new Error('bar'));
    } catch (e) {
        e.message.should.equal('bar');
    }
});

test('throw方法被捕获后，会自动执行一次next方法', t => {
    function* gen() {
        try {
            yield 'first';
        } catch (e) {
            e.message.should.equal('foo');
        }
        yield 'skip';
        yield 'bar';
    }
    const iter = gen();
    iter.next();
    iter.throw(new Error('foo'));
    iter.next().value.should.equal('bar');
});

//Generator.prototype.return
test('通过Generator.prototype.return方法，可以返回指定值并终止遍历器', t => {
    function* gen() {
        while (true) {
            yield 1;
        }
    }
    const iter = gen();
    iter.next().should.eql({ value: 1, done: false });
    iter.return(2).should.eql({ value: 2, done: true });
    iter.next().should.eql({ value: undefined, done: true });
});

test('如果Generator函数内部有try...finally代码块，return方法会推迟到finally代码块执行完再执行', t => {
    function* gen() {
        try {
            yield 'try';
        } finally {
            yield 'finally';
            yield 'finally';
        }
    }
    const iter = gen();
    iter.next().should.eql({ value: 'try', done: false });
    iter.return('return').should.eql({ value: 'finally', done: false });
    iter.next().should.eql({ value: 'finally', done: false });
    iter.next().should.eql({ value: 'return', done: true });
});

//yield*
test('在generator函数中直接调用另一个generator没有效果', t => {
    function* foo() {
        yield 'foo';
        bar();
    }
    function* bar() {
        yield 'bar';
    }
    const iter = foo();
    iter.next().should.eql({ value: 'foo', done: false });
    iter.next().should.eql({ value: undefined, done: true });
});

test('如果需要调用其他遍历器，可以使用for...of循环或者yield*语句', t => {
    function* foo() {
        yield 'foo';
    }
    function* bar1() {
        yield 'bar';
        for(let v of foo()){
            yield v;
        }
    }
    function* bar2(){
        yield 'bar';
        yield* foo();
    }
    const iter1 = bar1();
    const iter2 = bar2();
    iter1.next().should.eql({ value: 'bar', done: false });
    iter1.next().should.eql({ value: 'foo', done: false });
    iter1.next().should.eql({ value: undefined, done: true });
    iter2.next().should.eql({ value: 'bar', done: false });
    iter2.next().should.eql({ value: 'foo', done: false });
    iter2.next().should.eql({ value: undefined, done: true });
});

test('yield*能够遍历任何实现了iterator接口的对象',t=>{
    function* gen(){
        yield '123';
        yield* '123';
    }
    const iter = gen();
    iter.next().value.should.eql('123');
    [...iter].should.eql(['1','2','3']);
});

test('generator函数返回的遍历器是generator函数的实例，且继承至函数的prototype', t=>{
    function* gen(){};
    const iter = gen();
    gen.prototype.foo = 'foo';
    (iter instanceof gen).should.true;
    iter.foo.should.equal('foo');
});
