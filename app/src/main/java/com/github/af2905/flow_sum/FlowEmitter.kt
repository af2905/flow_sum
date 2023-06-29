package com.github.af2905.flow_sum

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

private const val DELAY_VALUE_DEFAULT = 100

object FlowEmitter {

    fun createFlowList(n: Int): List<Flow<Int>> {
        val list = mutableListOf<Flow<Int>>()
        for (index in 0 until n) {
            list.add(
                flow {
                    delay(delayTime(index))
                    emit(index.inc())
                }
            )
        }
        return list
    }

    private fun delayTime(index: Int): Long {
        return ((index.inc()) * DELAY_VALUE_DEFAULT).toLong()
    }
}