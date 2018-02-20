import unittest


class PropertyTestCase(unittest.TestCase):
    # python 不支持私有特性
    # 使用双下划线可以使特性在外部“不可见”
    # 但还是可以通过_ClassName__methodName来访问特性
    # 也可以使用单下划线标识内部属性，
    # 前面带有下划线的名字不会被带星号的import语句(from module import *)导入
    def testPrivateAttribute100(self):
        # noinspection PyMethodMayBeStatic
        class Foo:
            def foo(self): return 'foo' + self.__bar()

            def __bar(self): return 'bar'

        foo = Foo()
        self.assertEqual(foo.foo(), 'foobar')
        with self.assertRaises(AttributeError):
            # noinspection PyUnresolvedReferences,PyStatementEffect
            foo.__bar
        # noinspection PyUnresolvedReferences
        self.assertEqual(foo._Foo__bar(), 'bar')

    # 使用property函数新建属性
    def testProperty100(self):
        class Foo:
            def __init__(self):
                self._foo = "foo"

            def set_foo(self, value):
                pass

            def get_foo(self):
                return self._foo

            foo = property(get_foo, set_foo)

        foo = Foo()
        self.assertEqual(foo.foo, "foo")
        self.assertEqual(foo._foo, "foo")
        self.assertEqual(foo.get_foo(), "foo")

    # property 作为装饰器使用（新建只读属性）
    def testProperty200(self):
        class Foo:
            @property
            def get_foo(self):
                return 'foo'

        foo = Foo()
        self.assertEqual(foo.get_foo, 'foo')
        with self.assertRaises(AttributeError):
            # noinspection PyPropertyAccess
            foo.get_foo = 'bar'
