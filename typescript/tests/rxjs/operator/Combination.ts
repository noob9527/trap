import 'rxjs';

import { Observable } from 'rxjs/Observable';
import { TestScheduler } from 'rxjs/Rx';
import { ColdObservable } from 'rxjs/testing/ColdObservable';
import { HotObservable } from 'rxjs/testing/HotObservable';
import { SubscriptionLog } from 'rxjs/testing/SubscriptionLog';
import { observableToBeFn, subscriptionLogsToBeFn } from 'rxjs/testing/TestScheduler';

describe('combination', () => {
    let testScheduler: TestScheduler;
    let expectObservable: (observable: Observable<any>, unsubscriptionMarbles?: string) => ({
        toBe: observableToBeFn;
    });
    let expectSubscriptions: (actualSubscriptionLogs: SubscriptionLog[]) => ({
        toBe: subscriptionLogsToBeFn;
    });
    let hot: <T>(marbles: string, values?: any, error?: any) => HotObservable<T>;
    let cold: <T>(marbles: string, values?: any, error?: any) => ColdObservable<T>;

    beforeEach(() => {
        testScheduler = new TestScheduler(
            (actual, expected) => expect(actual).toEqual(expected)
        );
        expectObservable = testScheduler.expectObservable
            .bind(testScheduler)
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
                (actual, expected) => expect(actual).toEqual(expected)
            );
        }
    });

    /**
     * Returns an Observable that mirrors the first source Observable
     * to emit an item from the combination of 
     * this Observable and supplied Observables.
     * @see Promise.race
     */
    it('race', () => {
        const m1 = '-a-b-c-|';
        const m2 = '--xyz|';
        const e1 = cold(m1);
        const e1subs = '^------!';
        const e2 = cold(m2);
        const e2subs = '^!';
        const result = e1.race(e2);
        expectObservable(result).toBe(m1);
        expectSubscriptions(e1.subscriptions).toBe(e1subs);
        expectSubscriptions(e2.subscriptions).toBe(e2subs);
    });

    /**
     * join the last values of the provided observables into an array
     * @see Promise.all
     */
    it('forkjoin', () => {
        const e1 = Observable.forkJoin(
            hot('--a--b--c--d--|'),
            hot('(b|)'),
            hot('--1--2--3--|')
        );
        const expected = '--------------(x|)'
        expectObservable(e1)
            .toBe(expected, { x: ['d', 'b', '3'] })
    });

    /**
     * Combines multiple Observables to create an Observable 
     * whose values are calculated from the latest values 
     * of each of its input Observables.
     * @see Promise.all
     */
    it('combineLatest', () => {
        const e1 = cold('--a--b--c--|');
        const e2 = cold('---xy--z---|');
        const expected = '---012-34--|';
        const result = e1.combineLatest(e2);
        expectObservable(result)
            .toBe(expected, [
                ['a', 'x'],
                ['a', 'y'],
                ['b', 'y'],
                ['b', 'z'],
                ['c', 'z'],
            ]);
    });

    /**
     * Combines multiple Observables to create an Observable
     * whose values are calculated from the values
     * in order of each of its input Observables.
     * should end once one observable completes and its buffer is empty
     */
    it('zip', () => {
        const e1 = hot('-a-b-c-|');
        const e2 = hot('--d--e--f--g--h--');
        const expected = '--x--y--(z|)';    // e1 complete and buffer empty
        const result = e1.zip(e2);
        expectObservable(result)
            .toBe(expected, {
                x: ['a', 'd'],
                y: ['b', 'e'],
                z: ['c', 'f'],
            });
    });

});