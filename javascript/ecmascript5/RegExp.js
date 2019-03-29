/**
 * Created by xy on 16-12-25.
 */

var should = require('should');
var log = console.log.bind(console);

describe('RegExp', function () {
    describe('基础特性', function () {
        it('js正则表达式可以使用RegExp构造函数与直接量两种方式创建', function () {
            var pattern1 = /^$/;
            var pattern2 = new RegExp('^$');
            (pattern1 instanceof RegExp).should.true();
            (pattern2 instanceof RegExp).should.true();
        }); 
        it('由于字符串直接量与正则表达式都使用"\"符号作为转义字符的前缀，因此给RegExp传入字符串表述的正则表达式时' +
            '需要将单斜杠"\"替换成双斜杠"\\"', function () {
            (new RegExp('\d')).test('0').should.false();
            (new RegExp('\\d')).test('0').should.true();
        });
        it('RegExp构造函数的第二个参数指定正则表达式的修饰符，只允许传入g,i,m,u,y与它们的组合', function () {
            (function () {new RegExp(new RegExp('.', 'whatever'))})
                .should.throw('Invalid flags supplied to RegExp constructor \'whatever\'')
        });
        it('es5中正则表达式的每次运算都返回新对象', function () {
            function getPattern() {
                return /^$/;
            }
            var pattern1 = getPattern();
            var pattern2 = getPattern();
            pattern1.foo = 'foo';
            pattern1.should.not.eql(pattern2);
            should(pattern2.foo).undefined();
        });
    });
    
    describe('正则表达式语法', function () {
        it('\s匹配空格符，制表符与其它unicode空白符', function () {
            /\s/.test(' ').should.true();
            /\s/.test('\t').should.true();
        });
        it('\w并不等价于[a-zA-Z0-9]', function () {
            //javascript权威指南书上的一个bug(P256,书中提到"\w等价于[a-zA-Z0-9]")
            /\w/.test('_').should.true();
            /[a-zA-Z0-9]/.test('_').should.false();
            /[a-zA-Z0-9_]/.test('_').should.true();
        });
        it('\b匹配单词边界，[\b]匹配退格直接量', function () {
            /\b\d/.test('1').should.true();
            /[\b]\d/.test('1').should.false(); 
        });
        it('非贪婪匹配',function () {
            'aaa'.match(/a+/)[0].should.equal('aaa');
            'aaa'.match(/a+?/)[0].should.equal('a');
            /a+?b/.exec('aaab')[0].should.equal('aaab');//正则表达式总是从左往右寻找第一个可能匹配的位置，因此结果不是'ab'
        });
        it('选择项匹配尝试次序为从左往右，如果发现匹配项则忽略右边的匹配项', function () {
            /a|ab/.exec('ab')[0].should.equal('a');
        });
        it('使用转移符加数字来引用之前表达式捕获的文本内容', function () {
            (/(['"])[^'"]*\1/).test('\'abc\'').should.true();
            (/(['"])[^'"]*\1/).test('\'abc\"').should.false();
        });
        it('使用(?:)可以仅创建分组，而不创建引用', function () {
            var pattern = /(?:a)(b)\1/;
            //由于(?:)不创建引用，因此\1引用了(b)捕获的字符串
            (/(?:a)(b)\1/).test('aba').should.false();
            pattern.exec('abb')[1].should.equal('b');
        });
        it('使用(?=p)表示正向肯定查找(lookahead)，要求接下来的字符能够匹配p', function () {
            var pattern = /a(?=b)/;
            pattern.test('aa').should.false();
            pattern.exec('ab')[0].should.equal('a');
        });
        it('使用(?!p)表示正向否定查找(negative lookahead)，要求接下来的字符不能匹配p', function () {
            var pattern = /a(?!b)/;
            pattern.test('ab').should.false();
            pattern.exec('aa')[0].should.equal('a');
        });
        // es7 已经支持反向查找
        // it('js目前不支持lookbehind(目前在es7提案阶段)', function () {
        //     (function () {var pattern1 = /(?<=a)b/;}).should.throw();
        //     (function () {var pattern2 = /(?<!a)b/;}).should.throw();
        // });
        it('使用捕获与引用来替代lookbehind', function () {
            'static int'.replace(/(static\s)int/, '$1long').should.equal('static long');
        });
    });
    
    describe('正则表达式修饰符', function () {
        it('ignoreCase', function () {
            /a/.test('A').should.false();
            /a/i.test('A').should.true();
        });
        it('global', function () {
            'aaa'.replace(/a/, 'b').should.equal('baa');
            'aaa'.replace(/a/g, 'b').should.equal('bbb');
        });
        it('multiline(多行模式下^$除了能够匹配字符串开始与结束，还能匹配每行的开始与结束)', function () {
            /a$/.test('a\n').should.false();
            /a$/m.test('a\n').should.true();
        });
    });
    
    describe('正则表达式对象的属性', function () {
        it('source属性包含正则表达式的文本', function () {
            var pattern = /.*/;
            pattern.source.should.equal('.*');
        });
        it('flags, global, ignoreCase, multiline, sticky(es2015), unicode(es2015),用于指示该正则表达式的修饰符', function () {
            var pattern = /.*/;
            pattern.flags.should.equal('');
            (pattern.global || pattern.ignoreCase || pattern.multiline || pattern.sticky || pattern.unicode).should.false();
            pattern = /.*/gimuy;
            pattern.flags.should.equal('gimuy');
            (pattern.global || pattern.ignoreCase || pattern.multiline || pattern.sticky || pattern.unicode).should.true();
        });
        it('以上属性在es5中为正则表达式的实例属性，而在es2015中为RegExp.prototype的访问器属性', function () {
            var obj = Object.getOwnPropertyDescriptor(RegExp.prototype, 'global');
            obj.get.should.ok();
            should(obj.set).undefined();
        });
        it('lastIndex用于指定下次匹配的起始索引（只有正则表达式带有g标识符时才起作用）', function () {
            var pattern = /\d/g;
            pattern.lastIndex.should.equal(0);
            pattern.exec('123')[0].should.equal('1');
            pattern.lastIndex.should.equal(1);
            pattern.exec('123')[0].should.equal('2');
        });
    });
    
    describe('正则表达式对象的方法', function () {
        it('exec方法的行为类似于字符串match方法执行非全局检索' +
            '其返回数组的index属性指示了发生匹配的字符位置，input属性引用正在检索的字符串', function () {
            var pattern = /(\w)(\d)/;
            var arr = pattern.exec('a1b2c3');
            arr[0].should.equal('a1');
            arr[1].should.equal('a');
            arr[2].should.equal('1');
            arr.index.should.equal(0);
            arr.input.should.equal('a1b2c3');
        });
        it('与match方法不同的是，无论正则表达式是否带有全局修饰符g，exec始终返回本次匹配的完整信息' +
            '并通过lastIndex来记录下次匹配的起始位置', function () {
            var pattern = /(\w)(\d)/g;
            var str = 'a1b2c3';
            str.match(pattern).should.eql(['a1', 'b2', 'c3']);

            pattern.lastIndex.should.equal(0);
            
            var res2 = Array.from(pattern.exec(str));
            pattern.lastIndex.should.equal(2);
            res2.should.eql(['a1','a','1']);
        });
        it('如果exec没有发现匹配结果，则会自动将lastIndex重置为0', function () {
            var pattern = /\d/g;
            pattern.lastIndex = 1;
            should(pattern.exec('1')).null();
            pattern.lastIndex.should.equal(0);
        });
        it('test方法行为类似于exec，当exec返回结果为null时，test返回false' +
            '它们都会使用lastIndex来记录下次匹配的起始位置', function () {
            var pattern = /\d/g;
            pattern.exec('1').should.ok();      //匹配成功，lastIndex设为1
            pattern.test('1').should.false();   //匹配失败，lastIndex重置为0
            pattern.test('1').should.true();    //匹配成功
        });
        it('当使用带有修饰符g的正则表达式来检测多个字符串，需要小心处理lastIndex，否则会带来非预期的结果', function () {
            var pattern = /\d/g;
            pattern.test('1').should.true();
            pattern.test('2').should.false();
        });
    });
    
    describe('String类与正则表达式相关的方法', function () {
        describe('search', function () {
            it('search返回匹配字符串的起始位置，如果找不到则返回-1', function () {
                'a'.search(/a/).should.equal(0);
                'a'.search(/b/).should.equal(-1);
            });
            it('如果search的参数不是正则表达式，则会自动调用RegExp构造函数将其转为正则表达式', function () {
                'a'.search('.').should.equal(0);
            });
        });
        describe('match', function () {
            it('match的参数为正则表达式，如果参数不是正则表达式，同样会自动调用RegExp构造函数将其转换为正则表达式', function () {
                'a'.match('.')[0].should.eql('a');
            });
            it('match方法匹配失败返回null', function () {
                should('a'.match(/b/)).null();
            });
            it('如果正则表达式带有修饰符g，则该方法返回的数组包含字符串中所有匹配的结果', function () {
                '1,2,3'.match(/\d/g).should.eql(['1','2','3']);
            });
            it('如果正则表达式没有修饰符g，则该方法只检索第一个匹配，但仍然返回一个数组' +
                '数组的第一个元素为匹配的字符串，余下的元素为圆括号捕获的子字符串', function () {
                var str = 'http://staynoob.cn/index.html';
                var pattern = /(\w+):\/{2}([\w.]+)\/(\S*)/;
                var res = str.match(pattern);
                res[0].should.equal(str);
                res[1].should.equal('http');
                res[2].should.equal('staynoob.cn');
                res[3].should.equal('index.html');
            });
            it('如果正则表达式没有修饰符g，则调用结果等价于给该正则表达式的exec()方法传入字符串', function () {
                var str = 'http://staynoob.cn/index.html';
                var pattern = /(\w+):\/{2}([\w.]+)\/(\S*)/;
                str.match(pattern).should.eql(pattern.exec(str));
            });
        });
        describe('replace', function () {
            it('replace第一个参数为正则表达式，第二个参数为要执行替换的字符创，如果正则表达式带有修饰符g' +
                '则源字符串中所有匹配的子串都会被替换，否则只替换匹配的第一个子串', function () {
                'aaa'.replace(/a/,'b').should.equal('baa');
                'aaa'.replace(/a/g,'b').should.equal('bbb');
            });
            it('如果replace的第一个参数是字符串而不是正则表达式，则replace将直接搜索这个字符串（这里与search方法行为不一样）', function () {
                'a'.replace('.','b').should.equal('a');
            });
            it('可以在要替换的字符串中使用$加数字来引用捕获的子字符串，数字从1开始', function () {
                '"abc"'.replace(/"([^"]*)"/, '“$1”').should.equal('“abc”');
            });
            it('replace的第二个参数还可以是函数，还函数将在每个匹配的结果上调用，它返回的字符串则作为要替换的文本' +
                '传入该函数的第一个参数是匹配该模式的字符串，接下来是捕获的子字符串' +
                '倒数第二个参数指定发生匹配的位置，最后一个参数为String本身', function () {
                var str = 'abc'.replace(/(a)(b)/, function ($0, $1, $2, $3, $4) {
                    $0.should.equal('ab');
                    $1.should.equal('a');
                    $2.should.equal('b');
                    $3.should.equal(0);
                    $4.should.equal('abc');
                    return $0;
                });
                str.should.equal('abc');
            });
        });
        describe('split', function () {
            it('split的参数可以是正则表达式', function () {
                '1 ,   2, 3'.split(/\s*,\s*/).should.eql(['1','2','3']); //允许分隔符的两侧有任意多的空白符号
            });
            it('split不会自动将其参数转换为正则表达式', function () {
                '1.2.3'.split('.').should.eql(['1','2','3']);
                '1.2.3'.split(/./).should.eql([ '', '', '', '', '', '' ])
            });
        });
        describe('string相关的正则表达式方法不会用到正则表达式对象的lastIndex属性', function () {
            //javascript权威指南书上的一个bug(P265,书中提到"String方法只是简单的将lastIndex重置为0"，事实上只有match与replace方法会重置lastIndex)
            it('match与replace方法会自动将参数正则表达式重置为0', function () {
                var pattern = /\d/g;
                pattern.lastIndex = 1;
                '1'.match(pattern).should.ok();
                pattern.lastIndex.should.equal(0);
                
                pattern.lastIndex = 4;
                '123'.replace(pattern, '0').should.equal('000');
                pattern.lastIndex.should.equal(0);
            });
            it('search与split', function () {
                var pattern = /\d/g;
                pattern.lastIndex = 1;
                '1'.search(pattern).should.equal(0);
                pattern.lastIndex.should.equal(1);
                pattern.lastIndex = 3;
                'a0b'.split(pattern).length.should.equal(2);
                pattern.lastIndex = 3;
            });
        });
    });
});
