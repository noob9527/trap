import { TestScheduler } from 'rxjs/Rx';
import 'rxjs';
import { Observable } from 'rxjs/Observable';
import * as sinon from 'sinon';

describe('filtering', () => {

    let testScheduler: TestScheduler;
    let expectObservable: any;
    let expectSubscriptions: any;
    beforeEach(() => {
        testScheduler = new TestScheduler(
            (actual, expected) => expect(actual).toEqual(expected)
        );
        expectObservable = testScheduler.expectObservable
            .bind(testScheduler)
        expectSubscriptions = testScheduler.expectSubscriptions
            .bind(testScheduler);
    });
    afterEach(() => {
        try {
            testScheduler.flush();
        } finally {
            testScheduler = new TestScheduler(
                (actual, expected) => expect(actual).toEqual(expected)
            );
        }
    });

    /**
     * Emits values emitted by the source Observable so long as each value
     * satisfies the given predicate,
     * and then completes as soon as this predicate is not satisfied.
     */
    it('takeWhile', () => {
        const observable = Observable
            .of(1, 2, 3, 4, 5)
            .takeWhile((x: number) => x <= 3);
        expectObservable(observable)
            .toBe('(012|)', [1, 2, 3])
    });
});