import 'rxjs';
import { Observable } from 'rxjs/Observable';
import * as sinon from 'sinon';

describe('utility', () => {
    /**
     * Perform a side effect for every emission on the source Observable,
     * but return an Observable that is identical to the source.
     */
    it('do', () => {
        const spy = sinon.spy();
        const arr: number[] = [];
        Observable
            .of(1, 2, 3)
            .do(x => arr.push(x))
            .subscribe(spy);
        expect(spy.args.reduce((acc, curr) => acc.concat(curr)))
            .toEqual([1, 2, 3]);
        expect(arr).toEqual([1, 2, 3]);
    });
});