import unittest


class FunctionTestCase(unittest.TestCase):
    def testNamingParameter(self):
        def fn(foo, bar): return foo, bar

        res = fn(bar="bar", foo="foo")
        self.assertEqual(res, ("foo", "bar"))

    def testDefaultParameter(self):
        def fn(foo="foo", bar="bar"): return foo, bar

        res = fn()
        self.assertEqual(res, ("foo", "bar"))

    def testRestParameter1(self):
        def fn(*rest): return rest

        res = fn(1, 2, 3)
        self.assertEqual(res, (1, 2, 3))

    def testRestParameter2(self):
        def fn(**rest): return rest

        res = fn(foo="foo", bar="bar")
        self.assertEqual(res, {'foo': 'foo', 'bar': 'bar'})
