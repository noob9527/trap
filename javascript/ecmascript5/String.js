/**
 * Created by xy on 16-6-30.
 */
var should=require('should');
var log=console.log.bind(console);

describe('String',function () {
    describe('js采用UTF-16编码Unicode字符集',function () {
        it('Unicode码点超过\uFFFF的字符,length属性无法正确返回字符数',function () {
            var str='𠮷'; //  \u{20BB7}即\uD842\uDFB7
            str.should
                .equal('\u{20BB7}')
                .equal('\uD842\uDFB7');
            str.length.should.equal(2);
        });
        it('特殊的字面量表示',function () {
            '\101'.should.equal('A');   //3位8进制数
            '\x41'.should.equal('A');   //2位16进制数表示一个Latin-1字符
            '\u0041'.should.equal('A'); //4位16进制数表示一个Unicode字符
            '\u{41}'.should.equal('A'); //(es6)4位16进制数表示一个Unicode字符
        });
    });
    
});