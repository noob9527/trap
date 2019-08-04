import unittest

import numpy as np


# Broadcasting consists of two steps:
# 1. Axes (called broadcast axes) are added to the smaller tensor to match the ndim of the larger tensor.
# 2. The smaller tensor is repeated alongside these new axes to match the full shape of the larger tensor.
class BroadcastTestCase(unittest.TestCase):
    def test_broadcast1(self):
        arr = np.array([1, 2, 3])
        res = arr + 1
        self.assertTrue(np.array_equal(res, [2, 3, 4]))

    def test_broadcast2(self):
        matrix = np.array([[1, 2], [3, 4]])
        res = matrix + [100, 200]
        self.assertTrue(np.array_equal(res, np.array([[101, 202], [103, 204]])))
