// https://leanpub.com/understandinges6/read/#leanpub-auto-proxies-and-the-reflection-api
import test from 'ava';
import chai from 'chai';

chai.should();

test('basic usage', t => {
    const target = {};
    const proxy = new Proxy(target, {});
    proxy.name = 'proxy';
    target.name.should.equal('proxy')
    target.name = "target";
    proxy.name.should.equal('target')
});

test('proxy set behavior', t => {
    // ex: property validation
    const counter = { count: 0 };
    const proxy = new Proxy(counter, {
        set(target, key, value, receiver) {
            target.should.equal(counter);
            if(key === 'count' && isNaN(value)) throw new Error('should be number');
            return Reflect.set(target, key, value, receiver);
        }
    });

    proxy.whatever = 'whatever';
    counter.whatever.should.equal('whatever');

    proxy.count = 1
    counter.count.should.equal(1);

    // manipulate proxy
    t.throws(() => proxy.count = 'foo', 'should be number');

    // manipulate origin object
    counter.count = 'foo'
    counter.count.should.equal('foo');
});

test('proxy get behavior', t => {
    // ex: object shape validation
    const proxy = new Proxy({}, {
        get(target, key, receiver) {
            if(!(key in receiver))
                throw new Error(`property ${key} does not exist`)
            return Reflect.get(target, key, receiver);
        }
    });
    proxy.foo = 'foo'
    proxy.foo.should.equal('foo');
    t.throws(() => proxy.bar, 'property bar does not exist')
});

test('proxy has behavior', t => {
    // ex: hide property
    const proxy = new Proxy({ 
        foo: 'foo',
        bar: 'bar'
    }, {
        has(target, key) {
            return key === 'bar' ? false : Reflect.has(target, key);
        }
    });
    ('foo' in proxy).should.be.true;
    ('bar' in proxy).should.be.false;
});

test('proxy apply behavior', t => {
    const sumProxy = new Proxy(
        (...args) => args.reduce((acc, curr) => acc + curr, 0),
        {
            apply(target, thisArg, args) {
                args.forEach(arg => {
                    if(typeof arg !== 'number')
                        throw new Error('argument must be number');
                });
                return Reflect.apply(target, thisArg, args);
            }
        }
    );
    sumProxy(1, 2).should.equal(3);
    t.throws(() => sumProxy(1, '2'), 'argument must be number');
});

test('proxy construct behavior', t => {
    function Foo() {}
    const NonInstantiable = new Proxy(Foo, {
        construct(target, args) {
            throw new Error('can not be instantiate');
        }
    });

    const foo = new Foo();
    (foo instanceof Foo).should.true;
    t.throws(() => new NonInstantiable(), 'can not be instantiate');
    // test new.target in function implementation is preferable,
    // But sometimes you aren’t in control of the function
    // whose behavior needs to be modified. In that case
    // using a proxy makes sense.
    function Bar() {
        if(new.target) {
            throw new Error('can not be instantiate');
        }
    }
    t.throws(() => new Bar(), 'can not be instantiate');
});

test('revocable', t => {
    // proxy - the proxy object that can be revoked
    // revoke - the function to call to revoke the proxy
    const { proxy, revoke } = Proxy.revocable({ foo: 'foo' }, {});
    proxy.foo.should.equal('foo');
    revoke();
    t.throws(() => proxy.foo);
});

// Only the get, set, and has proxy traps will ever be
// called on a proxy when it’s used as a prototype
test('using a proxy as a prototype', t => {
    let target = {};
    let thing = Object.create(new Proxy(target, {
        get(target, key, receiver) {
            // whenever subClass access properties which
            // they dosn't own, throw an error
            throw new Error(`${key} does not exist`);
        }
    }));

    thing.name = 'thing';
    thing.name.should.equal('thing');
    t.throws(() => thing.unknown, 'unknown does not exist')
});
