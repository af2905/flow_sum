package com.github.af2905.flow_sum

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var job: Job? = null

    private val resultView by lazy { findViewById<TextView>(R.id.result_tv) }
    private val inputLayout by lazy { findViewById<TextInputLayout>(R.id.input_layout) }
    private val startButton by lazy { findViewById<MaterialButton>(R.id.start_btn) }
    private val editTextView by lazy { findViewById<TextInputEditText>(R.id.input_txt) }

    private val strBuilder = StringBuilder()
    private var size = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }

    private fun initViews() {

        startButton.apply {
            isEnabled = false
            setOnClickListener {
                clearTask()

                val flowList = FlowEmitter.createFlowList(n = size)

                job = lifecycleScope.launch {
                    lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        flowList.forEach { flow ->
                            /**
                            По условию задачи значение на экране должно добавляться каждые 100мс.
                            Поэтому в данном случае использую билдер async, что позволяет запустить
                            в коррутинах асинхронно каждый наш flow. Поскольку delay в flow
                            расчитывается по формуле (index + 1) * 100, то мы получаем разницу между
                            delay в 100мс, что и требовалось по условию задачи.
                             */

                            val resultAsync = async {
                                FlowTransformer.sum(flow = flow).cancellable().collect {
                                    strBuilder.append("\n $it")
                                    resultView.text = strBuilder
                                }
                            }
                        }
                    }
                }
            }
        }

        editTextView.apply {
            addTextChangedListener {
                if (it?.isDigitsOnly() == true) {
                    startButton.isEnabled = it.isNotEmpty()
                    inputLayout.error = null
                    size = if (it.isEmpty()) Int.empty else it.toString().toInt()
                } else {
                    inputLayout.error = getString(R.string.error)
                    startButton.isEnabled = false
                }
                clearTask()
            }
        }
    }

    private fun clearTask() {
        job?.cancel()
        resultView.clear()
        strBuilder.clear()
    }
}

inline val String.Companion.empty get() = ""
inline val Int.Companion.empty get() = 0

fun TextView.clear() {
    text = String.empty
}