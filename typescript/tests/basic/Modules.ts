// https://www.typescriptlang.org/docs/handbook/modules.html#ambient-modules

// The declare keyword is used for ambient declarations
// where you want to define a variable
// that may not have originated from a TypeScript file
// In other words, var creates a new variable.
// declare is used to tell TypeScript
// that the variable has been created elsewhere
declare const bar = 'bar';
test('declare keyword(only for compiler)', () => {
    // compile error
    // because the compiler doesn't know anything about foo
    // console.log(foo);

    // runtime error
    // bar is still not defined
    // because declare won't do anything in runtime
    expect(() => console.log(bar)).toThrow();
});

describe('ambient modules', () => {
});
