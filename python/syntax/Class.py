import unittest


class ClassTestCase(unittest.TestCase):
    def testIsSubClass(self):
        class Foo: pass

        class Bar(Foo): pass

        self.assertTrue(issubclass(Bar, Foo))
        self.assertFalse(issubclass(Foo, Bar))

    def testIsInstance(self):
        class Foo: pass

        class Bar(Foo): pass

        bar = Bar()
        self.assertTrue(isinstance(bar, Bar))
        self.assertTrue(isinstance(bar, Foo))

    def testMultipleInheritance(self):
        class Parent1:
            def fn(self): return 1

        class Parent2:
            def fn(self): return 2

        class Child(Parent1, Parent2): pass

        child = Child()
        self.assertTrue(issubclass(Child, Parent1))
        self.assertTrue(issubclass(Child, Parent2))
        self.assertTrue(isinstance(child, Parent1))
        self.assertTrue(isinstance(child, Parent2))
        self.assertEqual(child.fn(), 1)

    def testSuper(self):
        class Parent:
            def __init__(self):
                self.foo = 'foo'

        class Child(Parent):
            def __init__(self):
                super(Child, self).__init__()

        child = Child()
        self.assertTrue(hasattr(child, 'foo'))

    def testSuperWithoutParam(self):
        class Parent:
            def __init__(self):
                self.foo = 'foo'

        class Child(Parent):
            def __init__(self):
                super().__init__()

        child = Child()
        self.assertTrue(hasattr(child, 'foo'))

    def testProperty(self):
        class Foo:
            def __init__(self):
                self._foo = 'foo'

            def __set_foo(self, value):
                self._foo = value

            def __get_foo(self):
                return self._foo

            @property
            def read_only(self):
                return self._foo

            foo = property(__get_foo, __set_foo)

        foo = Foo()
        self.assertEqual(foo.read_only, 'foo')
        with self.assertRaises(AttributeError):
            foo.read_only = 'bar'

        self.assertEqual(foo.foo, 'foo')
        foo.foo = 'bar'
        self.assertEqual(foo.foo, 'bar')

    def testStaticMethod(self):
        class Foo:
            @staticmethod
            def foo(): return 'foo'
        self.assertIsNotNone(Foo.foo())

    def testClassMethod(self):
        class Foo:
            @classmethod
            def foo(cls): return cls
        self.assertIsNotNone(Foo.foo())
