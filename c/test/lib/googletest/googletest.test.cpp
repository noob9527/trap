//
// Created by xy on 18-3-13.
//

/**
 * TEST() arguments go from general to specific.
 * The first argument is the name of the test case,
 * and the second argument is the test's name within the test case.
 * Both names must be valid C++ identifiers, and they should not contain underscore (_).
 * A test's full name consists of its containing test case and its individual name.
 * Tests from different test cases can have the same individual name.
 */

#include "gtest/gtest.h"

TEST(gtest, basicAssertions) {
    // nonfatal assertion(usually preferred)
    EXPECT_TRUE(1);
    EXPECT_FALSE(0);
    // fatal assertion
    ASSERT_TRUE(1);
    ASSERT_FALSE(0);
}

TEST(gtest, binaryComparison) {
    EXPECT_EQ(1, 1);// a == b
    EXPECT_NE(1, 2);// a != b
    EXPECT_LT(1, 2);// a < b
    EXPECT_LE(1, 1);// a <= b
    EXPECT_GT(2, 1);// a > b
    EXPECT_GE(1, 1);// a >= b
}

/**
 * The assertions in this group compare two C strings.
 * If you want to compare two string objects,
 * use EXPECT_EQ, EXPECT_NE, and etc instead.
 */
TEST(gtest, stringComparison) {
    EXPECT_STREQ("foo", "foo");
    EXPECT_STRNE("FOO", "foo");
    EXPECT_STRCASEEQ("FOO", "foo");
    EXPECT_STRCASENE("foo", "bar");
}