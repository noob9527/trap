import { SubscriptionLog } from 'rxjs/testing/SubscriptionLog';
import { observableToBeFn, subscriptionLogsToBeFn } from 'rxjs/testing/TestScheduler';
import { ColdObservable } from 'rxjs/testing/ColdObservable';
import { HotObservable } from 'rxjs/testing/HotObservable';
import { TestScheduler } from 'rxjs/Rx';
import { Observable } from 'rxjs/Observable';
import * as sinon from 'sinon';

describe('Observable', () => {
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
     * COLD is when your observable creates the producer
     * HOT is when your observable closes over the producer
     * // COLD(unicast)
     * const cold = new Observable((observer) => {
     *     const producer = new Producer();
     *     // have observer listen to producer here
     * });
     * // HOT(multicast)
     * const producer = new Producer();
     * const hot = new Observable((observer) => {
     *     // have observer listen to producer here
     * });
     * @see https://medium.com/@benlesh/hot-vs-cold-observables-f8094ed53339
     */
    it('Hot and Cold Observables', () => {
        const ob1 = hot('--^--|');
        const ob1subs = '^--!';
        const ob2 = cold('-----|');
        const ob2subs = '^----!';
        ob1.subscribe();
        ob2.subscribe();
        expectSubscriptions(ob1.subscriptions)
            .toBe(ob1subs);
        expectSubscriptions(ob2.subscriptions)
            .toBe(ob2subs);
    });

    it('Observables have no shared execution and are lazy', () => {
        // EventEmitters 共享副作用并且无论是否存在订阅者都会尽早执行
        // Observables 与之相反，不会共享副作用并且是延迟执行。
        // 订阅Observable相当于调用函数，并提供用于接收数据的回调函数
        const spy = sinon.spy();
        const observable = Observable.create((observer: any) => {
            spy();
            observer.next(0);
        });
        expect(spy.callCount).toBe(0);
        observable.subscribe();
        expect(spy.callCount).toBe(1);
        observable.subscribe();
        expect(spy.callCount).toBe(2);
    });

    it('subscription of observable was entirely synchronous, just like a function', () => {
        const spy1 = sinon.spy();
        const spy2 = sinon.spy();
        const spy3 = sinon.spy();
        const observable = Observable.create(spy2);
        spy1();
        observable.subscribe();
        spy3();
        expect(spy2.calledAfter(spy1));
        expect(spy3.calledAfter(spy2));
    });

    it('output multiple values synchronously', () => {
        const spy1 = sinon.spy();
        const spy2 = sinon.spy();
        const spy3 = sinon.spy();
        const observable = Observable.create((observer: any) => {
            observer.next(1);
            observer.next(2);
            observer.next(3);
        });
        spy1();
        observable.subscribe(spy2);
        spy3();
        expect(spy2.calledAfter(spy1));
        expect(spy3.calledAfter(spy2));
        expect(spy2.getCalls().map(e => e.args[0]))
            .toEqual([1, 2, 3]);
    });

    it('output multiple values asynchronously', done => {
        const spy1 = sinon.spy();
        const spy2 = sinon.spy();
        const spy3 = sinon.spy();
        const observable = Observable.create((observer: any) => {
            setTimeout(() => {
                observer.next(1);
                observer.next(2);
                observer.next(3);
                observer.complete();
            });
        });
        spy1();
        observable.subscribe({
            next: spy2,
            complete: () => {
                expect(spy2.calledAfter(spy1));
                expect(spy3.calledAfter(spy2));
                expect(spy2.getCalls().map(e => e.args[0]))
                    .toEqual([1, 2, 3]);
                done();
            },
        });
        spy3();
    });

    it('error|complete notification should at most call once', () => {
        const spy1 = sinon.spy();
        const spy2 = sinon.spy();
        const observable1 = Observable.create((observer: any) => {
            observer.complete();
            observer.complete();
        });
        const observable2 = Observable.create((observer: any) => {
            observer.error();
            observer.error();
        });
        observable1.subscribe({
            complete: spy1,
        });
        observable2.subscribe({
            error: spy2,
        });
        expect(spy1.calledOnce).toBeTruthy();
        expect(spy2.calledOnce).toBeTruthy();
    });

    it('should not invoke next after error|complete notification', () => {
        const spy = sinon.spy();
        const observable1 = Observable.create((observer: any) => {
            observer.complete();
            observer.next();
        });
        const observable2 = Observable.create((observer: any) => {
            observer.error();
            observer.next();
        });
        observable1.subscribe({
            next: spy,
        });
        observable2.subscribe({
            error: () => { },
            next: spy,
        });
        expect(spy.notCalled).toBeTruthy();
    });

    it('disposing Observable executions', () => {
        const spy = sinon.spy();
        const observable = Observable.create((observer: any) => {
            const id = setTimeout(() => observer.next(0));
            return function unsubscribe() {
                clearTimeout(id);
            };
        });
        const subscription = observable.subscribe(spy);
        subscription.unsubscribe();
        return new Promise((resolve, reject) => {
            setTimeout(() => {
                if (spy.notCalled) resolve();
                else reject();
            }, 10);
        });
    });

    it('simple Observable implementation', done => {
        type UnsubscribeFn = () => void;
        type SubscribeFn = (observer: Observer) => UnsubscribeFn | void;
        interface Observer {
            next?: (x: any) => void;
            error?: (x: any) => void;
            complete?: () => void;
        }
        class SimpleObservable {
            static create(subscribeFn: SubscribeFn) {
                return new SimpleObservable(subscribeFn);
            }
            constructor(public subscribe: SubscribeFn) { }
        }
        const spy = sinon.spy();
        const observable = SimpleObservable.create((observer: Observer) => {
            observer.next(0);
            setTimeout(() => {
                observer.next(1);
                observer.complete();
            });
        });
        observable.subscribe({
            next: spy,
            complete: () => {
                expect(spy.getCalls().map(e => e.args[0]))
                    .toEqual([0, 1]);
                done();
            },
        });
    });
});
