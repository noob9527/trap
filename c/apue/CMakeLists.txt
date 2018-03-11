cmake_minimum_required(VERSION 3.5)
project(apue)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

include_directories(include)
link_directories(lib)

add_subdirectory(src/1-unix-system-overview)
