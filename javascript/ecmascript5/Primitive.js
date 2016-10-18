/**
 * Created by xy on 16-6-30.
 */
var should=require('should');
var log=console.log.bind(console);

describe('Primitive',function () {
    describe('null与undefined',function () {
        it('typeof null返回object',function () {
            (typeof null).should.equal('object');
        });
        it('null与undefined都是"falsy"值，但并不代表它们等于false',function () {
            Boolean(null).should.false();
            Boolean(undefined).should.false();
            //==运算符并不试图将其操作数隐式转换为布尔值,而是偏向于将操作数转换为数字
            (null==false).should.false();
            (undefined==false).should.false();
            //另一个例子
            '0'.should.ok();
            ('0'==false).should.true();
        });
        it('null==undefined',function () {
            (null==undefined).should.true();
        });
    });
    
    describe('Number,Boolean,String',function () {
        it('原始类型不完全等于对应的包装类型',function () {
            var s1='';
            var s2=new String('');
            (s1==s2).should.true();
            (s1===s2).should.false();
            (typeof s1).should.equal('string');
            (typeof s2).should.equal('object');
            (s1 instanceof String).should.false();
            (s2 instanceof String).should.true();
        });
        it('typeof运算符对原始类型与包装类型分别返回不同值',function () {
            var s1='';
            var s2=new String('');
            (typeof s1).should.equal('string');
            (typeof s2).should.equal('object');
        });
        it('instanceof运算符对原始类型与包装类型分别返回不同值',function () {
            var s1='';
            var s2=new String('');
            (s1 instanceof String).should.false();
            (s2 instanceof String).should.true();
        });
        it('所有包装类型对象都是"truthy"值',function () {
            (!!new Boolean(false)).should.true();
            (!!new Number(0)).should.true();
            (!!new String('')).should.true();
        });
        it('作为类型转换函数返回原始数据类型，作为构造函数返回引用数据类型',function () {
            (typeof Number(1)).should.equal('number');
            (typeof new Number(1)).should.equal('object');
        });
        it('Object构造函数根据参数类型返回指定的包装类型实例',function () {
            var str=new Object('foo');
            var num=new Object(1);
            (str instanceof String).should.true();
            (num instanceof Number).should.true();
            (typeof str).should.equal('object');
            (typeof num).should.equal('object');
        });
        it('访问原始数据类型时会自动创建对应的包装类型对象，访问结束后该对象被销毁',function () {
            //原始数据类型
            var s1='foo';
            s1.name='bar'; //这里创建String类型对象并为该对象添加name属性，但执行结束后该对象被销毁
            should(s1.name).undefined();
            //包装类型对象
            var s2 = new String('foo');
            s2.name = 'bar';
            s2.name.should.equal('bar');
        });
    });
    
    describe('使用运算符进行类型转换',function () {
        it('+""等价于String()',function () {
            (0+'').should.equal('0');
        }); 
        it('+与-0等价于Number()',function () {
            (+'0').should
                .equal('0'-0)
                .equal(0);
        });
        it('!!等价于Boolean',function () {
            (!!1).should.true();
        });
    });
});