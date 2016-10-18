/**
 * Created by xy on 16-6-30.
 */
var should=require('should');
var log=console.log.bind(console);

describe('Number',function () {
    describe('存储方式',function () {
        it('js所有数值数据均使用IEEE754浮点类型64位格式进行存储,这意味着存在舍入误差',function () {
            (0.15+0.15==0.3).should.true();
            (0.25+0.05==0.3).should.true();
            (0.1+0.2==0.3).should.false();
        });
    });

    describe('位操作符先将64位数值转换为32位整数，操作完成后再将结果转换为64位,使用二进制补码规则表示负数',function () {
        it('NaN与Infinity在进行位运算时被当成0处理',function () {
            (~NaN).should.equal(-1);
            (~Infinity).should.equal(-1);
            (~-Infinity).should.equal(-1);
        });
        it('使用~运算符表示按位取反',function () {
            //对一个值使用~运算符相当于改变其符号并-1;
            (~(-1)).should.equal(0);
            //计算一个负数的二进制补码相当于其正数的二进制码取反再+1
        });
        it('使用^操作符表示按位异或XOR',function () {
            (0^-1).should.equal(-1);
        });
        it('左移使用0填充右侧空位(需要考虑符号位)',function () {
            (-1<<1).should.equal(-2);
            //javascript高级程序设计书中的一个bug（P43,"左移不会影响操作数的符号位"）
            (1<<31).should.equal(-2147483648);
            (-3<<30).should.equal(1<<30);
        });
        it('右移使用符号位填充左侧的空位',function () {
            (-2>>1).should.equal(-1);
        });
        it('无符号右移使用0填充左侧空位',function () {
            (-2>>>1).should.equal(~(1<<31));
        });
    });
    
    describe('进制',function () {
        it('前导0与0x分表作为8进制与16进制的字面量表示',function () {
            (010+1==9).should.true();//严格模式下会报错
            (0x10+1==17).should.true();
        });
        it('使用parseInt将指定进制数转换为10进制',function () {
            parseInt(70,8).should.equal(56);
        });
        it('使用toString可以将10进制数转换为任意有效进制格式表示的字符串',function () {
            var num=17;
            num.toString(2).should.equal('10001');
            num.toString(8).should.equal('21');
            num.toString(16).should.equal('11');
        });
    });
    
    describe('精度',function () {
        it('toFixed按照参数指定的小数位返回数值的字符串表示(四舍五入或填充0)',function () {
            (10).toFixed(1).should.equal('10.0');
            (10.05).toFixed(1).should.equal('10.1');
        });
        it('toExponential同样由参数决定小数位数，但其确保小数点前只有一位',function () {
            (10).toExponential(2).should.equal('1.00e+1');
        });
        it('toPrecision只在参数指定的有效位数少于数字整数部分的位数，才转换为指数形式',function () {
            (123.456).toPrecision(4).should.equal('123.5');
            (123.456).toPrecision(2).should.equal('1.2e+2');
        });
    });

    describe('最值',function () {
        it('Number.MAX_VALUE加1或者减1依然等于Number.Max_VALUE(因为计算结果丢失精度)',function () {
            //javascript权威指南书上的一个bug(P37,书中结果是Infinity)
            (Number.MAX_VALUE+1).should.equal(Number.MAX_VALUE);
            (Number.MAX_VALUE-1).should.equal(Number.MAX_VALUE);
        });
        it('Number.MIN_VALUE/2等于0(发生下溢)',function () {
            (Number.MIN_VALUE/2).should.eql(0);
            (-Number.MIN_VALUE/2).should.eql(-0);
        });
    });
    
    describe('0与无穷',function () {
        it('一个数字除以0可能得到三种结果,分别是Infinity,-Infinity,NaN',function () {
            (0/0).should.NaN();
            (1/0===Infinity).should.true();
            (-1/0===-Infinity).should.true();
        });       
        it('正0等于负0',function () {
            (0).should.equal(-0);
        });
        it('正Infinity不等于负Infinity',function () {
            Infinity.should.not.eql(-Infinity);
            //可以使用这个特性来区分0与-0
            var zero=0,negZero=-0;
            zero.should.equal(negZero);
            (1/zero).should.not.eql(1/negZero);
        });
    });
    
    describe('布尔运算',function () {
        it('Number类型包含三个"falsy"值，分别是NaN,0,-0',function () {
            (0).should.not.ok();
            (-0).should.not.ok();
            NaN.should.not.ok();
        });

        it('NaN能够隐式转换成假值，但NaN不等于任何值',function () {
            (!!NaN).should.false();
            (NaN==false).should.false();
            (NaN==NaN).should.false();
            (NaN>0).should.false();
            (NaN<=0).should.false();
            (NaN!=NaN).should.true();
        });       
    });
    
    describe('数值转换',function () {
        describe('Number()函数',function () {
            it('Boolean值分别转为0与1',function () {
                Number(true).should.equal(1);
                Number(false).should.equal(0);
            });
            it('null转为0,undefined转为NaN',function () {
                Number(null).should.equal(0);
                Number(undefined).should.be.NaN();
            });
            it('空字符串转换为0',function () {
                Number("").should.equal(0);
            });
            it('忽略字符串两边的空格',function () {
                Number(" 1 ").should.equal(1);
                Number("1 1").should.be.NaN();
            });
            it('如果字符串包含前导0x，则将其视为16进制并转换为相同大小的十进制值，但前导0不会被视为8进制，而是直接忽略',function () {
                Number("0x10").should.eql(16);
                Number("010").should.eql(10);
            });
            it('如果是对象，先调用其valueOf方法，如果没有valueOf方法，则继续调用toString方法，再依据字符串转换规则将其转换为数值',function () {
                var obj={};
                Number(obj).should.be.NaN();
                obj.toString=function () {
                    return '1';
                };
                Number(obj).should.equal(1);
                obj.valueOf=function () {
                    return '2';
                };
                Number(obj).should.equal(2);
            });
        });
        
        describe('parseInt与parseFloat',function () {
            it('parseInt与parseFloat仅用于将字符串转换为数字,其它参数类型会先被自动转换成字符串',function () {
                parseInt(1).should.equal(1);
                parseInt(true).should.be.NaN();
                parseInt(null).should.be.NaN();
                parseInt(undefined).should.be.NaN();
                parseInt({}).should.be.NaN();
            });
            it('如果第一个非空格字符串不是数字或符号，则返回NaN',function () {
                parseInt(' 1').should.equal(1);
                parseInt('').should.be.NaN();
            });
            it('当解析到一个数字字符后，忽略后续的非数字字符',function () {
                parseInt('123abc').should.equal(123);
            });
            it('parseInt忽略小数点，parseFloat忽略第二个小数点',function () {
                parseInt('123.4').should.equal(123);
                parseFloat('123.4.5').should.equal(123.4);
            });
            it('parseInt能够识别16进制的前导0x,但忽略前导0,parseFloat则只解析十进制值',function () {
                parseInt('010').should.equal(10);
                parseInt('0x10').should.equal(16);
            });
            it('parseInt有第二个参数用于指定转换的基数,取值范围为2-36',function () {
                parseInt('10',8).should.equal(8); 
            });
        });
    });
});