import unittest

import numpy as np


class ArrayTestCase(unittest.TestCase):
    def test_array_matrix(self):
        arr = np.array([1, 2, 3])
        matrix = np.array([[1, 2, 3]])
        self.assertEqual(arr.shape, (3,))
        self.assertEqual(matrix.shape, (1, 3))
