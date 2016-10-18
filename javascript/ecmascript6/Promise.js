import test from 'ava';
import chai from 'chai';

const should = chai.should();
Reflect.defineProperty(Object.prototype, 'log', {
    get: function () {
        console.log(this);
    }
});

test.cb('promise的执行顺序', t => {
    let count = 0;
    const promise = new Promise(function (resolve, reject) {
        (count++).should.equal(0); //最先执行
        resolve();
    });

    promise.then(() => {
        (count++).should.equal(2) //最后执行
        t.end();
    });

    (count++).should.equal(1);
});

//Promise.resolve, Promise.reject
test('使用Promise静态方法将值包装成promise', async t => {
    const p1 = Promise.resolve('foo');
    const p2 = Promise.reject('bar');
    await p1.then(v => v.should.equal('foo'));
    await p2.catch(v => v.should.equal('bar'));
});

test('不使用任何参数调用Promise.resolve, Promise.reject，传给回调函数的参数为undefined', async t=>{
    const p1 = Promise.resolve();
    const p2 = Promise.reject();
    await p1.then(v=>should.not.exist(v));
    await p2.catch(v=>should.not.exist(v));
});

test('Promise.resolve对promise对象返回其本身，对有then方法的对象会执行其then方法，并等待其resolve', async t=>{
    const thenable = {
        then(resolve,reject){
            resolve('foo');
        }
    }
    const p1 = Promise.resolve();
    Promise.resolve(p1).should.equal(p1);

    const p2 = Promise.resolve(thenable);
    await p2.then(v=>v.should.equal('foo'));
});

test('如果promise已经resolve，依然可以调用then方法获得返回值', async t => {
    const p1 = Promise.resolve('foo');
    const p2 = Promise.reject('bar');
    await p1.then(v => v.should.equal('foo'));
    await p1.then(v => v.should.equal('foo'));
    await p2.catch(v => v.should.equal('bar'));
    await p2.catch(v => v.should.equal('bar'));
});

//Promise.all与Promise.race
test('Promise.all与Promise.race会自动调用Promise.resolve包装数组中不是promise的值', async t => {
    const arr = [0, Promise.resolve(1)];
    await Promise.all(arr).then(v => v.should.eql([0, 1]));
    await Promise.race(arr).then(v => v.should.equal(0));
});

