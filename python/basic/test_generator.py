import unittest


class GeneratorTestCase(unittest.TestCase):

    def testBasicUsage(self):
        def generator():
            i = 0
            while True:
                i += 1
                yield i

        j = 0
        for item in generator():
            j = item
            if j > 4:
                break

        self.assertEqual(j, 5)
