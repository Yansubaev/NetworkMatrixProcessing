import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.*
import java.net.Socket

class Client {
    private var s: Socket? = null
    private val host: String = "127.0.0.1"
    private val port: Int = 5603
    private var byteArray: ByteArray? = null
    private var dataInputStream: DataInputStream? = null
    private var dataOutputStream: DataOutputStream? = null
    private var active = false


    private var onLogin: MutableList<((result: Boolean) -> Unit)> = mutableListOf()
    private var onReceiveMessage: MutableList<((result: String) -> Unit)> = mutableListOf()

    fun addLoginListener(m: (Boolean) -> Unit) {
        onLogin.add(m)
    }

    fun addMessageListener(message: (String) -> Unit){
        onReceiveMessage.add(message)
    }

    init {
        println(connect())
        while (true){

        }
    }

    fun connect(): Boolean {
        return try {
            s = Socket(host, port)

            dataInputStream = DataInputStream(s?.getInputStream())
            dataOutputStream = DataOutputStream(s?.getOutputStream())

            active = true
            runBlocking { run() }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun run() = GlobalScope.launch {
        println("зашело сюда")
        while (active) {
            println("сюда тоже зашло")

            receiveMatrix().print()
        }
    }

    private fun parse(data: String) {
        if (data.trim().isEmpty()) return
        val delim = ":"
        val parts = data.split(delimiters = *arrayOf(delim), limit = 2)
        if (parts[0].equals("LOGIN", true)) {
            onLogin.forEach { it(parts[1].contains("OK")) }
        }else if(parts[0].equals("MESSAGE", true)){
            var str = parts.subList(1, parts.size).toString()
            str = str.slice(1 until str.length-1)
            onReceiveMessage.forEach{it(str)}
        }
    }


    fun sendMatrix(matrix: Matrix) {
        val ba = matrix.getByteArray()
        dataOutputStream?.writeInt(ba.size)
        dataOutputStream?.flush()
        dataOutputStream?.writeInt(matrix.rows)
        dataOutputStream?.flush()
        dataOutputStream?.writeInt(matrix.columns)
        dataOutputStream?.flush()
        dataOutputStream?.write(ba)
        dataOutputStream?.flush()
    }

    private fun receiveMatrix() : Matrix{
        val size = dataInputStream?.readInt()
        val rows = dataInputStream?.readInt()
        val columns = dataInputStream?.readInt()
        println("size=$size, rows=$rows, columns=$columns")

        if (size != null) {
            if (size > 0) {
                byteArray = ByteArray(size)
                dataInputStream?.read(byteArray, 0, byteArray?.size ?: 0)
            }
        }
        return Matrix(byteArray, rows ?: 0, columns ?: 0)
    }
}

fun main(args: Array<String>) {
    Client()

}