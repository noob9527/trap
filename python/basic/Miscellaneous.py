import unittest


class MiscellaneousTestCase(unittest.TestCase):
    def testAssert(self):
        with self.assertRaises(AssertionError):
            assert 1 == 2

    def testDel(self):
        x = 1
        del x
        with self.assertRaises(UnboundLocalError):
            print(x)

    def testDelShouldNotDeleteHeapValue(self):
        x = y = [1, 2]
        del y
        self.assertEqual(x, [1, 2])

    def testExec(self):
        scope = {}
        exec("x=2", scope)
        self.assertEqual(scope['x'], 2)

    def testEval(self):
        scope = {'x': 2}
        y = eval("x ** 2", scope)
        self.assertEqual(y, 4)

    def testVars(self):
        foo = 'foo'
        scope = vars()
        self.assertEqual(scope['foo'], 'foo')

    def testNonLocal(self):
        foo = 'foo'

        def fn1(): foo = 'bar'

        def fn2():
            nonlocal foo
            foo = 'bar'

        fn1()
        self.assertEqual(foo, 'foo')
        fn2()
        self.assertEqual(foo, 'bar')
