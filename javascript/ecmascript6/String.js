import test from 'ava';
import chai from 'chai';

const should = chai.should();

//utf16
test('新增表示unicode字符的方法', t=>{
    '\u{7a}'.should.equal('\u007a');
    '\u{1F680}'.should.equal('\uD83D\uDE80');
});

//include,startWith,endsWith
test('includes, startWith, endWith可以使用第二个参数指定搜索的位置', t => {
    'whatever'.includes('what').should.true;
    'whatever'.includes('what', 1).should.false;
});

test('endsWith使用第二个参数指定搜索的字符数(前n个字符)', t => {
    'whatever'.endsWith('at').should.false;
    'whatever'.endsWith('at', 4).should.true;
});

//repeat
test('repeat的参数如果是负数或者infinity则会报错', t => {
    t.throws(() => { return 'foo'.repeat(-1); }, 'Invalid count value');
});

test('repeat的参数如果是字符串，则会自动转换为数字，如果是小数则会先取整', t => {
    'foo'.repeat(-0.5).should.equal('');
    'foo'.repeat(1.9).should.equal('foo');
    'foo'.repeat('2').should.equal('foofoo');
});

//padStart,padEnd
test('原字符串大于等于最小长度，则返回原字符串', t => {
    'foo'.padStart(3, 'bar').should.equal('foo');
});

test('如果补全字符串与原字符串二者长度之和超过指定的最小长度，则截取补全字符串', t => {
    'foo'.padStart(4, 'bar').should.equal('bfoo');
});

test('如果补全字符串与原字符串二者长度之和仍不足最小长度，则重复补全字符串', t => {
    'foo'.padStart(8, 'bar').should.equal('barbafoo');
});

test('如果省略第二个参数，则默认用空格补全字符串', t => {
    ''.padStart(2).should.equal('  ');
});

test('padStart可以用来生成前导０', t => {
    '1'.padStart(5, '0').should.equal('00001');
});

//template String
test('使用模板字符串在字符串中嵌入变量', t => {
    const foo = 'foo';
    `hello ${foo}`.should.equal('hello foo');
});

test('``中的空格与缩进会保留在结果字符串中', t => {
    `
    foo`.startsWith('\n').should.true;
});

test('模板字符串可以嵌套', t=>{
    `foo ${`bar ${'baz'}`}`.should.equal('foo bar baz');
});