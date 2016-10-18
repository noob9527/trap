import test from 'ava';
import chai from 'chai';

const should = chai.should();
Reflect.defineProperty(Object.prototype, 'log', {
    get: function () {
        console.log(this);
    }
});

test('singleton', t=>{
    class Singleton{
        static instance = null;
        constructor(){
            if(Singleton.instance) return Singleton.instance;
            return Singleton.instance = this;
        }
    }
    const foo = new Singleton();
    const bar = new Singleton();
    foo.should.equal(bar);
});
