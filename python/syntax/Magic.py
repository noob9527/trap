import unittest


class MagicTestCase(unittest.TestCase):
    def testIter(self):
        class Foo:
            def __init__(self):
                self.count = 0

            def __iter__(self):
                return self

            def __next__(self):
                if self.count == 5:
                    raise StopIteration
                x = self.count
                self.count += 1
                return x

        self.assertEqual(list(Foo()), [0, 1, 2, 3, 4])
        self.assertEqual([*Foo()], [0, 1, 2, 3, 4])
