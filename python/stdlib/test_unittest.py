import unittest


# https://docs.python.org/3.5/library/unittest.html#unittest.TestCase.assertEqual
class UnitTestTestCase(unittest.TestCase):
    def testAssertEqual(self):
        self.assertEqual(1, 1)
        self.assertEqual(1, True)
        self.assertNotEqual(1, 2)

    def testAssertIs(self):
        self.assertIs(1, 1)
        self.assertIsNot(1, True)

    def testAssertTrue(self):
        self.assertTrue(True)
        self.assertTrue(1)
        self.assertFalse(False)
        self.assertFalse(0)

    def testAssertIsNone(self):
        self.assertIsNone(None)
        self.assertIsNotNone(0)

    def testAssertRaises100(self):
        def fn(): raise Exception

        self.assertRaises(Exception, fn)

    def testAssertRaise200(self):
        with self.assertRaises(Exception) as context_manager:
            raise Exception
        self.assertIsNotNone(context_manager.exception)
