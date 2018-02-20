#!/usr/bin/env python3
import unittest
from argparse import ArgumentParser


# https://docs.python.org/3.5/library/argparse.html#argparse.ArgumentParser
class ArgParseTestCase(unittest.TestCase):
    def testPositional(self):
        parser = ArgumentParser()
        parser.add_argument('foo')
        res = parser.parse_args(['FOO'])
        self.assertEqual(res.foo, 'FOO')

    def testOptional(self):
        parser = ArgumentParser()
        parser.add_argument('-f', '--foo')
        self.assertEqual(
            parser.parse_args(['-f', 'FOO']).foo,
            'FOO'
        )
        self.assertEqual(
            parser.parse_args(['--foo', 'FOO']).foo,
            'FOO'
        )

    def testNargs(self):
        # nargs - The number of command-line arguments that should be consumed.
        parser = ArgumentParser()
        parser.add_argument('foo', nargs=2)
        parser.add_argument('bar', nargs=1)
        res = parser.parse_args('A B C'.split())
        self.assertEqual(res.foo, ['A', 'B'])
        self.assertEqual(res.bar, ['C'])

    def testDest(self):
        # dest - The name of the attribute to be added to the object returned by parse_args()
        parser = ArgumentParser()
        parser.add_argument('--foo', dest='bar')
        res = parser.parse_args('--foo A'.split())
        self.assertEqual(res.bar, 'A')

    def testDefault(self):
        # default - The value produced if the argument is absent from the command line.
        parser = ArgumentParser()
        parser.add_argument('-foo')
        parser.add_argument('-bar', default='BAR')
        res = parser.parse_args()
        self.assertIsNone(res.foo)
        self.assertEqual(res.bar, 'BAR')
