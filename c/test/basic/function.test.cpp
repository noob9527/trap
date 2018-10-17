//
// Created by xy on 18-11-21.
//

#include "gtest/gtest.h"

TEST(function, memset) {
    char str[] = "aaabbb";
    memset(str + 3, 'a', 3*sizeof(char));
    EXPECT_STREQ(str, "aaaaaa");
}
