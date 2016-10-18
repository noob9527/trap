import test from 'ava';
import chai from 'chai';

const should = chai.should();

test('基础用法', t => {
    const [a, b] = [1, 2];
    a.should.equal(1);
    b.should.equal(2);
});

test('使用逗号跳过元素', t => {
    const [, , third] = ['foo', 'bar', 'baz'];
    third.should.equal('baz');
});

test('使用...获取不定项参数', t => {
    const [foo, ...bar] = [1, 2, 3, 4];
    foo.should.equal(1);
    bar.should.eql([2, 3, 4]);
});

test('解构失败，则变量等于undefined', t => {
    const [a] = [];
    const [b, c] = [1];
    (a === undefined).should.true;
    (c === undefined).should.true;
});

test('解构赋值可以使用默认值', t => {
    let [foo = 'bar'] = [];
    foo.should.equal('bar');
    [foo = 'bar'] = ['abc'];
    foo.should.equal('abc');
    [foo = 'bar'] = [undefined]; //只有在对应位置严格等于undefined时，默认值才会生效
    foo.should.equal('bar');
    [foo = 'bar'] = [null];
    (foo === null).should.true;
});

test('解构赋值可以作用于对象', t => {
    let {foo, bar} = { foo: 'a', bar: 'b' };
    foo.should.equal('a');
    bar.should.equal('b');
});

test('如果变量名在对象中不存在，则需要指明变量应该对应于对象的哪个属性', t => {
    let {foo: aaa, bbb} = { foo: 'a', bar: 'b' };
    aaa.should.equal('a');
    should.not.exist(bbb);
});

test('解构赋值复杂对象', t=>{
    const obj = {
        bar:'aaa',
        baz:{
            bbb:'bbb',
            ccc:'ccc',
        },
        qux:['ddd', 'eee'],
    }

    let {
        foo='foo',
        bar:aaa,
        baz:{
            bbb,
            ccc,
        },
        qux:[ddd,eee],
    } = obj;

    (typeof bar).should.equal('undefined');
    (typeof baz).should.equal('undefined');
    (typeof qux).should.equal('undefined');

    ({foo, aaa, bbb, ccc}).should.eql({
        foo:'foo',
        aaa:'aaa',
        bbb:'bbb',
        ccc:'ccc',
    });
});

test('解构赋值等号右侧如果是原始数据类型，则会先被转换成对应的包装类型', t => {
    let [a, b] = 'foo';
    a.should.equal('f');
    b.should.equal('o');
    let {toString} = 123;
    toString.should.equal(Number.prototype.toString);
    ({ toString } = true);
    toString.should.equal(Boolean.prototype.toString);
});

test('函数参数解构1', t => {
    const arr = [[1, 2], [3, 4]].map(([a, b]) => a + b); //注意这里的b不是index，而是参数数组的第二个元素
    arr.should.eql([3, 7]);
});

test('函数参数解构2', t=>{
    const fn = ({
        foo,
        bar='bar',
        baz:aaa,
        qux:{
            bbb,
            ccc,
        }
    }) => {
        return {
            foo,
            bar,
            aaa,
            bbb,
            ccc,
        };
    }

    const result = fn({
        foo:'foo',
        baz:'baz',
        qux:{
            bbb:'bbb',
            ccc:'ccc',
        },
    });

    result.should.eql({
        foo:'foo',
        bar:'bar',
        aaa:'baz',
        bbb:'bbb',
        ccc:'ccc',
    });
});
