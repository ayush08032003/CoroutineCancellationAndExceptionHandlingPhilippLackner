package com.example.coroutinecancellationandexceptionhandling

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import java.util.concurrent.CancellationException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val tvException = findViewById<TextView>(R.id.tvException)
/*
        lifecycleScope.launch { // as this is simply an launch coroutine, it will check whether the exception is catched ot not and since it is not, it will rasie an exception.
            val string = async {// this is a child coroutine which raises exception to the parent coroutine
                delay(500L)
                throw Exception("error")
                "Result"
            }
        }

 */
        // ------------------------------ Method 2 ------------------------
 /*
        val deffered = lifecycleScope.async { //  This will not raise any exception untill it was not called .await().
            val string = async {// this is a child coroutine which raises exception to the parent coroutine
                delay(500L)
                throw Exception("error")
                "Result"
            }
        }
        lifecycleScope.launch {
//            deffered.await() // as soon as this lines wrote, it will raise an exception.. as expected..
            /*
            FATAL EXCEPTION: main
                Process: com.example.coroutinecancellationandexceptionhandling, PID: 24200
                java.lang.Exception: error
                    at com.example.coroutinecancellationandexceptionhandling.MainActivity$onCreate$deffered$1$string$1.invokeSuspend(MainActivity.kt:27)
                    at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
                    at kotlinx.coroutines.DispatchedTaskKt.resume(DispatchedTask.kt:234)
                   at kotlinx.coroutines.DispatchedTaskKt.dispatch(DispatchedTask.kt:166).........
             */

            // but if we include this line alone using try and catch, it will not generate any error..
            try{ // But this method is not recommended as much..
                deffered.await()
            }catch (e:Exception){
                e.printStackTrace() // This will generate the error as warning block in Logcat block
            }
        }

 */
        // ----------------------------- Method 3 ---------------------------------------
        /*
        val handler = CoroutineExceptionHandler{ _, throwable ->
            println("Caught Exception: $throwable")
        }// Creates a CoroutineExceptionHandler instance Params: handler - a function which handles exception thrown by a coroutine
        lifecycleScope.launch (handler){ // by this, you can also catch the exceptions of all the child coroutines..
           launch{
               throw Exception("Error")
           }
            withContext(Dispatchers.Main){
                tvException.text = "Caught Exception"
            }

        }

         */
        // Even after adding exception handler, coroutine2 finished will not show up, this shows that 2nd coroutine will never runs..
        // and the reason is coroutineScope, because, as soon as one child coroutine failes, it doesnot matter how many other child coroutine present, it will cancel them all. and then cancel the whole scope.
        // to make it right, need to use supervisorScope.
        /*
        val handler = CoroutineExceptionHandler{_, throwable ->  println("Caught Exception: $throwable") }
        CoroutineScope(Dispatchers.Main + handler).launch{
            supervisorScope {
                launch {
                    delay(300L)
                    throw Exception("Coroutine 1 Failed")
                }
                launch {
                    delay(400L)
                    println("Coroutine 2 Finished.. ")
                }

            }
        }
*/
        // ------------------------------------------- Method 4 ----------------------------------------
        lifecycleScope.launch {
            val job = launch {
                try{
                    delay(500L)
                }catch (e:Exception){
//                    println(e.printStackTrace())
                    if(e is CancellationException) throw e
                    e.printStackTrace()
                }
                println("Coroutine 1 finished..!")
            }
            delay(300L)
            job.cancel()
        }

    }
}
/*
NOTES/STEPS:
Need to include dependencies, same as of lifeCycleScope coroutine
    implementation 'androidx.navigation:navigation-fragment-ktx:2.6.0'
    implementation 'androidx.navigation:navigation-ui-ktx:2.6.0'


 */