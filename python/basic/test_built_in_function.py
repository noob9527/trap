import unittest


class BuiltInFunctionTestCase(unittest.TestCase):
    # like the typeof keyword in js
    # python use type function to test the type of a variable
    def testType(self):
        res = type(1)
        self.assertEqual(str(res), "<class 'int'>")

    # The isinstance() function checks if the object (first argument)
    # is an instance or subclass of classinfo class (second argument)
    def testIsInstance(self):
        class Test:
            a = 5

        test = Test()
        self.assertTrue(isinstance(test, Test))
