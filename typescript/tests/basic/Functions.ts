/* tslint:disable */
test('使用?表示函数的可选参数', () => {
    // correct
    function foo(foo: string, bar?: string) {
        return `${foo}${bar || ''}`;
    }
    // error
    // function foo(foo?: string, bar: string) {
    //     return `${foo}${bar || ''}`;
    // }
});

test('带默认值的参数不一定在参数列表最后，可以传入undefined来使用参数的默认值', () => {
    const foo = (foo: string = 'foo', bar: string) => `${foo}${bar || ''}`;
    expect(foo(undefined, 'bar')).toBe('foobar');
});

test('noImplicitThis编译选项', () => {
    // correct
    const foo = {
        bar: 'bar',
        foo() {
            return () => {
                return this.bar;
            }
        }
    }
    // error
    // const foo = {
    //     bar: 'bar',
    //     foo() {
    //         return function() {
    //             return this.bar;
    //         }
    //     }
    // }
    expect(foo.foo()()).toBe('bar');
});

test('指明函数中this的类型', () => {
    // correct
    function foo(this: string) {
        return this.length;
    }

    // error
    // function bar() {
    //     return this.length;
    // }
});

test('overloads', () => {
    interface Annotation {
        value: string;
    }
    function fun(value: string): Annotation;
    function fun(annotation: Annotation): Annotation;
    function fun(arg: any): Annotation {
        if (typeof arg === 'object') return arg;
        if (typeof arg === 'string') return { value: arg };
    }
    // correct
    fun('');
    fun({ value: '' });
    // error
    // fun(1);
});
