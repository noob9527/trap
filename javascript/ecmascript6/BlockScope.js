import test from 'ava';
import chai from 'chai';

const should = chai.should();

test('函数每次调用会产生新的作用域链，同一个外部函数内定义的多个嵌套函数共享一个作用域链', t => {
    function counter() {
        var n = 0;
        return {
            count: () => n++,
            reset: () => n = 0
        };
    }
    var cnt1 = counter();
    var cnt2 = counter();
    cnt1.count(); //n自增
    cnt2.count().should.equal(0); //由于使用不同的作用域链,因此cnt1不会影响到cnt2
    cnt1.reset();
    cnt1.count().should.equal(0); //由于reset与count共享变量n，因此这里返回0
    cnt2.count().should.equal(1);
});

test('ES2015循环体每次执行都会产生一个作用域', t => {
    const funcArr1 = [];
    for (var i = 0; i < 3; ++i) {
        funcArr1.push(() => i);
    }
    const resArr1 = funcArr1.map(e => e());
    const funcArr2 = [];
    for (let i = 0; i < 3; ++i) {
        funcArr2.push(() => i);
    }
    const resArr2 = funcArr2.map(e => e());
    resArr1.should.eql([3, 3, 3]);
    resArr2.should.eql([0, 1, 2]);
});

test('let关键字不存在变量提升(hoist)', t => {
    should.not.exist(foo);
    t.throws(() => bar, 'bar is not defined');
    var foo;
    let bar;
});

test('只要区块中存在let定义，则在定义前使用变量会报错(temporal dead zone)', t => {
    var foo;
    {
        (typeof bar).should.equal('undefined');
        t.throws(() => typeof foo, 'foo is not defined'); //TDZ中使用typeof依然会报错
        let foo;
    }
    t.notThrows(() => { var a = a; });
    t.throws(() => { let b = b; }, 'b is not defined');
});

test('es6不再需要立即执行函数表达式', t=>{
    (function(){
        var foo;
    }());
    //等价于
    {
        let foo;
    }
});

test('es6允许在块级作用域中声明函数，行为类似与let',t=>{
    {
        function foo(){}
    }
    (typeof foo).should.equal('undefined');
});

test('使用const定义常量', t => {
    const PI = 3.14;
    t.throws(() => PI = 3.5, 'Assignment to constant variable.');
});

test('const类似于java中的final，对于Object类型的变量，仅保证内存地址不变', t=>{
    const foo = {bar:0};
    foo.bar = 1;
    foo.bar.should.equal(1);
});

