/**
 * Created by xy on 16-12-31.
 */

var should = require('should');

describe('json', function () {
    describe('stringify', function () {
        it('如果两个对象互相引用,则无法直接序列化', function () {
            var foo = {};
            var bar = {};
            foo.bar = bar;
            bar.foo = foo;
            should(function(){ JSON.stringify(foo);}).throw('Converting circular structure to JSON');
        });
        it('JSON.stringify 只序列化自有可枚举属性', function(){
            var obj = Object.create({
                extendProp: 'extendProp',
            });
            Object.defineProperties(obj, {
                enumerableProp: {
                    value: 'enumerableProp',
                    enumerable: true,
                },
                enumerableGetter: {
                    get: function(){return 'enumerableGetter';},
                    enumerable: true,
                },
                unEnumerableProp: {
                    value: 'unEnumerableProp',
                },
                unEnumerableGetter: {
                    get: function(){return 'unEnumerableGetter';},
                },
            });
            Object.keys(JSON.parse(JSON.stringify(obj))).should
                .eql(['enumerableProp', 'enumerableGetter']);

        });
        it('第二个参数如果是函数，则每个属性都将经过该函数转换处理', function () {
            var obj = {
                foo:'foo',
                bar:'bar',
                baz:'baz'
            };
            JSON.stringify(obj, transformer)
                .should.equal('{"foo":"FOO","bar":0}');

            function transformer(key, value) {
                switch (key){
                    case 'foo':
                        return 'FOO';
                    case 'bar':
                        return 0;
                    case 'baz':
                        return undefined;
                    default:
                        return value;
                }
            }
        });
        it('第二个参数如果是数组，则只序列化数组中存在的属性名', function () {
            var obj = {
                foo:'foo',
                bar:'bar',
                baz:'baz'
            };
            JSON.stringify(obj, ['foo', 'bar', 'whatever'])
                .should.equal('{"foo":"foo","bar":"bar"}');
        });
        it('第三个参数用于指示每个属性前的空格数', function () {
            var obj = {
                foo:'foo',
                bar:'bar'
            };
            JSON.stringify(obj, null, 3)
                .split(/\n/)[1]
                .should.equal('   "foo": "foo",');
        });
        it('如果第三个参数是字符串，则该字符串将视为空格输出', function () {
            var obj = {
                foo:'foo',
                bar:'bar'
            };
            JSON.stringify(obj, null, 'abc')
                .split(/\n/)[1]
                .should.equal('abc"foo": "foo",');
        })
    });
});
