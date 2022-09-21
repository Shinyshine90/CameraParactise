package com.example.core.entity

enum class RenderMode(val mode: Int) {

    SINGLE_SIDE_FRONT(0x01),
    SINGLE_SIDE_BACK(0x02),
    SINGLE_SIDE_LEFT(0x03),
    SINGLE_SIDE_RIGHT(0x04),

    DOUBLE_SIDE_LR_FAIRLY(0x11),
    DOUBLE_SIDE_LR_LEFT_WEIGHT(0x12),
    DOUBLE_SIDE_LR_RIGHT_WEIGHT(0x13),

    TRIPLE_SIDE_LRB(0x21),

    FOUR_SIDE_T(0X31),
    FOUR_SIDE_H(0X32)


}