import unittest

import numpy as np


class ArrayTestCase(unittest.TestCase):
    def test_array_matrix(self):
        # array
        arr = np.array([1, 2, 3])
        # matrix
        matrix = np.array([[1, 2, 3]])
        self.assertEqual(arr.shape, (3,))
        self.assertEqual(matrix.shape, (1, 3))

    # https://stackoverflow.com/questions/5284646/rank-items-in-an-array-using-python-numpy-without-sorting-array-twice/
    def test_rank_array1(self):
        array = np.array([4, 2, 7, 1])
        temp = array.argsort()
        ranks = np.empty_like(temp)
        ranks[temp] = np.arange(len(array))
        print(array)
        print(ranks)

    def test_rank_array2(self):
        array = np.array([4, 2, 7, 1])
        order = array.argsort()
        ranks = order.argsort()
        print(ranks)

