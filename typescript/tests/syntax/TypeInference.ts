/* tslint:disable */
test('best common type', () => {
    class Parent { }
    class Child1 extends Parent { }
    class Child2 extends Parent { }
    class Child3 extends Parent { }
    // 这里数组会被推断为 Child1|Child2|Child3 类型
    let arr1 = [new Child1(), new Child2(), new Child3()];
    // 这里数组会被推断为 Parent 类型
    let arr2 = [new Child1(), new Child2(), new Child3(), new Parent()];
});

test('contextual type', ()=>{
    interface Foo{
        (foo: String):String;
    }
    class Bar extends String {
        bar(){
            return this.slice();
        }
    }
    // correct
    let foo1:Foo = (foo) => foo.slice();
    let foo2:Foo = (foo: any) => foo.slice();
    let foo3:Foo = (foo: Bar) => foo.bar();
    // error
    // let foo = (foo) => foo.slice();
});
