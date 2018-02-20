import unittest


class UnpackingTestCase(unittest.TestCase):
    def testUnpackAssignment(self):
        x, y, z = 1, 2, 3
        self.assertEqual([x, y, z], [1, 2, 3])

    def testSwitch(self):
        x, y = 1, 2
        x, y = y, x
        self.assertEqual([x, y], [2, 1])

    def testRest(self):
        *bar, y = x, *foo = 1, 2, 3, 4
        self.assertEqual(x, 1)
        self.assertEqual(foo, [2, 3, 4])
        self.assertEqual(bar, [1, 2, 3])

    def testSpread1(self):
        arr = [1, *[2, 3, 4]]
        self.assertEqual(arr, [1, 2, 3, 4])

    def testSpread2(self):
        res = {'foo': 1, **{'bar': 2, 'baz': 3}}
        self.assertEqual(res, {'foo': 1, 'bar': 2, 'baz': 3})
