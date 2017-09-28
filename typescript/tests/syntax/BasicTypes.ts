/* tslint:disable */
test('tuple', () => {
    let tuple: [string, number];
    // correct
    tuple = ['foo', 1];
    tuple = ['foo', 1, 2];  // 越界元素使用联合类型 eg:string|number
    tuple = ['foo', 1, 2, 'bar'];
    // error
    // tuple = ['foo'];
    // tuple = [1, 'foo'];
    // tuple = ['foo', 1, true];
});

test('any与Object的区别', () => {
    const foo: any = 'foo';
    const bar: Object = 'bar';
    // correct
    foo.match;
    // error
    // bar.match
});

describe('空值', () => {

    it('void类型', () => {
        let unusable: void;
        unusable = null;
        unusable = undefined;
    });

    it('null与undefined是所有类型的子类型', () => {
        let foo: number = null;
        let bar: number = undefined;
    });

});

describe('never', () => {
    it('返回never类型的函数', () => {
        // 返回never的函数必须存在无法达到的终点
        function error(message: string): never {
            throw new Error(message);
        }
        // 推断的返回值类型为never
        function fail(): never {
            return error("Something failed");
        }
        // 返回never的函数必须存在无法达到的终点
        function infiniteLoop(): never {
            while (true) { }
        }
    });

    it('never类型是任何类型的子类型', () => {
        const error = (): never => {
            throw new Error();
        }
        expect(():number => error()).toThrow();
        expect(():string => error()).toThrow();
    });
});

test('类型断言', ()=>{
    let foo:any = 'foo';
    expect((<string>foo).length).toBe(3);
    expect((foo as string).length).toBe(3);
});

test('类型断言失败不会立即报错', () => {
    const foo: any = 'foo';
    const num: number = foo as number;
    expect(() => num.toFixed()).toThrow()
})