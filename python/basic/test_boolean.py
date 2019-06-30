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

    # By default, an object is considered true unless its class defines
    # either a __bool__() method that returns False
    # or a __len__() method that returns zero, when called with the object.
    # Here are most of the built-in objects considered false:
    # - constants defined to be false: None and False.
    # - zero of any numeric type: 0, 0.0, 0j, Decimal(0), Fraction(0, 1)
    # - empty sequences and collections: '', (), [], {}, set(), range(0)
    def testSixFalsyValue(self):
        for ele in [None, 0, '', (), [], {}]:
            self.assertFalse(ele)
