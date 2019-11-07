import unittest

import numpy as np


class NumpyTest(unittest.TestCase):
    def test_zero_matrix(self):
        matrix = np.zeros((3, 2))
        self.assertEqual(matrix.shape, (3, 2))

    def test_random_matrix(self):
        matrix = np.random.rand(2, 3)
        self.assertEqual(matrix.shape, (2, 3))

    def test_map(self):
        matrix = np.array([[-1, -1], [1, 1]])
        res = matrix > 0
        self.assertTrue(np.array_equal(res, np.array([[False, False], [True, True]])))

    # trim zeros
    def test_trim_zeros(self):
        arr = np.array([0, 1, 2, 3, 0, 0, 0])
        res = np.trim_zeros(arr)
        self.assertTrue(np.array_equal(res, np.array([1, 2, 3])))

    # slice
    def test_slice(self):
        arr = np.array([1, 2, 3, -1, 0, 0])
        res = arr[:3]
        self.assertTrue(np.array_equal(res, np.array([1, 2, 3])))

    # https://stackoverflow.com/questions/38191855/zero-pad-numpy-array/38192105
    def test_pad_array(self):
        arr = np.array([1, 1, 1])
        # pad 2 elements on the left side and 3 elements on the right side
        res = np.pad(arr, (2, 3), 'constant')
        print(res)
        self.assertTrue(np.array_equal(res, np.array([0, 0, 1, 1, 1, 0, 0, 0])))

    # It's also possible to pad a 2D numpy arrays by passing a tuple of tuples as padding width,
    # which takes the format of ((top, bottom), (left, right)):
    def test_pad_matrix(self):
        arr = np.array([[1, 2], [3, 4]])
        res = np.pad(arr, ((1, 2), (2, 1)), 'constant')
        self.assertTrue(np.array_equal(res, np.array(
            [[0, 0, 0, 0, 0],  # 1 zero padded to the top
             [0, 0, 1, 2, 0],  # 2 zeros padded to the bottom
             [0, 0, 3, 4, 0],  # 2 zeros padded to the left
             [0, 0, 0, 0, 0],  # 1 zero padded to the right
             [0, 0, 0, 0, 0]]
        )))

    # Returns the indices that would sort an array.
    def test_arg_sort(self):
        arr = np.array([3, 1, 2])
        res = np.argsort(arr)
        self.assertTrue(np.array_equal(res, np.array([1, 2, 0])))
