type Maybe<T> = T | null | undefined;

test('null与undefined只可以赋值给它们自身或any', () => {
    const a1: null = null;
    const a2: any = null;
    const b1: undefined = undefined;
    const b2: any = undefined;
});

test('只有undefined可以赋值给void类型', () => {
    let unusable: void;
    // correct
    unusable = undefined;
    // incorrect
    // unusable = null;
});

test('unlike regular mode, T,T|undefined,T|null are different types', () => {
    type A1<T> = T;
    type A2<T> = T | null;
    type A3<T> = T | undefined;
    type A4<T> = T | null | undefined;
    // incorrect
    // const a1: A1<string> = null;
    // const a3: A3<string> = null;
    // correct
    const a2: A2<string> = null;
    const a4: A4<string> = null;
});

/* tslint:disable */
test('assigned before use checking', () => {
    let x: number;
    let y: number | null;
    let z: number | undefined;
    // Error, reference not preceded by assignment
    // x;
    // y;
    // Ok
    z;
});
/* tslint:enable */

test('optinal parameters and properties', () => {
    const A1 = (x?: number) => x;
    const A2 = (x?: number | undefined) => x;
    A1(1);
    A1(undefined);
    A2(1);
    A2(undefined);
    // incorrect
    // A1(null);
    // A2(null);
});

/* tslint:disable */
test('local non-null and non-undefined type guards', () => {
    let x: number | null | undefined;

    const y: number = x ? x : 0;
    const z: number = x || 0;
    if (x) {
        // correct
        const y: number = x;
    } else {
        // incorrect
        // const y: number = x;
    }

    if (x != undefined) {
        const y: number = x;
    }

    if (x !== undefined) {
        // incorrect
        // const y: number = x;
        // correct
        const z: number | null = x;
    } else if (x !== null) {
        // incorrect
        // const y: number = x;
        // correct
        const z: number | undefined = x;
    } else {
        const y: number = x;
    }
});
/* tslint:enable */

test('dotted names in type guards', () => {
    interface Options {
        location?: {
            x?: number;
            y?: number;
        };
    }

    /* tslint:disable-next-line */
    let options: Maybe<Options>;
    if (options && options.location && options.location.x) {
        // correct
        const x = options.location.x;
        // A type guard for a dotted name has no effect
        // following an assignment to any part of the dotted name
        // incorrect
        // options = { location: { x: 0, y: 0 } };
        // const y = options.location.x;
    }
});

/* tslint:disable-next-line */
// see: https://www.typescriptlang.org/docs/handbook/release-notes/typescript-2-2.html#better-checking-for-nullundefined-in-operands-of-expressions
test('null/undefined in operands of expressions', () => {
    // incorrect
    // function add1(a: Maybe<number>, b: Maybe<number>) {
    //     return a + b;
    // }
    // correct
    function add2(a: Maybe<number>, b: Maybe<number>) {
        return (a || 0) + (b || 0);
    }
});

/* tslint:disable */
test('&& adds null and/or undefined to the type of the right operand', () => {
    const fn = () => Math.random() ? 1 : '';
    const x: string | number = fn();
    // incorrect
    // const res1: boolean = x && true;
    // correct
    const res2: string | number | boolean = x && true;
});

test('|| removes both null and undefined from the type of the left operand', () => {
    const x: boolean = null || true;
});

test('null and undefined types are not widened to any', () => {
    let x = null; // null is the only possible value for x
    // incorrect
    // x = undefined;
});

test('non-null assertion operator', () => {
    function fn(arg?: string) {
        // incorrect
        // arg.length
        // correct
        arg!.length
    }
});
