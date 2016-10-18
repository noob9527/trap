/**
 * Created by xy on 16-7-1.
 */
var should=require('should');
var log=console.log.bind(console);

describe('Array',function () {
    describe('构造函数',function () {
        it('使用一个数值参数调用Array构造函数，此时该参数指的是创建数组的长度，所创建的数组是稀疏的',function () {
            var arr=new Array(5);
            arr.should.eql([,,,,,]);
            arr.length.should.equal(5);
        }); 
        it('使用多个参数调用构造函数，此时参数将成为新数组的元素',function () {
            var arr=new Array(1,2);
            arr.should.eql([1,2]);
        });
    });
    
    describe('直接量',function () {
        it('数组直接量元素列表中的元素可以省略，此时读取省略的值是undefined，数组是稀疏的',function () {
            var arr=[0,,2,,];
            arr.length.should.equal(4);
            should(arr[1]).undefined();
        });
        it('元素不存在与元素存在但值为undefined并不完全相同',function () {
            ('1' in [1,,3]).should.false();
            ('1' in [1,undefined,3]).should.true();
        });
        it('在元素列表最后可以添加单个逗号，并不会改变数组的长度',function () {
            [,].length.should.equal(1);
            [0,1,].length.should.equal(2);
        });
    });
    
    describe('数组的特性',function () {
        it('使用方括号访问数组元素与使用方括号访问对象属性一样，js会自动将数值索引值转换为字符串',function () {
            var arr=['a','b','c'];
            arr[1].should.equal(arr['1']).equal('b');
            var obj={'0':'a','1':'b','2':'c'};
            obj[1].should.equal(obj['1']).equal('b');
        });
        it('对于数组，当使用小于2^32-2的非负整数作为属性名时，数组会自动维护其length属性',function () {
            var arr=[];
            arr['-1']='a';
            arr[5]='b';
            arr[10]='c';
            arr[11.0]='e';//被自动转换为整数11
            arr[11.1]='e';
            arr[2<<32]='d';
            arr['a']='f';
            arr.length.should.equal(12);
        });
        it('length属性是可读写的,当为其length属性设定一个非负整数n时，索引值大于等于n的元素将被删除',function () {
            var arr=[1,,3,4];
            arr.length=2;
            arr.should.eql([1,,]);
        });
        it('可以将数组元素设为不可配置，因为无法删除，length属性也无法小于该元素的索引',function () {
            var arr=[1,2];
            Object.defineProperty(arr,'2',{});
            arr.length=2;
            arr.length.should.equal(3);
            (function () {arr.pop();}).should.throw("Cannot delete property '2' of [object Array]");
        });
        it('可以对数组元素使用delete运算符,但不会改变数组长度，只会将数组变为稀疏数组',function () {
            var arr=[1,2,3];
            delete arr['2'];
            arr.length.should.equal(3);
            arr.should.eql([1,2,,]);
        });
        it('数组元素也可以继承',function () {
            var parent=[1,2,3];
            var child=Object.create(parent);
            child[0].should.equal(1);
            child.length.should.equal(3);
        });
    });
    
    describe('数组遍历',function () {
        it('不要使用for/in循环遍历数组，因为它能够枚举到继承的属性名',function () {
            var arr=[1,2,3];
            Array.prototype['3']=4;
            var res=[];
            for(var i in arr){
                res.push(arr[i]);
            }
            res.length.should.equal(4);
            res.should.eql([1,2,3,4]);
            delete Array.prototype['3'];
        });
    });
    
    describe('数组方法', function () {
        describe('join', function () {
            it('join是split的逆向操作', function () {
                '1-2-3'.split('-').join('-').should.equal('1-2-3');
            });
            it('如果不指定参数，join默认使用逗号连接字符串', function () {
                [1,2,3].join().should.equal('1,2,3');
            })
        });
        
        describe('toString', function () {
            it('toString将数组中的每个元素转换为字符串，输出用逗号分隔的字符串列表', function () {
                [1,2,3].toString().should.equal("1,2,3");
            });
        });
        
        describe('reverse', function () {
            it('reverse会修改原数组，这很容易产生让人费解的效果', function () {
                var arr = [1,2,3];
                arr.reverse().should.eql(arr);
                arr.should.eql([3,2,1]);
            }) 
        });
        
        describe('sort', function () {
            it('sort同样会修改原数组', function () {
                var arr = [3,2,1];
                arr.sort().should.eql(arr);
                arr.should.eql([1,2,3]);
            });
            it('默认情况下,sort将元素都视为字符串，并按照字母表顺序排序', function () {
                var arr = [3,2,11];
                arr.sort().should.eql([11,2,3]);
            });
            it('默认情况下undefined元素被排到尾部，仅先于不存在的元素', function () {
                var arr = [,undefined,,'v'];
                arr.sort().should.eql(['v', undefined,,,]);
            });
            it('如果需要自定义排序规则，则需要传入比较函数',function () {
                var arr = [3,2,11];
                arr.sort(function (a, b) {
                    return a - b;
                }).should.eql([2,3,11]);
            });
        });
        
        describe('concat', function () {
            it('concat不会修改原数组', function () {
                var arr = [1,2,3];
                arr.concat(4).should.eql([1,2,3,4]);
                arr.should.eql([1,2,3]);
            });
            it('concat会扁平化参数中的数组，但是该过程不是递归的', function () {
                var arr = [1,2,3];
                arr.concat([4,5], [6, [7,8]]).should.eql([1,2,3,4,5,6,[7,8]]);
            });
        });
        
        describe('slice', function () {
            it('slice不修改原数组', function () {
                var arr = [1,2,3];
                arr.slice(1).should.eql([2,3]);
                arr.should.eql([1,2,3]);
            });
            it('slice返回的数组包含第一个参数指定位置，但不包含第二个参数指定位置', function () {
                [1,2,3].slice(1,2).should.eql([2]);
            });
            it('如果省略第二个参数，此时将返回第一个参数指定位置到数组结尾的所有元素', function () {
                [1,2,3].slice(1).should.eql([2,3]);
            });
            it('如果两个参数都省略，则返回原数组，该方法常用于将类数组对象转换为数组', function () {
                [1,2,3].slice().should.eql([1,2,3]);
                [].slice.call('abc').should.eql(['a','b','c']);
                [].slice.call({'0':'a','1':'b',length:2}).should.eql(['a','b']);
            });
            it('参数可以是负数，表示相对于数组最后一个元素的位置（-1表示最后一个元素）', function () {
                [1,2,3,4].slice(-3,-1).should.eql([2,3]);
            });
        });
        
        describe('splice', function () {
            it('splice会修改原数组', function () {
                var arr=[1,2,3] ;
                arr.splice(0,1);
                arr.should.eql([2,3]);
            });
            it('splice第一个参数指定插入或删除的起始位置，第二个参数指定删除元素的个数，如果省略，则一直删除到数组末尾', function () {
                [1,2,3,4].splice(0,2).should.eql([1,2]);
                [1,2,3,4].splice(1).should.eql([2,3,4]);
            });
            it('除前两个参数外，其后的任意个参数指定需要插入的元素，区别于concat，splice不会尝试扁平化数组', function () {
                var arr = [1,2,3];
                arr.splice(0,0,4,[5,6]);
                arr.should.eql([4,[5,6],1,2,3]);
            });
            it('注意splice并不返回修改后的数组，而是返回由删除元素组成的数组', function () {
                var arr = [1,2,3];
                arr.splice(0).should.eql([1,2,3]);
                arr.should.eql([]);
            });
        });
        describe('push,pop,shift,unshift', function () {
            it('push在数组尾部添加一个或多个元素，并返回数组的新长度', function () {
                var arr = [];
                arr.push(1,2,3).should.equal(3);
                arr.should.eql([1,2,3]);
            });
            it('pop删除数组最后一个元素,减小数组长度并返回删除的值', function () {
                var arr = [1,2,3];
                arr.pop().should.equal(3);
                arr.should.eql([1,2]);
            });
            it('unshift,shift类似与push,pop,不同是是它操作数组的头部,并修改其它元素的索引',function () {
                var arr=[1,2,3];
                arr.unshift(4).should.eql(4);
                arr.should.eql([4,1,2,3]);
                arr.shift().should.equal(4);
                arr.should.eql([1,2,3]);
            });
            it('注意使用多个参数调用unshift时是一次性插入的，最终元素在数组中的顺序与它们在参数列表中的顺序已知', function () {
                var arr = [];
                arr.unshift(1,2,3);
                arr.should.eql([1,2,3]);
            });
        });
        
        describe('ecmascript5数组方法' ,function () {
            it('大多数es5数组方法，第一个参数为一个函数，对数组中的每个元素调用该函数' +
                '如果是稀疏数组，不存在的元素会被跳过', function () {
                var arr = [undefined,,undefined];
                var count = 0;
                arr.forEach(function (e) {
                    count++;
                });
                var newArr = arr.map(function (e) {
                    return 1;
                });
                count.should.equal(2);
                newArr.should.eql([1,,1]);
            });
            it('大多数es5数组方法，调用函数可接受三个参数，分别是是数组元素，元素索引和数组本身', function () {
                var arr = [1,2,3];
                arr.forEach(function (ele, idx, array) {
                    array.should.equal(arr);
                    ele.should.equal(idx + 1);
                });
            });
            it('大多数es5数组方法，第二个参数是可选的，如果有第二个参数，则第二个参数作为它的this关键字的值来使用',function () {
                var arr = [1,2,3];
                arr.forEach(function (e) {
                    this.should.equal('foo');
                }, 'foo');
            });
            it('大多数es5数组方法不会修改原数组,但传入的函数可以修改这些数组',function () {
                var arr = [1,2,3];
                arr.map(function () {
                    return 0;
                });
                arr.should.eql([1,2,3]);
                arr.map(function () {
                    arr[2] = 4;
                });
                arr.should.eql([1,2,4]);
            });
            
            describe('forEach', function () {
                it('forEach函数始终返回undefined', function () {
                    var arr = [1,2,3] ;
                    var result = arr.forEach(function (e) {
                        return 'whatever';
                    });
                    should(result).undefined();
                });
                it('break,return关键字都无法让forEach函数在中途停止', function () {
                    var arr = [1,2,3,4,5];
                    var count = 0;
                    arr.forEach(function (e) {
                        count++;
                        return
                    });
                    count.should.equal(5);
                });
            });
            
            describe('map', function () {
                it('如果使用稀疏数组调用map。会返回相同长度的稀疏数组',function () {
                    var arr = [1,,2];
                    arr.map(function (e) {
                        return 0;
                    }).should.eql([0,,0]);
                });
            });
            
            describe('filter', function () {
                it('filter返回调用数组的一个子集，如果传入函数的返回值是truthy值，则该值被添加到子集并返回', function () {
                    var arr = [,null,undefined,0,'',NaN,{}];
                    arr.filter(function (e) {
                        return e;
                    }).should.eql([{}]);
                });
                it('filter返回的数组总是稠密的,因此可以用来压缩数组', function () {
                    var arr = [,,,,0,undefined];
                    arr.filter(function (e) {
                        return 1;
                    }).should.eql([0,undefined]);
                });
                it('可以使用filter来进行简单的数组去重', function () {
                    var arr = [1,1,2,3,'a','a','b'];
                    arr.filter(function (e, i, arr) {
                        return arr.indexOf(e) == i;
                    }).should.eql([1,2,3,'a','b']);
                })
            });
            describe('every,some', function(){
                it('every与some一旦确定了返回值，就会停止遍历数组元素', function () {
                    var arr = [false, true];
                    var count = 0;
                    arr.every(function (e) {
                        count++;
                        return e;
                    });
                    count.should.equal(1);
                    count = 0;
                    arr.some(function (e) {
                        count++;
                        return !e;
                    });
                    count.should.equal(1);
                });
                it('使用空数组调用every返回true,调用some返回false', function () {
                    var arr = [];
                    arr.every(function () {
                        return false;
                    }).should.true();
                    arr.some(function () {
                        return false;
                    }).should.false();
                });
            });
            describe('reduce,reduceRight', function () {
                it('reduce函数的回调函数，第一个参数是到目前为止的结果，第2-4个参数为数组元素，元素索引和数组本身', function () {
                    var arr = [1,2,3];
                    arr.reduce(function (accumulator, ele, index, arr) {
                        arr[index].should.equal(ele);
                        return accumulator + ele;
                    }).should.equal(6);
                });
                it('reduce函数的第二个参数为初始值，在其回调函数第一次调用时，作为第一个参数传入', function () {
                    var arr = [1,2,3];
                    arr.reduce(function (accumulator, ele) {
                        accumulator.should.equal(0);
                        return accumulator * ele;
                    }, 0).should.equal(0);
                });
                it('reduce函数的第二个参数可以省略，当不指定初始值时，它将使用数组的第一个元素作为初始值' +
                    '即第一次调用就使用了数组的前两个元素作为第一和第二个参数', function () {
                    var arr = [1,2,3];
                    var count = 0;
                    arr.reduce(function (a, e) {
                        count++;
                        return a+e;
                    }).should.equal(6);
                    count.should.equal(2);
                    
                    count = 0;
                    arr.reduce(function (a, e) {
                        count++;
                        return a+e;
                    }, 0).should.equal(6);
                    count.should.equal(3);
                });
                it('使用空数组，并且不带初始值调用reduce将导致异常，因此建议尽可能为其指定第二个参数', function () {
                    (function () {
                        [].reduce(function () {});
                    }).should.throw('Reduce of empty array with no initial value');
                });
                it('如果数组只有一个元素，没有指定初始值，或者指定了初始值，数组没有元素，' +
                    '则reduce函数只是返回那个值而不会调用化简函数', function () {
                    [1].reduce(function () {
                        throw new Error();
                    }).should.equal(1);
                    [].reduce(function () {
                        throw new Error();
                    },1).should.equal(1);
                });
                it('reduce函数中this指向全局对象', function () {
                    //javascript权威指南书上的一个bug(P159,书中提到"reduce与reduceRight可以接受一个可选参数，使用它来指定调用化简函数时this关键字的值")
                    var arr = [1,2,3];
                    var obj = {foo:'bar'};
                    arr.reduce(function (a, e) {
                        this.should.not.equal(obj);
                        return a+e;
                    },0,obj);
                });
                it('reduceRight工作原理与reduce一样，不同的是它按照索引从高到低处理数组', function () {
                    var arr = [2,3,4];
                    arr.reduce(function (a, e) {
                        return a-e;
                    }).should.equal(-5);
                    arr.reduceRight(function (a, e) {
                        return a-e;
                    }).should.equal(-1);
                });
                it('可以使用reduce来扁平化数组', function () {
                    [[0, 1], [2, 3], [4, 5]].reduce(function(a, b) {
                        return a.concat(b);
                    }).should.eql([0,1,2,3,4,5]);
                });
            });
        });

    })
});