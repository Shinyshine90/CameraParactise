package com.example.core.entity.config

/**
 * 录制配置
 * @param enable 是否开启录制
 * @param recordDuration 每个录制文件分片的时长, 单位秒
 */
data class RecordConfig(
    val enable: Boolean,
    val recordDuration: Long,
    val videoWidth: Int = 1280,
    val videoHeight: Int = 720
)