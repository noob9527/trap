import unittest

import numpy as np


class PropertyTestCase(unittest.TestCase):
    def test_shape_array(self):
        arr = np.zeros(2)
        self.assertEqual(arr.shape, (2,))
        self.assertTrue(np.array_equal(arr, [0, 0]))

    def test_shape_matrix(self):
        arr = np.zeros([2, 2])
        self.assertEqual(arr.shape, (2, 2))
        self.assertTrue(np.array_equal(arr, [[0, 0], [0, 0]]))

    def test_shape_tensor(self):
        arr = np.zeros([2, 2, 2])
        self.assertEqual(arr.shape, (2, 2, 2))
        self.assertTrue(np.array_equal(arr, [[[0, 0],
                                              [0, 0]],
                                             [[0, 0],
                                              [0, 0]]]))

    def test_dim_array(self):
        arr = np.zeros(2)
        self.assertEqual(arr.ndim, 1)

    def test_dim_matrix(self):
        arr = np.zeros([2, 2])
        self.assertEqual(arr.ndim, 2)

    def test_dim_tensor(self):
        arr = np.zeros([2, 2, 2])
        self.assertEqual(arr.ndim, 3)


if __name__ == '__main__':
    unittest.main()
