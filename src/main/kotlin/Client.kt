import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.*
import java.net.Socket
import java.net.SocketException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer
import kotlin.system.measureTimeMillis

class Client {
    private var s: Socket? = null
    private val host: String = "127.0.0.1"
    private val port: Int = 5603
    private var byteArray: ByteArray? = null
    private var intArray: IntArray? = null
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

    private fun connect(): Boolean {
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
        while (active) {
            val m1 = receiveMatrix()
            val m2 = receiveMatrix()
            //m1.print()
            println("next:")
            //m2.print()
            try {
                val multM = multiplyMatrixes(m1!!, m2!!)
                sendMessage(true)
                sendMatrix(multM)
            } catch (ex: MatrixException){
                sendMessage(false)
                ex.printStackTrace()
                return@launch
            }
        }
    }

    private fun sendMessage(bool: Boolean) {
        dataOutputStream?.writeBoolean(bool)
        dataOutputStream?.flush()
    }

    private fun sendMatrix(matrix: Matrix) {
        val ba = matrix.getByteArray()
        val size = matrix.columns*matrix.rows
        dataOutputStream?.writeInt(size)
        dataOutputStream?.flush()
        dataOutputStream?.writeInt(matrix.rows)
        dataOutputStream?.flush()
        dataOutputStream?.writeInt(matrix.columns)
        dataOutputStream?.flush()
/*
        dataOutputStream?.write(ba)
        dataOutputStream?.flush()
*/

        for(ar in matrix.matrix){
            dataOutputStream?.write(fromIntsToBytes(ar))
            floatArrayOf()
        }

/*
        val timer = measureTimeMillis {
            for (ar in matrix.matrix) {
                for (v in ar) {
                    dataOutputStream?.writeInt(v)
                }
            }
        }
*/

    }

    private fun receiveMatrix() : Matrix{
        val size = dataInputStream?.readInt()
        val rows = dataInputStream?.readInt()
        val columns = dataInputStream?.readInt()
        println("size=$size, rows=$rows, columns=$columns")
        val intArray = Array(rows!!){ IntArray(columns!!) }

        if (size != null) {
            if (size > 0) {
                for(i in 0 until rows){
                    val ba = ByteArray(columns!! * 4)
                    dataInputStream?.read(ba, 0, columns!! * 4)
                    intArray[i] = fromBytesToInts(ba)
                }

/*
                intArray = IntArray(size)
//                    dataInputStream?.read(byteArray, 0, byteArray?.size ?: 0)
                val timer = measureTimeMillis {
                    for (i in 0 until size) {
                        //byteArray?.set(i, dataInputStream?.readByte()!!)
                        intArray!![i] = dataInputStream?.readInt()!!
                    }
                }
                println("receive timer = $timer")
*/
            }
        }
        //dataInputStream?.reset()
        return Matrix(intArray)
    }

    private fun fromBytesToInts(byteArray: ByteArray): IntArray{
        val ib: IntBuffer = ByteBuffer.wrap(byteArray).order(ByteOrder.BIG_ENDIAN).asIntBuffer()
        val iar = IntArray(ib.remaining())
        ib.get(iar)

        return iar
    }

    private fun fromIntsToBytes(intArray: IntArray): ByteArray{
        val baos = ByteArrayOutputStream()
        val dos = DataOutputStream(baos)
        for(v in intArray) {
            dos.writeInt(v)
        }

        return baos.toByteArray()
    }

}

fun main(args: Array<String>) {
    Client()

}