import unittest

import numpy as np
from keras.datasets import mnist


class TensorTestCase(unittest.TestCase):
    def test_slice(self):
        matrix = np.array([[1, 2, 3]])
        res = matrix[:, :]
        self.assertTrue(np.array_equal(res, matrix))
