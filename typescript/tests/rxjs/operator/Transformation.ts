import 'rxjs';

import { Observable } from 'rxjs/Observable';
import * as sinon from 'sinon';

describe('transformation', () => {
    it('scan', () => {
        const spy = sinon.spy();
        Observable
            .from([1, 2, 3])
            .scan((acc, curr) => acc + curr)
            .subscribe(spy);
        expect(spy.callCount).toBe(3);
        expect(spy.args.reduce((acc, curr) => acc.concat(curr)))
            .toEqual([1, 3, 6]);
    });

    /**
     * Projects each source value to an Observable which is merged in the output Observable,
     * emitting values only from the most recently projected Observable.
     */
    it('switchMap', () => {
        const spy = sinon.spy();
        // 当发出一个新的内部 Observable 时，
        // switchMap 会停止发出先前发出的内部 Observable 并开始发出新的内部 Observable 的值
        // 这里的例子是同步的，因此并不能很好的解释上面的规则,
        Observable
            .of(1, 2)
            .switchMap((e: number) => {
                switch (e) {
                    case 1:
                        return Observable.of('foo').repeat(2);
                    case 2:
                        return Observable.of('bar').repeat(2);
                }
            })
            .subscribe(spy);
        expect(spy.callCount).toBe(4);
        expect(spy.args.reduce((acc, curr) => acc.concat(curr)))
            .toEqual(['foo', 'foo', 'bar', 'bar']);
    });
});