cmake_minimum_required(VERSION 3.22.1)

project(AdFilter)

add_library(
        adblock-client
        SHARED
        src/main/cpp/adblockclient-lib.cpp
        src/main/cpp/third-party/ad-block/ad_block_client.cc
        src/main/cpp/third-party/ad-block/cosmetic_filter.cc
        src/main/cpp/third-party/ad-block/filter.cc
        src/main/cpp/third-party/ad-block/no_fingerprint_domain.cc
        src/main/cpp/third-party/ad-block/context_domain.cc
        src/main/cpp/third-party/ad-block/protocol.cc
        src/main/cpp/third-party/bloom-filter-cpp/BloomFilter.cpp
        src/main/cpp/third-party/hashset-cpp/hashFn.cc
        src/main/cpp/third-party/hashset-cpp/hash_set.cc
)

find_library(
        log-lib
        log
)

target_link_libraries(
        adblock-client
        ${log-lib}
)