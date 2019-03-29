import test from 'ava';
import chai from 'chai';

const should = chai.should();
const utf16str = '\uD83D\uDC2A';

test('es7现在已经支持反向(lookbehind)查找', t => {
    const pattern1 = /(?<=a)b/;
    const pattern2 = /(?<!a)b/;

    pattern1.test('ab').should.true;
    pattern1.test('bb').should.false;

    pattern2.test('ab').should.false;
    pattern2.test('bb').should.true;
});

// u flag
test('u修饰符，用于处理四字节的Unicode字符', t => {
    /^\uD83D/.test(utf16str).should.true;
    /^\uD83D/u.test(utf16str).should.false;
});

test('如果没有u修饰符, 正则元字符无法匹配4字节字符', t => {
    /^.$/.test(utf16str).should.false;
    /^.$/u.test(utf16str).should.true;
});

test('使用es6新增的unicode字符表示方法创建正则0表达式，必须加上u修饰符', t => {
    /\u{61}/.test('a').should.false;//匹配６１个连续的u
    /\u{61}/u.test('a').should.true;
});

test('如果不使用u修饰符,正则表达式量词无法正确匹配４字节字符', t => {
    const str = '𠮷'; //  \u{20BB7}即\uD842\uDFB7
    /𠮷{2}/.test('𠮷𠮷').should.false;
    /𠮷{2}/u.test('𠮷𠮷').should.true;
});

// y flag
test('y修饰符与g修饰符类似，但是y修饰符要求每次匹配都从上次匹配的下一个位置开始', t=>{
    const str = 'foo_bar';
    const r1 = /[^_]+/g;
    const r2 = /[^_]+/y;
    r1.exec(str)[0].should.equal('foo');
    r2.exec(str)[0].should.equal('foo');

    r1.exec(str)[0].should.equal('bar');
    should.not.exist(r2.exec(str));
});

test('可以认为y修饰符在每次匹配前，为正则表达式添加^锚', t=>{
    /b/.test('aba').should.true;
    /b/y.test('aba').should.false;
});

test('如果只有一个y修饰符,match方法只返回第一个匹配，只有与g一起使用才返回所有匹配', t=>{
    'a1a2a3'.match(/a\d/y).length.should.equal(1);
    'a1a2a3'.match(/a\d/gy).length.should.equal(3);
});

//flag
test('使用source返回文本，使用flags放回所有修饰符', t=>{
    const r = /a/gimyu;
    r.source.should.equal('a');
    Array.from(r.flags).sort()
        .should.eql(Array.from('gimyu').sort());
});

test('使用sticky与unicode访问器属性获取正则表达式是否设定了y,u修饰符', t=>{
    const r = /a/yu;
    r.sticky.should.true;
    r.unicode.should.true;
});