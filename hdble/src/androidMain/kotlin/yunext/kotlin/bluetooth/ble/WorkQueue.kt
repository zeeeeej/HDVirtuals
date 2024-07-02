package yunext.kotlin.bluetooth.ble

import java.util.concurrent.BlockingQueue
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue



class WorkQueue<T:Work> {

    private val queue: BlockingQueue<T> = LinkedBlockingQueue()


    fun put(t:T){
        queue.put(t)
    }

    fun take(block: T.() -> Unit) {
        try {
            val take = queue.take() ?: return
            take.block()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    fun close(){
        queue.clear()
    }
}