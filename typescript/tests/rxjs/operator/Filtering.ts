import { TestScheduler } from 'rxjs/Rx';
import 'rxjs';
import { Observable } from 'rxjs/Observable';
import { ColdObservable } from 'rxjs/testing/ColdObservable';
import { HotObservable } from 'rxjs/testing/HotObservable';
import * as sinon from 'sinon';

describe('filtering', () => {

    let testScheduler: TestScheduler;
    let expectObservable: any;
    let expectSubscriptions: any;
    let hot: <T>(marbles: string, values?: any, error?: any) => HotObservable<T>;
    let cold: <T>(marbles: string, values?: any, error?: any) => ColdObservable<T>;
    beforeEach(() => {
        testScheduler = new TestScheduler(
            (actual, expected) => expect(actual).toEqual(expected),
        );
        expectObservable = testScheduler.expectObservable
            .bind(testScheduler);
        expectSubscriptions = testScheduler.expectSubscriptions
            .bind(testScheduler);
        hot = testScheduler.createHotObservable
            .bind(testScheduler);
        cold = testScheduler.createColdObservable
            .bind(testScheduler);
    });
    afterEach(() => {
        try {
            testScheduler.flush();
        } finally {
            testScheduler = new TestScheduler(
                (actual, expected) => expect(actual).toEqual(expected),
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
            .toBe('(012|)', [1, 2, 3]);
    });

    /**
     * Emits a value from the source Observable only after a particular time span
     * has passed without another source emission.
     */
    it('debounceTime', () => {
        const ob = cold('--a--bc--d|')
            .debounceTime(10, testScheduler);
        const expected = '---0---1--(2|)';
        expectObservable(ob)
            .toBe(expected, ['a', 'c', 'd']);
    });

    /**
     * Emits a value from the source Observable
     * then ignores subsequent source values for duration milliseconds
     * then repeats this process.
     */
    it('throttleTime', () => {
        const ob = cold('--ab-cd-e-f-g|')
            .throttleTime(10, testScheduler);
        const expected = '--0--1--2-3-4|';
        expectObservable(ob)
            .toBe(expected, ['a', 'c', 'e', 'f', 'g']);
    });
});
