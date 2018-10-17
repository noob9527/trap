include_directories(${gtest_SOURCE_DIR}/include ${gtest_SOURCE_DIR})

#aux_source_directory(. TEST_FILES)
FILE(GLOB TEST_FILES *.test.cpp)

# 指定生成目标
add_executable(
        runUnitTests
        ${TEST_FILES}
        ../lib/googletest/googletest.test.cpp
)

# 添加 gtest 链接库
target_link_libraries(runUnitTests gtest gtest_main)

# 添加链接库 (这样test中才可以调用src中的函数)
target_link_libraries(runUnitTests src)
