//
// Created by xy on 18-3-14.
//

#include "gtest/gtest.h"

/**
 * 整数除法运算采用 趋零截尾
 */
TEST(Operator, divide) {
    EXPECT_EQ(3/2, 1);
    EXPECT_EQ(-3/2, -1);
}
