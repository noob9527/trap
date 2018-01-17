import 'rxjs';
import { Observable } from 'rxjs/Observable';
import * as sinon from 'sinon';

describe('operator', () => {
    it('custom', () => {
        const spy = sinon.spy();
        function add1(input: Observable<number>): Observable<number> {
            return Observable.create((observer: any) => {
                input.subscribe({
                    next: v => observer.next(v + 1),
                    error: err => observer.error(err),
                    complete: () => observer.complete(),
                });
            });
        }
        const observable = add1(Observable.from([1, 2, 3]));
        observable.subscribe(spy);
        expect(spy.getCalls().map(e => e.args[0]))
            .toEqual([2, 3, 4]);
    });
});
