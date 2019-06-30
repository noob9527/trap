import unittest


class BuiltInTypeTestCase(unittest.TestCase):
    def testNone(self):
        self.assertTrue(None is None)

    # numeric types: int, float, complex
    # sequence types: list, tuple, range
    # text sequence types: str
    # binary sequence types: bytes, bytearray, memoryview
    # set types: set, frozenset
    # mapping types: dict
