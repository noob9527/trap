/**
 * Created by xy on 16-7-1.
 */
var should=require('should');
var log=console.log.bind(console);

describe('Syntax',function () {
    describe('变量',function () {
         it('变量与函数声明提前(hoisting)',function () {
             var str='foo';
             (function () {
                 should(str).undefined();
                 var str='bar'; //该行声明提前至函数顶部，但赋值并没有提前，因此值为undefined
             })();
             f().should.equal('foo');
             
             //该函数声明提前至顶部,因此可以在“声明”之前调用
             function f() {
                return 'foo';  
             }
         });
    });
    
    describe('运算符',function () {
        describe('加法运算符',function () {
            it('+运算符先将所有操作数转换为原始值，转换结束后如果有一个操作数为字符串' +
                '则进行字符串连接，否则进行数字加法操作',function () {
                (1+'1').should.equal('11');
                (true+1).should.equal(2);
                (null+1).should.equal(1);
                (null+'1').should.equal('null1');
                (undefined+1).should.NaN();
            });
            it('+运算符结合性为自左向右',function () {
                (1+2+'3').should.equal('33');
                (1+(2+'3')).should.equal('123');
            });
            it('一元加法运算符将操作数转换为数字，其效果类似于Number函数',function () {
                (+'1').should.equal(1);
                (+'a').should.NaN();
            });    
        });
        
        describe('比较运算符',function () {
            it('=运算符先将所有操作数转换为原始值，布尔值转换为数字，转换结束后如果一个操作数为数字' +
                '另一个操作数为字符串，则将字符串转为数字进行比较',function () {
                //==运算符更倾向于将操作数转换为数字
                (!!'0').should.true();
                ('0'==false).should.true();
                ('0x11'==17).should.true();
            }); 
        });
        
        describe('typeof运算符',function () {
            it('typeof在es5中是“完全安全”的操作符，可以对未定义的变量使用，而不会报错', function () {
                (typeof foo).should.equal('undefined');
            });
            it('返回操作数的类型',function () {
                (typeof NaN).should.equal('number');
                (typeof new Number(NaN)).should.equal('object');
                (typeof null).should.equal('object');
                (typeof new Function()).should.equal('function');
                (typeof undefined).should.equal('undefined');
                (typeof foo).should.equal('undefined')
            });
            it('可以像函数一样使用typeof运算符',function () {
                (typeof([])).should.equal('object'); 
            });
        });
        
        describe('in运算符',function () {
            it('右操作数拥有左操作数的属性名，则表达式返回true',function () {
                ('toString' in {}).should.true();
                ('0' in [1,2,3]).should.true();
            });
        });
        
        describe('instanceof运算符',function () {
            it('instanceof运算符用于判断子类',function () {
                var obj=new Number(1);
                (obj instanceof Number).should.true();
                (obj instanceof Object).should.true();
            });
            it('instanceof运算符判断子类的依据为函数的prototype是否在对象的原型链中', function () {
                var obj = {};
                var ctor1 = function () {};
                var ctor2 = function () {};
                ctor1.prototype = obj;
                ctor2.prototype = obj;
                var child = {};
                child.__proto__ = obj;
                (child instanceof ctor1).should.true();
                (child instanceof ctor2).should.true();
                var grandChild = {};
                grandChild.__proto__ = child;
                (grandChild instanceof ctor1).should.true();
            });
        });
        
        describe('delete运算符',function () {
            it('delete删除数组元素并不改变数组长度',function () {
                var arr=[1,2,3];
                (delete arr[2]).should.true();
                (2 in arr).should.false();
                arr.length.should.equal(3);
            });
            it('在非严格模式中delete不可删除的属性不会报错，而是返回false,但是删除不存在的属性返回true',function () {
                var foo;
                (delete foo).should.false();
                (delete bar).should.true();
            });
        });
        
        describe('逗号运算符',function () {
            it('逗号运算符从左至右依次计算操作数的值，最后返回右操作数的值',function () {
                (1+1,1+2).should.equal(3);
            });
        });
    });
    
    describe('语句',function () {
        it('空语句只包含一个分号',function () {
            for (var arr=[],i = 0; i < 5; arr[i++]=i);//具有空循环体的循环
            arr.should.eql([1,2,3,4,5]);
        });
        
    })

});