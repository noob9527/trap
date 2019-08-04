import unittest


class StringTestCase(unittest.TestCase):
    def testStringAutoConcat(self):
        self.assertEqual('foo''bar', 'foobar')

    def testStringFormat(self):
        self.assertEqual('foo%s' % 'bar', 'foobar')
        dic = {'foo': 'f', 'bar': 'b'}
        self.assertEqual('foo b', 'foo %(bar)s' % dic)

    def testNewFormat(self):
        foo = 'f'
        bar = 'b'
        self.assertEqual(f'{foo} {bar}', 'f b')
