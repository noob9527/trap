import { Observable } from 'rxjs/Observable';
import { AsyncSubject, BehaviorSubject, ObjectUnsubscribedError, ReplaySubject, Subject } from 'rxjs/Rx';
import * as sinon from 'sinon';

describe('Subject', () => {
    it('multicast to many Observers', () => {
        const spy1 = sinon.spy();
        const spy2 = sinon.spy();
        const subject = new Subject();
        subject.subscribe(spy1);
        subject.subscribe(spy2);
        subject.next(1);
        subject.next(2);
        expect(spy1.getCalls().map(e => e.args[0]))
            .toEqual([1, 2]);
        expect(spy2.getCalls().map(e => e.args[0]))
            .toEqual([1, 2]);
    });

    it('Subject is an Observable, and also is an Observer', () => {
        const spy1 = sinon.spy();
        const spy2 = sinon.spy();
        const subject = new Subject();
        subject.subscribe(spy1);
        subject.subscribe(spy2);
        const observable = Observable.create((observer: any) => {
            observer.next(1);
            observer.next(2);
        });
        observable.subscribe(subject);
        expect(spy1.getCalls().map(e => e.args[0]))
            .toEqual([1, 2]);
        expect(spy2.getCalls().map(e => e.args[0]))
            .toEqual([1, 2]);
    });

    it('BehaviorSubject', () => {
        // behaviorSubject 就像只缓存一个值的 replaySubject
        const spy1 = sinon.spy();
        const spy2 = sinon.spy();
        const subject1 = new BehaviorSubject(0);
        subject1.subscribe(spy1);
        subject1.next(1);
        subject1.subscribe(spy2);
        // spy1 invoke twice
        expect(spy1.getCall(0).calledWith(0)).toBeTruthy();
        expect(spy1.getCall(1).calledWith(1)).toBeTruthy();
        // spy2 invoke once with last value
        expect(spy2.getCall(0).calledWith(1)).toBeTruthy();

        // normal subject
        const spy3 = sinon.spy();
        const spy4 = sinon.spy();
        const subject2 = new Subject();
        subject2.subscribe(spy3);
        subject2.next(1);
        subject2.subscribe(spy4);
        expect(spy3.calledOnce).toBeTruthy();
        expect(spy3.getCall(0).calledWith(1)).toBeTruthy();
        expect(spy4.notCalled).toBeTruthy();
    });

    describe('ReplaySubject', () => {
        it('cache value', () => {
            const spy1 = sinon.spy();
            const spy2 = sinon.spy();
            const subject = new ReplaySubject(2); // cache 2 value
            subject.subscribe(spy1);
            subject.next(0);
            subject.next(1);
            subject.next(2);
            expect(spy1.getCalls().map(e => e.args[0]))
                .toEqual([0, 1, 2]);
            subject.subscribe(spy2);
            expect(spy2.getCalls().map(e => e.args[0]))
                .toEqual([1, 2]);
        });

        it('cache value in windowTime', () => {
            const spy = sinon.spy();
            const subject = new ReplaySubject(100, 10); // cache value in 10ms
            setInterval(() => {
                subject.next(0);
            }, 1);
            return new Promise((resolve, reject) => {
                setTimeout(() => {
                    subject.subscribe(spy);
                    spy.callCount > 10 ? reject() : resolve();
                }, 100);
            });
        });
    });

    it('AsyncSubject', () => {
        const spy = sinon.spy();
        const subject = new AsyncSubject();
        subject.subscribe(spy);
        subject.next(0);
        subject.next(0);
        subject.next(1);
        subject.complete();
        // invoke as soon as complete, using last value
        expect(spy.calledOnce).toBeTruthy();
        expect(spy.firstCall.calledWith(1)).toBeTruthy();
    });

    it('invoke next after subject.unsubscribe，should throw ObjectUnsubscribedError', () => {
        const subject = new Subject();
        subject.unsubscribe();
        expect(() => subject.next(0)).toThrow(ObjectUnsubscribedError);
    });

    it('multicast', () => {
        const spy1 = sinon.spy();
        const spy2 = sinon.spy();
        const subject = new Subject();
        const multicasted = Observable
            .create((observer: any) => {
                observer.next(1);
                observer.next(2);
            })
            .multicast(subject);
        multicasted.subscribe(spy1);
        multicasted.subscribe(spy2);
        multicasted.connect();
        expect(spy1.getCalls().map(e => e.args[0]))
            .toEqual([1, 2]);
        expect(spy2.getCalls().map(e => e.args[0]))
            .toEqual([1, 2]);
    });

    it('refCount(auto connect/disconnect)', () => {
        const spy1 = sinon.spy();
        const spy2 = sinon.spy();
        const refCounted = Observable
            .create((observer: any) => {
                spy1();
                return spy2;
            })
            .multicast(new Subject())
            .refCount();
        expect(spy1.calledOnce).toBeFalsy();
        const subscription = refCounted.subscribe();
        expect(spy1.calledOnce).toBeTruthy();
        expect(spy2.called).toBeFalsy();
        subscription.unsubscribe();
        expect(spy2.called).toBeTruthy();
    });
});