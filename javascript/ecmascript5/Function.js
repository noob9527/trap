/**
 * Created by xy on 16-7-1.
 */
var should=require('should');
var log=console.log.bind(console);

describe('Function',function () {
    describe('函数的基本特性', function () {
        it('js函数是一种特殊的对象，使用typeof运算符得到的值是"function"', function () {
            var fn = function () {};
            (typeof fn).should.equal('function');
        });
        it('当函数作为方法调用时，除了可以使用点操作符，还可以使用方括号来调用函数', function () {
            (0['toString']()).should.equal('0');
        });
        it('如果return语句没有与之相关的表达式，或者函数没有return语句，则返回undefined给调用者' +
            '有时候人们把没有返回值的函数称为过程(subroutine)', function () {
            should(foo()).undefined();
            should(bar()).undefined();
            function foo() {
                return;
            }
            function bar() {}
        });
        it('函数对象的内部状态不仅包含函数的代码逻辑，还必须引用当前的作用域链' +
            '这个作用域是在函数定义时决定的，而不是在函数调用时决定的', function () {
            var scope = 'global';
            function outer() {
                var scope = 'local';
                function inner() {
                    return scope;
                }
                return inner;
            }
            outer()().should.equal('local');//不管在什么地方调用inner函数，其scope的值始终是local
        });
        it('每次调用函数都会创建新的作用域链，同一个外部函数内定义的多个嵌套函数共享一个作用域链', function () {
            function counter() {
                var n = 0;
                return {
                    count:function () {
                        return n++;
                    },
                    reset:function () {
                        n=0;
                    }
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
        it('同一个外部函数内定义的多个嵌套函数共享一个作用域链', function () {
            function outer() {
                var funcs = [];
                for (var i = 0; i < 5; i++) {
                    funcs[i]=function () {
                        return i;
                    }       
                }
                return funcs;
            }
            outer().forEach(function (fn) {
                fn().should.equal(5);//由于共享变量i,因此funcs数组中的所有函数都会返回i的最终值
            });
        });
    });
    
    describe('Function构造函数', function () {
        it('可以通过Function构造函数来定义函数，Function构造函数的最后一个实参就是函数体', function () {
            var fn = new Function('x', 'y', 'return x*y');
            fn(2,3).should.equal(6);
        });
        it('使用Function构造函数创建函数，其函数体编译总会在全局作用域执行，因此函数体无法访问任何局部变量' +
            '可以将Function构造函数认为是在全局作用域执行eval', function () {
            var foo = 'foo';
            var fn = new Function("return foo");
            fn.should.throw('foo is not defined');
        });
        it('Function构造函数创建的函数name属性为anonymous', function(){
            var fn = new Function();
            fn.name.should.equal('anonymous');
        });
    });
    
    describe('函数的属性,方法', function () {
        it('length属性代表函数形参的数量，通常也是函数调用时期望传入的实参个数，该属性是只读的', function () {
            fn.length.should.equal(2);
            fn();
            function fn(arg1, arg2) {
                fn.length.should.equal(2);
                fn.length=1;    //该赋值无效
                fn.length.should.equal(2);
            }
        });
        it('由于函数是特殊的对象，因此可以给函数对象添加任意属性', function () {
            fn.foo = 'bar';
            fn();
            function fn() {
                fn['foo'].should.equal('bar');
            }
        });
        it('call与apply可以将函数看做任意对象的方法来调用',function () {
            var foo = {name:'foo'};
            var bar = {name:'bar'};
            foo.fn = function () {
                return this.name;
            };
            foo.fn().should.equal('foo');
            foo.fn.call(bar).should.equal('bar');
        });
        it('apply接受实参数组，因此当需要使用数组来调用期望可变参数的函数时，可以使用apply方法',function () {
            var arr = [1,2,3,4]; //这里需要获取数组的最大值，但Math.max只接受数字作为参数
            Math.max.apply(Math, arr).should.equal(4);  //借助apply的特性来调用函数
            //补充:es6还可以使用spread operator来完成上述功能
            Math.max(...arr).should.equal(4);
        });
        it('apply的参数数组还可以是类数组对象，如arguments', function () {
            var max = function () {
                return Math.max.apply(Math, arguments);
            };
            max(3,1,5,4).should.equal(5);
        });
        it('bind方法接受一个对象作为参数，并返回一个新的函数，新的函数会把原函数作为参数对象的方法来调用', function () {
            var foo = {name:'foo'};
            var bar = {name:'bar'};
            var fn1 = function () {
                this.x = 'x';
                this.should.equal(foo);
                return this.name;
            };
            var fn2 = fn1.bind(foo);
            bar.fn2 = fn2;
            fn2().should.equal('foo');
            foo.x.should.equal('x');
            bar.fn2().should.equal('foo'); //这里即便将fn作为其它对象的方法来调用，其中this的值仍然指向参数对象
        });
        it('除了第一个实参外，传入bind方法的其它实参会被绑定到新函数的实参列表', function () {
            //ex1
            var fn1 = function (x, y) {
                return x+y;
            };
            var fn2 = fn1.bind(null, 1); //将原函数的x绑定到1
            fn2(2).should.equal(3);      //传入2作为实参y
            //ex2
            var fn3 = function (y, z) {
                return this.x + y + z;
            };
            var fn4 = fn3.bind({x:1}, 2); //this.x绑定到1，y绑定到2
            fn4(3).should.equal(6);       //传入3作为实参z
        });
        it('bind方法返回的函数对象length属性是原函数的形参个数减去绑定的实参个数', function () {
            var fn1 = function (x,y) {};
            var fn2 = fn1.bind(null, 1);
            fn1.length.should.equal(2);
            fn2.length.should.equal(1);
        });
        it('length属性的值不会因为bind而小于0', function () {
            var fn1 = function () {};
            var fn2 = fn1.bind(null, 1, 2, 3);
            fn1.length.should.equal(0);
            fn2.length.should.equal(0);
        });
        it('bind方法返回的函数没有prototype属性', function () {
            var fn1 = function () {};
            var fn2 = fn1.bind({});
            fn1.prototype.should.eql({});
            should(fn2.prototype).undefined();
        });
        it('bind方法返回的函数name属性是"bound"加上原函数名', function(){
            var fn1 = function(){};
            var fn2 = fn1.bind({});
            fn1.name.should.equal('fn1');
            fn2.name.should.equal('bound fn1');
        });
        it('尽管没有prototype属性，bind方法返回的函数依然可以作为构造函数调用' +
            '此时将忽略传入bind方法的对象(其它参数仍然可以成功绑定)，以构造函数的方式调用原始函数' +
            '新生成的对象原型指向的是原函数的prototype', function () {
            var obj = {foo:'foo'};
            var fn1 = function (x, y) {
                this.x = x;
                this.y = y;
            };
            var fn2 = fn1.bind(obj, 'x');
            var result = new fn2('y');
            result.should.eql(new fn1('x', 'y'));
            result.__proto__.should.equal(fn1.prototype); //虽然是以构造函数的方式调用fn2,但原型指向fn1
            (result instanceof fn2).should.true();
            (result instanceof fn1).should.true();
            obj.should.eql({foo:'foo'}); //因为this指向新对象，因此obj不会有变化
            fn2('y'); //如果不是以构造函数的形式调用，则this指向参数对象
            obj.should.eql({foo:'foo', x: 'x', y:'y'}); 
        });
        
    });

    describe('prototype属性', function () {
        it('prototype属性指向一个对象引用，当将函数作为构造函数使用时，新创建的对象会从原型对象上继承属性', function () {
            var obj = {foo:'bar'};
            ctor.prototype = obj;
            function ctor() {
                ctor.prototype.should.equal(obj);
                this.__proto__.should.equal(obj);
            }
            new ctor; // execute ctor as a constructor
        });
        it('除bind方法返回的函数外,js中每个函数都自动拥有一个prototype属性,该属性为一个对象' +
            '且该对象包含唯一一个不可枚举属性constructor，该属性的值指向关联的函数', function () {
            function ctor() {}
            ctor.prototype.constructor.should.equal(ctor);
        });
        it('当将函数作为构造函数调用时，新创建的对象会继承函数的prototype属性，因此在新创建的对象中可以使用constructor属性' +
            '访问该对象的构造函数' , function () {
            function ctor(){
                this.__proto__.should.equal(ctor.prototype);
                this.__proto__.constructor.should.equal(ctor);
                this.constructor.should.equal(ctor);
            }
            (new ctor).constructor.should.equal(ctor);
        });
        it('一些继承实现直接对函数的prototype属性赋值，这个导致新创建的对象无法访问到constructor属性', function () {
            var parent = {foo:'foo'}; //使用字面量创建的对象constructor指向Object
            var ctor = function(){};
            //这行的本意是让ctor创建的对象继承parent对象，但是却导致新创建的对象无法通过constructor属性引用到ctor函数,而是引用到Object函数
            ctor.prototype = parent; 
            (new ctor).constructor.should.equal(Object);
        });
    });
    
    describe('arguments对象', function () {
        it('可以使用arguments对象来获取未声明的实参', function () {
            function fn() {
                arguments[0].should.equal('bar');
            }
            fn('bar');
        });
        it('非严格模式下,arguments使用callee属性指代当前正在执行的函数,caller指代调用当前函数的函数' +
            '注意新版的node与chrome环境已经无法访问caller属性了',function () {
            function fn1() {
                fn2();
            }
            function fn2() {
                arguments.callee.should.equal(fn2); //callee属性的一个应用场景是在匿名函数中递归调用自身
                should(arguments.caller).undefined();
            }
            fn1();
        });
        it('严格模式下对callee, caller属性的读写操作会报错', function () {
            'use strict'
            function fn1() {
                return arguments.callee;
            }
            function fn2() {
                return fn2.caller;
            }
            fn1.should.throw()
            fn2.should.throw()
        });
    });

    describe('函数声明', function () {
        it('由于变量申明提前，因此使用函数声明语句声明的函数可以在“声明”之前调用，但声明表达式不行' +
            '因为赋值语句不会提前执行', function () {
            f().should.equal('foo');

            (typeof b).should.equal('undefined');
            (typeof bar).should.equal('undefined');

            //该函数声明提前至顶部,因此可以在“声明”之前调用
            function f() {
                return 'foo';
            }
            //该函数无法在声明前调用
            var b = function bar() {
                return 'bar';
            };
        });
        it('使用函数定义表达式声明函数时也可以包含函数名称，该名称将成为函数内部的局部变量，这在函数需要递归调用自身时有用', function () {
            var f = function fact(x) {
                if(x<=1) return 1;
                return x*fact(x - 1);
            };
            (typeof fact).should.equal('undefined');
        });
    });
    
    describe('this关键字', function () {
        it('如果函数挂载在一个对象上，作为对象的一个属性，就称它为对象的方法，当通过该对象来调用函数时' +
            '该对象就是此次调用的上下文，也就是this关键字的值', function () {
            var obj = {};
            obj.fn = function (){
                this.should.equal(obj);
            };
            obj.fn();
        });
        it('函数调用在非严格模式下，this指向全局对象，严格模式下指向undefined，因此可以借此判断当前是否为严格模式', function () {
            (function normal() {
                this.should.ok();
            })();
            (function strict() {
                'use strict'
                should(this).undefined();
            })();
        });
        it('嵌套的函数不会从调用它的函数中继承this关键字的值，如果需要在嵌套函数中访问外部函数的this值，需要将其保存到变量中', function () {
            var obj = {};
            obj.foo = function () {
                var self = this;
                bar();
                this.should.equal(obj);
                function bar() {
                    this.should.not.equal(obj);
                    self.should.equal(obj);
                }
            };
            obj.foo();
        });
    });

    describe('构造函数',function () {
        it('如果构造函数不需要参数，可以省略括号',function () {
            var obj=new Object;
            obj.should.be.ok();
        });
        it('使用new关键字创建对象时，js首先创建一个空对象，这个对象继承自构造函数的prototype属性' +
            '之后使用该对象作为this值来调用构造函数',function () {
            var obj = {foo:'bar'};
            ctor.prototype = obj;
            function ctor() {
                this.__proto__.should.equal(obj);
            }
            new ctor;
        });
        it('即便使用类似于方法调用的语法调用构造函数，构造函数中的this始终指向新创建的对象',function () {
            var obj = {foo:'bar'};
            obj.ctor = function () {
                this.should.not.eql(obj);
            };
            new obj.ctor;
        });
        it('如果构造函数使用return语句显式返回一个对象，则调用表达式的结果为该对象，而新创建的对象会被抛弃',function () {
            var foo={name:'foo'};
            var ctor=function () {
                this.name='bar';
                return foo;
            };
            var bar=new ctor;
            bar.name.should.equal('foo');
        });
        it('如果构造函数使用了return语句，没有指定返回值，或者返回的是primitive类型的值' +
            '则抛弃该返回值，使用新对象作为调用表达式的结果', function () {
            var ctor1 = function () {
                this.name='bar';
                return;
            };
            var ctor2 = function () {
                this.name='bar';
                return 'whatever';
            };
            (new ctor1).name.should
                .equal((new ctor2).name)
                .equal('bar');
        });
    });
    
});