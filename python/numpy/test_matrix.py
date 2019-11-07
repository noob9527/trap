import unittest

import numpy as np


class ArrayTestCase(unittest.TestCase):
    def test_ndenumerate(self):
        # matrix
        # https://docs.scipy.org/doc/numpy/reference/generated/numpy.ndenumerate.html
        matrix = np.array([[1, 2], [3, 4]])
        res = {}
        for index, x in np.ndenumerate(matrix):
            res[index] = x
        self.assertEqual(res, {
            (0, 0): 1,
            (0, 1): 2,
            (1, 0): 3,
            (1, 1): 4
        })
