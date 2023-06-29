package com.github.af2905.flow_sum

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

object FlowTransformer {
    fun sum(flow: Flow<Int>): Flow<Int> {
        return flow.transform { value ->
            val result = (0..value).sum()
            emit(result)
        }
    }
}