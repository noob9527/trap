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

    # 静态方法可以被类直接调用，没有self参数
    def testStaticMethod(self):
        class Foo:
            @staticmethod
            def foo(): return 'foo'

        self.assertIsNotNone(Foo.foo())

    # 类方法也可以被类直接调用，同时会自动绑定cls参数
    def testClassMethod(self):
        class Foo:
            @classmethod
            def foo(cls): return cls

        self.assertIsNotNone(Foo.foo())
