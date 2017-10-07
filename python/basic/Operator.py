import unittest


class OperatorTestCase(unittest.TestCase):
    def testFloorDivision(self):
        self.assertEqual(1.0 // 2.0, 0)

    def testPower(self):
        self.assertEqual(2 ** 3, 8)

    def testConcatMultiOperator(self):
        self.assertTrue(1 < 2 < 3)

    def testCompareSequance(self):
        self.assertTrue('ab' < 'ba')
        self.assertTrue([1, 2] < [2, 1])
        self.assertTrue([1, 1, [1, 2]] < [1, 1, [1, 3]])

    def testIs(self):
        self.assertTrue([] == [])
        self.assertFalse([] is [])
        self.assertTrue([] is not [])

    def testChainAssignment(self):
        foo = bar = []
        self.assertEqual(foo, [])
        self.assertIs(foo, bar)
