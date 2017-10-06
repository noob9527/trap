import unittest


class SyntaxTestCase(unittest.TestCase):

    def testElseInLoop(self):
        # for/while循环可以使用else子句，它仅在没有使用break时调用
        for i in range(10):
            foo = 'bar'
        else:
            foo = 'foo'
        for i in range(10):
            bar = 'bar'
            break
        else:
            bar = 'foo'
        self.assertEqual(foo, 'foo')
        self.assertEqual(bar, 'bar')
