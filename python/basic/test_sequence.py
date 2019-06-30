import unittest


class SequenceTestCase(unittest.TestCase):
    def testNegativeIndex(self):
        self.assertEqual([1, 2, 3][-1], 3)

    def testRange(self):
        self.assertEqual([x for x in range(0, 3)], [0, 1, 2])
        self.assertEqual([x for x in range(3)], [0, 1, 2])

    def testListComprehension(self):
        self.assertEqual([x + 1 for x in range(3)], [1, 2, 3])
        self.assertEqual([x + 1 for x in range(3) if x > 0], [2, 3])
        tuples = [(x, y) for x in range(2) for y in range(2)]
        self.assertEqual(tuples, [(0, 0), (0, 1), (1, 0), (1, 1)])

    # immutable
    def testSlice1(self):
        arr = [1, 2, 3][1:2]
        self.assertEqual(arr, [2])

    def testSlice2(self):
        arr = [1, 2, 3][1:0]
        self.assertEqual(arr, [])

    def testSlice3(self):
        arr = [1, 2, 3][1:-1]
        self.assertEqual(arr, [2])

    def testSlice5(self):
        arr = [1, 2, 3]
        self.assertEqual(arr[1:], [2, 3])
        self.assertEqual(arr[:-1], [1, 2])
        self.assertEqual(arr[:], [1, 2, 3])

    def testSliceShouldNotMutateOriginalArray(self):
        original = [1, 2, 3]
        target = [1, 2, 3][0:-1]
        self.assertEqual(original, [1, 2, 3])
        self.assertEqual(target, [1, 2])

    def testSliceWithStep(self):
        arr = [1, 2, 3, 4, 5]
        self.assertEqual(arr[::2], [1, 3, 5])
        self.assertEqual(arr[::-2], [5, 3, 1])

    def testConcat(self):
        self.assertEqual([1, 2, 3] + [4, 5, 6], [1, 2, 3, 4, 5, 6])

    def testMultiply(self):
        self.assertEqual('foo' * 3, 'foofoofoo')
        self.assertEqual([1, 2] * 3, [1, 2, 1, 2, 1, 2])

    def testCheckIfExist(self):
        arr = [1, 2, 3]
        self.assertTrue(1 in arr)
        self.assertFalse(4 in arr)

    def testLengthMinMaxEtc(self):
        arr = [1, 2, 3]
        self.assertEqual(len(arr), 3)
        self.assertEqual(max(arr), 3)
        self.assertEqual(min(arr), 1)
        self.assertEqual(max(1, 2, 3), 3)
        self.assertEqual(min(1, 2, 3), 1)

    def testCount(self):
        arr = [1, 1, 1, [1, 2], [1, 2]]
        self.assertEqual(arr.count(1), 3)
        self.assertEqual(arr.count([1, 2]), 2)

    def testIndex(self):
        self.assertEqual([1, 2, 3].index(2), 1)
        # 查找不存在的值会引发异常
        with self.assertRaises(ValueError):
            [1, 2, 3].index(4)

    # mutable
    def testSliceAssignment(self):
        arr = [1, 5]
        arr[1:] = [2, 3, 4, 5]
        self.assertEqual(arr, [1, 2, 3, 4, 5])
        arr[1:-1] = []
        self.assertEqual(arr, [1, 5])
        arr = [1, 2, 3, 4, 5]
        arr[::2] = [-1, -3, -5]
        self.assertEqual(arr, [-1, 2, -3, 4, -5])

    def testAppend(self):
        # similar to push
        origin = [1, 2, 3]
        res = origin.append(4)
        self.assertIsNone(res)
        self.assertEqual(origin, [1, 2, 3, 4])

    def testExtend(self):
        origin1 = [1, 2, 3]
        origin2 = origin1[:]
        origin1.extend([4, 5])
        origin2[len(origin2):] = [4, 5]
        self.assertEqual(origin1, [1, 2, 3, 4, 5])
        self.assertEqual(origin2, [1, 2, 3, 4, 5])

    def testMap(self):
        origin = [1, 2]
        res = list(map(lambda e: e * 2, origin))
        self.assertEqual(res, [2, 4])
