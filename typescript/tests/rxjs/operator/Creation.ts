import { ColdObservable } from 'rxjs/testing/ColdObservable';
import { HotObservable } from 'rxjs/testing/HotObservable';
import { SubscriptionLog } from 'rxjs/testing/SubscriptionLog';
import { observableToBeFn, subscriptionLogsToBeFn } from 'rxjs/testing/TestScheduler';
import 'rxjs';

import { EventEmitter } from 'events';
import { Observable } from 'rxjs/Observable';
import { TestScheduler } from 'rxjs/Rx';
import * as sinon from 'sinon';

describe('creation', () => {
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

    it('empty', () => {
        expectObservable(Observable.empty())
            .toBe('|')
    });

    it('throw', () => {
        expectObservable(Observable.throw('error'))
            .toBe('#')
    });

    it('create', done => {
        const spy = sinon.spy();
        const observable = Observable.create((observer: any) => {
            observer.next(1);
            observer.next(2);
            observer.next(3);
            setTimeout(() => {
                observer.next(4);
                observer.complete();
            });
        });
        observable.subscribe(spy, fail, () => {
            expect(spy.callCount).toBe(4);
            expect(spy.getCalls().map(e => e.args[0]))
                .toEqual([1, 2, 3, 4]);
            done();
        });
    });

    it('of', () => {
        expectObservable(Observable.of(1, 2, 3))
            .toBe('(abc|)', { a: 1, b: 2, c: 3 })
    });

    it('from', () => {
        expectObservable(Observable.from([1, 2, 3]))
            .toBe('(abc|)', { a: 1, b: 2, c: 3 })
    });

    it('fromEvent', done => {
        const emitter = new EventEmitter();
        Observable
            .fromEvent(emitter, 'foo')
            .subscribe(() => {
                done();
            });
        emitter.emit('foo');
    });

    it('fromPromise resolve', done => {
        const spy = sinon.spy();
        Observable
            .fromPromise(Promise.resolve(0))
            .subscribe(spy, fail, () => {
                expect(spy.calledOnce).toBeTruthy();
                expect(spy.calledWith(0)).toBeTruthy();
                done();
            });
    });

    it('fromPromise reject', done => {
        Observable
            .fromPromise(Promise.reject(0))
            .subscribe(fail, err => {
                expect(err).toBe(0);
                done();
            }, fail);
    });

    it('interval', () => {
        const observable = Observable
            .interval(20, testScheduler)
            .take(3);
        expectObservable(observable)
            .toBe('--0-1-(2|)', [0, 1, 2]);
    });
});
