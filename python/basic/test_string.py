import unittest


class StringTestCase(unittest.TestCase):
    def testStringAutoConcat(self):
        self.assertEqual('foo''bar', 'foobar')

    def testStringFormat(self):
        self.assertEqual('foo%s' % 'bar', 'foobar')
        dic = {'foo': 'foo', 'bar': 'bar'}
        self.assertEqual('foo%(bar)s' % dic, 'foobar')