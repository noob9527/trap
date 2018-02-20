import unittest


class BooleanTestCase(unittest.TestCase):

    def testBooleanValue(self):
        self.assertEqual(True, 1)
        self.assertEqual(False, 0)
        self.assertEqual(10 + True + False, 11)

    def testBoolOperator(self):
        # short circuit
        self.assertEqual(False or 'foo', 'foo')
        self.assertEqual('foo' or True, 'foo')
        self.assertEqual(True and 'bar', 'bar')
        self.assertEqual('' and True, '')

    def testTernaryOperator(self):
        foo = 'foo' if True else 'bar'
        bar = 'foo' if False else 'bar'
        self.assertEqual(foo, 'foo')
        self.assertEqual(bar, 'bar')

    # def testSixFalsyValue(self):
    #     [None, 0, '', (), [], {}]


