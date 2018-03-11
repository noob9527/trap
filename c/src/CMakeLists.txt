cmake_minimum_required(VERSION 3.2)

add_definitions(-std=c++11)
SET(CMAKE_C_COMPILER /usr/bin/c++)

# 将.目录所有源文件名保存至变量 SRC_FILES
aux_source_directory(. SRC_FILES)

# 生成链接库, 以供 test 调用
add_library(src STATIC ${SRC_FILES})
