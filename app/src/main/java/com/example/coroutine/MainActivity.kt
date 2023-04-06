package com.example.coroutine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.Settings.Global
import android.util.Log
import com.example.coroutine.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var handler: Handler
    lateinit var channel: Channel<Long>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

       // 1. 핸들러등록
        handler = object:Handler(){
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                val value = msg.obj as String
                binding.tvSumResult.text = "sum = ${msg.obj}"
            }
        }

        //2.1 Message 역할 (코루틴 처리방식 채널객체생성)
        channel = Channel<Long>()
        //2.1 Handler 역할 (코루틴 처리방식 채널객체생성)
        val mainScope = GlobalScope.launch(Dispatchers.Main) {
            channel.consumeEach {
                binding.tvSumResult.text = "sum = ${it}"
            }
        }

        //3.스레드를 설계한다.
        val backgroundScope = CoroutineScope(Dispatchers.Default + Job())

        //오래동안 시간이 걸리는 작업(6~8초)을 요청
        binding.btnClick.setOnClickListener {
            backgroundScope.launch {
                var sum = 0L
                var time = measureTimeMillis {
                    for ( i in 1..2_000_000_000){
                        sum += i
                    }
                }
                Log.e("MainActivity","${time}")
//                binding.tvSumResult.text = "sum = ${sum}"
//                val message:Message = Message()
//                message.obj = "${sum}"
//                handler.sendMessage(message)
                  channel.send(sum.toLong())
            }
        }
        binding.btnClear.setOnClickListener {
            binding.tvSumResult.text = "합계출력: 0"
        }
    }
}