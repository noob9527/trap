import 'rxjs/add/operator/observeOn';

import { Observable } from 'rxjs/Observable';
import { async } from 'rxjs/scheduler/async';
import * as sinon from 'sinon';

describe('Scheduler', () => {
    it('async', done => {
        const spy1 = sinon.spy();
        const spy2 = sinon.spy();
        const spy3 = sinon.spy();
        const observable = Observable.create((observer: any) => {
            observer.next(1);
            observer.next(2);
            observer.next(3);
            observer.complete();
        }).observeOn(async);

        spy1();
        observable.subscribe({
            next: spy3,
            complete: done,
        });
        spy2();
        setTimeout(() => {
            expect(spy2.calledAfter(spy1));
            expect(spy3.calledAfter(spy2));
            expect(spy3.getCalls().map(e => e.args[0]))
                .toEqual([1, 2, 3]);
        });
    });
});