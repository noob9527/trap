/**
 * Created by xy on 16-7-1.
 */
var should = require('should');
var log = console.log.bind(console);

describe('Object', function () {

    describe('创建对象', function () {
        it('使用new关键字创建对象，该对象的原型就是构造函数的prototype属性', function () {
            var num = new Number(1);
            num.__proto__.should.equal(Number.prototype);
        });
        it('使用Object.create()方法创建对象，该方法的第一个参数作为新对象的原型', function () {
            var obj = Object.create({name: 'foo'});
            obj.__proto__.name.should.equal('foo');
            obj = Object.create(null);
            ('toString' in obj).should.false();
        });
        it('使用Object.create方法实现继承', function(){
            var Parent = function(name){
                this.name = name;
            };

            var Child = function(){
                Parent.apply(this, arguments);
            };

            Child.prototype = Object.create(Parent.prototype);

            var child = new Child('foo');

            child.name.should.equal('foo');
            (child instanceof Parent).should.true();
            Child.prototype.constructor.should.not.equal(Child); //这种方法的弊端
        });
        it('Object.create方法始终返回对象，因此无法直接用于创建函数的子类', function(){
            var Parent = function(){};
            var Child = Object.create(Parent);
            (Parent instanceof Function).should.true();
            (Child instanceof Function).should.true();
            (typeof Parent).should.equal('function');
            (typeof Child).should.equal('object');
            // Object.create 接受的第二个参数类似于 Object.defineProperties 方法的第二个参数
        });
    });

    describe('四种检测对象属性的方式', function () {
        it('in运算符检测对象自有属性或继承属性', function () {
            var obj = {x: 1};
            ('x' in obj).should.true();
            ('toString' in obj).should.true();
        });
        it('hasOwnProperty仅用来检测自有属性', function () {
            var obj = {x: 1};
            obj.hasOwnProperty('x').should.true();
            obj.hasOwnProperty('toString').should.false();
        });
        it('propertyIsEnumerable仅检测自有可枚举属性', function () {
            var obj = {x: 1};
            obj.propertyIsEnumerable('x').should.true();
            Object.defineProperty(obj, 'x', {enumerable: false});
            obj.propertyIsEnumerable('x').should.false();
        });
        it('使用!==undefined,该方法无法区分存在但值为undefined的属性', function () {
            var obj = {x: undefined};
            ('x' in obj).should.true();
            (obj.x !== undefined).should.false();
        });
    });

    describe('遍历属性名的几种方法', function () {
        it('for in循环遍历所有可枚举属性(包括继承属性)' +
            'Object.keys()返回可枚举的自有属性名组成的数组' +
            'Object.getOwnPropertyNames()返回所有自有属性名(包括不可枚举属性)', function () {
                var arr = [];
                var obj = Object.defineProperty({x: 1, y: 2}, 'y', {enumerable: false});
                ;
                obj.__proto__ = {z: 3};
                for (var prop in obj) {
                    arr.push(prop);
                }
                arr.should.eql(['x', 'z']);
                Object.keys(obj).should.eql(['x']);
                Object.getOwnPropertyNames(obj).should.eql(['x', 'y']);
            });
    });

    describe('存取器方法', function () {
        it('定义存取器方法', function () {
            var person = {
                $firstName: null,
                $lastName: null,
                get name() {
                    return this.$firstName + '.' + this.$lastName;
                },
                set name(value) {
                    this.$firstName = value.split('.')[0];
                    this.$lastName = value.split('.')[1];
                }
            };
            person.name.should.equal('null.null');
            person.name = 'foo.bar';
            person.$firstName.should.equal('foo');
            person.$lastName.should.equal('bar');
        });
        it('使用defineProperty在已经存在的对象上定义存取器方法', function () {
            var obj = {foo: 0};
            Object.defineProperty(obj, 'bar', {
                get: function () {
                    return this.foo + 1;
                },
                set: function (value) {
                    this.foo = value;
                }
            });
            obj.bar = 1;
            obj.foo.should.equal(1);
            obj.bar.should.equal(2);
        });
        it('存取器方法可以被继承', function () {
            function Person() {
                this.foo = 0;
            }
            Object.defineProperty(Person.prototype, 'bar', {
                get: function () {
                    return this.foo + 1;
                },
                set: function (value) {
                    this.foo = value;
                }
            });
            var p = new Person;
            p.bar = 1;
            p.foo.should.equal(1);
            p.bar.should.equal(2);
        });
    });

    describe('属性的特性', function () {
        it('使用Object.getOwnPropertyDescriptor方法获取对象自有属性的描述符', function(){
            var Parent = function(){
                this.parent = 'parent';
            }
            var Child = function(){
                this.child = 'child';
            }
            var child = new Child();

            should(Object.getOwnPropertyDescriptor(child, 'parent'))
                .undefined();
            Object.getOwnPropertyDescriptor(child, 'child')
                .should.be.ok();
        });

        it('默认情况下创建的属性都是可写，可枚举，可配置的', function () {
            var obj = {x: 1};
            Object.getOwnPropertyDescriptor(obj, 'x').should.eql({
                value: 1,
                writable: true,
                enumerable: true,
                configurable: true
            });
        });
        it('非严格模式下直接修改只读属性没有效果，但不会报错，如果属性是可配置的，则可以通过defineProperty修改只读属性的值', function () {
            var obj = Object.defineProperty({}, 'x', {value: 1, writable: false, configurable: true});
            obj.x = 2;
            obj.x.should.equal(1);
            Object.defineProperty(obj, 'x', {value: 2});
            obj.x.should.equal(2);
        });
        it('使用defineProperty创建的属性，默认的特性是false与undefined', function () {
            var obj = Object.defineProperty({}, 'x', {});
            Object.getOwnPropertyDescriptor(obj, 'x').should.eql({
                value: undefined,
                writable: false,
                enumerable: false,
                configurable: false
            });
        });
        it('defineProperty要么修改自有属性，要么新建自有属性，无法修改继承属性', function () {
            var obj = {x: 1};
            obj.__proto__ = {y: 2};
            Object.defineProperties(obj, {
                x: {writable: false},
                y: {writable: false}
            });
            obj.x.should.equal(1);    //x仅仅是修改了属性特性，原有的value值不变
            should(obj.y).undefined();//y是新建的自有属性，具有value默认值为undefined
        });
        it('如果属性不可配置，则无法修改它的属性特性，但仍然可以将它的可写性由true改为false', function () {
            var obj = Object.defineProperty({}, 'x', {writable: true});
            Object.getOwnPropertyDescriptor(obj, 'x').should.eql({
                value: undefined,
                writable: true,
                enumerable: false,
                configurable: false
            });
            Object.defineProperty(obj, 'x', {writable: false});
            Object.getOwnPropertyDescriptor(obj, 'x').writable.should.false();
            (function () {
                Object.defineProperty(obj, 'x', {writable: true})
            }).should.throw('Cannot redefine property: x');
        });
    });

    describe('对象的内置属性', function () {
        describe('原型属性', function () {
            it('使用__proto__属性可以直接访问对象原型属性，但并不推荐使用' +
                '一般使用Object.getPrototypeOf与isPrototypeOf方法来访问与检测原型属性', function () {
                    ({}).__proto__.should.eql(Object.prototype);
                    Object.getPrototypeOf({}).should.eql(Object.prototype);
                    Object.prototype.isPrototypeOf({}).should.true();
                });
            it('isPrototypeOf函数功能类似于instanceof运算符，它还检测对象的原型链', function () {
                var obj = new Number(1);
                (obj instanceof Number).should.true();
                (obj instanceof Object).should.true();
                Number.prototype.isPrototypeOf(obj).should.true();
                Object.prototype.isPrototypeOf(obj).should.true();
            });
            it('使用constructor.prototype来检测原型并不可靠，因为Object.create方法创建的对象其constructor属性始终指向Object()函数', function () {
                var obj = Object.create({foo: 'bar'});
                obj.constructor.prototype.should.eql(Object.prototype);
            });
            it('使用直接量创建的对象以Object.prototype作为原型' +
                '使用new创建的对象使用构造函数的prototype属性作为原型' +
                '使用Object.create创建的对象使用第一个参数作为原型', function () {
                    function ctor() {
                    }

                    ctor.prototype = {foo: 'bar'};
                    Object.getPrototypeOf({}).should.eql(Object.prototype);
                    Object.getPrototypeOf(new ctor).should.eql({foo: 'bar'});
                    should(Object.getPrototypeOf(Object.create(null))).null();
                });
        });

        describe('可扩展性', function () {
            it('可扩展性表示是否可以给对象添加新属性，使用Object.preventExtensible将对象转化为不可扩展的，该过程不可逆', function () {
                var obj = {};
                Object.preventExtensions(obj);
                obj.x = 1;
                should(obj.x).undefined();
            });
            it('使用Object.isExtensible查询对象的可扩展性', function () {
                Object.isExtensible({}).should.true();
            });
            it('可以给不可扩展的对象的原型添加新属性，这个不可扩展的对象依然会继承这些属性', function () {
                var parent = {};
                var obj = Object.create(parent);
                Object.preventExtensions(obj);
                parent.x = 1;
                obj.x.should.equal(1);
            });
        });
    });

    describe('seal与freeze', function () {
        it('Object.seal将对象变为不可扩展的，同时将该对象所有自有属性都设为不可配置，使用Object.isSealed检测对象是否封闭', function () {
            var obj = Object.seal({x: 1});
            Object.isExtensible(obj).should.false();
            Object.getOwnPropertyDescriptor(obj, 'x').configurable.should.false();
            (delete obj.x).should.false();
        });
        it('freeze更为严格，它还将所有自有数据属性设置为只读', function () {
            var obj = Object.freeze({x: 1});
            Object.isFrozen(obj).should.true();
            Object.isExtensible(obj).should.false();
            Object.getOwnPropertyDescriptor(obj, 'x').should.eql({
                value: 1,
                writable: false,
                enumerable: true,
                configurable: false
            });
        });
    });

    describe('序列化', function () {
        it('RegExp与Error对象序列化后将以空对象替代', function () {
            JSON.stringify(/a/).should.equal('{}');
            JSON.stringify(new Error('a')).should.equal('{}');
        });
        it('NaN与Infinity序列化的结果是null', function () {
            JSON.stringify(NaN).should.equal('null');
            JSON.stringify(Infinity).should
                .equal(JSON.stringify(-Infinity))
                .equal('null');
        });
        it('JSON.stringfy仅序列化对象可枚举的自有属性,不符合该条件的属性将被忽略', function () {
            var obj = Object.create({x: 1});
            Object.defineProperty(obj, 'y', {value: 2});
            obj.z = 3;
            JSON.stringify(obj).should.equal('{"z":3}');
        });
    });
});
