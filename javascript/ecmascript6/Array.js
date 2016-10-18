import test from 'ava';
import chai from 'chai';

const should = chai.should();

//Array.from
test('Array.from可以将类数组对象与可遍历的对象转换为数组', () => {
    Array.from('abc').should.eql(['a', 'b', 'c']);
    Array.from({ length: 2 }).should.eql([undefined, undefined]);
    Array.from(new Set(['a', 'b'])).should.eql(['a', 'b']);
});

test('如果参数是数组，则返回一模一样的新数组，但不包含额外属性', t => {
    const arr = [1, 2, 3];
    arr.foo = 'bar';
    const copy = Array.from(arr);
    copy.should.not.equal(arr);
    copy.should.eql([1, 2, 3]);
    should.not.exist(copy.foo);
});

test('Array.from会使用undefined填充空位', t => {
    Array.from({ length: 2 }).should.eql([undefined, undefined]);
    Array.from([1, , 2]).should.eql([1, undefined, 2]);
});

test('Array.from接收第二个参数，其作用类似于map方法', t => {
    Array.from({ length: 2 }, (e, i) => i).should.eql([0, 1]);
});

test('由于Array.from方法能够正确处理４字节unicode字符，因此可用于统计字符数', t => {
    '𠮷'.length.should.equal(2);
    Array.from('𠮷').length.should.equal(1);
});

//Array.of
test('Array.of用于将一组值转换为数组', t => {
    Array.of(1, 2, 3).should.eql([1, 2, 3]);
});

test('Array.of()与Array()的主要区别', t => {
    Array(1).should.eql([,]);
    Array.of(1).should.eql([1]);
});

//copyWithin(target, start=0, end=this.length)
test('copyWithin会修改原数组', t => {
    const arr = [1, 2, 3];
    arr.copyWithin(1);
    arr.should.eql([1, 1, 2]);
});

test('copyWithin会拷贝数组空位', t => {
    [, 1, 2].copyWithin(1).should.eql([, , 1]);
});

//find findIndex
test('find与findIndex能够发现NaN', t => {
    [NaN].indexOf(NaN).should.equal(-1);
    [NaN].findIndex(e => Object.is(NaN, e));
});

//fill
test('fill使用指定元素填充数组', t => {
    [1, 2, 3].fill(0).should.eql([0, 0, 0]);
});

test('fill的其它参数用于控制填充的起始与结束位置', t => {
    [1, 2, 3].fill(0, 1, 2).should.eql([1, 0, 3]);
});

//entries, keys
test('entries, keys返回遍历器', t => {
    const arr = [5, 6, 7]
    arr.keys().next().value.should.equal(0);
    arr.entries().next().value.should.eql([0, 5]);
});

//includes
test('includes的第二个参数指定搜索的起始位置', t => {
    [1, 2, 3].includes(1).should.true;
    [1, 2, 3].includes(1, 1).should.false;
});

test('includes能够正确查找NaN', t => {
    [NaN].indexOf(NaN).should.equal(-1);
    [NaN].includes(NaN).should.true;
});

//for of
test('for of不会跳过数组空位', t => {
    const arr = [,];
    let res = [];
    arr.forEach(i => res.push(i));
    res.should.eql([]);
    res = [];
    for (let i of arr) {
        res.push(i);
    }
    res.should.eql([undefined]);
});

test('快速排序的es2015实现', t => {
    function quickSort(arr) {
        if (!arr.length) return [];
        const [pivot, ...rest] = arr;
        return [
            ...quickSort(rest.filter(x => x < pivot)),
            pivot,
            ...quickSort(rest.filter(x => x >= pivot))
        ]
    }
    quickSort([3, 5, 1, 4, 2]).should.eql([1, 2, 3, 4, 5]);
});