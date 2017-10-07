import unittest


class ExceptionTestCase(unittest.TestCase):
    def testRaiseWithoutParameter(self):
        with self.assertRaises(AssertionError):
            try:
                assert 1 == 0
            except AssertionError:
                raise

    def testCatchExceptionObject(self):
        try:
            assert 1 == 0
        except AssertionError as e:
            self.assertIsNotNone(e)

    def testTryWithElseFinally(self):
        def fn():
            res = []
            try:
                pass
            except Exception:
                pass
            else:
                res.append(1)
                return res
            finally:
                res.append(2)
                return res
        self.assertEqual(fn(), [1,2])

