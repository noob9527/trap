describe('merging interface', () => {

    it('merge property', () => {
        interface Foo {
            foo: number;
        }
        interface Foo {
            bar: number;
        }
        // correct
        const foo: Foo = { foo: 1, bar: 2 };
        // incorrect
        // const foo: Foo = { foo: 1, bar: 2, baz: 3 };
    });

    it('funciton property with the same name is treated as describing an overload', () => {
        interface Foo {
            fn(input: number): number;
        }
        interface Foo {
            fn(input: string): string;
        }
        const foo: Foo = { fn: e => e };
        // correct
        foo.fn(1);
        foo.fn('1');
        // incorrect
        // foo.fn(true)
    });
});

/* tslint:disable */
namespace Np1 {
    export const foo = 'foo'
}
namespace Np1 {
    export const bar = 'bar'
}
test('merging namespace', () => {
    expect(Np1.foo).toBe('foo')
    expect(Np1.bar).toBe('bar')
});

// merging namespace with classes
// ex: describing inner classes.
class Np2 {
    foo: Np2.Foo;
}
namespace Np2 {
    export class Foo { }
}
test('merging namespace with classes', () => {
    const np2 = new Np2();
    np2.foo = new Np2.Foo();
});

// merging namespace with function
// ex: adding properties onto functions
function Np3() {
}
namespace Np3 {
    export const foo = 'foo';
}
test('merging namespace with function', () => {
    expect(Np3.foo).toBe('foo');
});

// merging namespace with enums
enum Np4 {
    RED, GREEN, BLUE
}
namespace Np4 {
    export function values() {
        return [
            Np4.RED,
            Np4.GREEN,
            Np4.BLUE,
        ];
    }
}
test('merging namespace with enums', () => {
    expect(Np4.values()).toEqual([ 0, 1, 2 ]);
});
/* tslint:enable */
