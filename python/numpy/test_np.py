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
