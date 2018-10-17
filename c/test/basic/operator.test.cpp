//
// Created by xy on 18-3-14.
//

#include "gtest/gtest.h"

/**
 * 整数除法运算采用 趋零截尾
 */
TEST(Operator, divide) {
    EXPECT_EQ(3 / 2, 1);
    EXPECT_EQ(-3 / 2, -1);
}

/**
 * sizeof 运算符以字节为单位返回其操作数大小, 返回类型为size_t
 * 操作数可以是一个具体的数据对象（如一个变量名），或者一个类型
 * 如果是一个类型，则操作数必须被括在圆括号里
 * tips: 使用%zd作为用来显示size_t类型值的printf()说明符
 */
TEST(Operator, sizeof) {
    char c = 'c';
    EXPECT_EQ(sizeof c, 1);
    EXPECT_EQ(sizeof(char), 1);
}

/**
 * 在使用结构指针的情况下，可以使用 -> 运算符访问结构成员
 */
TEST(Operator, arrow) {
    struct person {
        char firstname[100];
        char lastname[100];
        int age;
    } foo = {
            "stephen",
            "curry",
            30
    };

    struct person *ptr = &foo;

    EXPECT_STREQ(foo.firstname, "stephen");
    EXPECT_STREQ((*ptr).firstname, "stephen");
    EXPECT_STREQ(ptr->firstname, "stephen");
}

/**
 * 点运算符优先级高于&运算符
 */
TEST(Opertor, reference) {
    struct person {
        char firstname[100];
    } foo = {"stephen"};

    EXPECT_EQ(&foo.firstname, &(foo.firstname));
}
