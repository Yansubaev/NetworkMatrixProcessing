import kotlinx.coroutines.*
import java.io.*
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer
import kotlin.system.measureTimeMillis

class Server{
    private val ss: ServerSocket
    private val port = 5603

    private val clients = mutableListOf<Client>()

    init{
        ss = ServerSocket(port)
        communicate()
    }

    private fun communicate() {
        while(true) {
            println("Ожидание подключения...")
            val cs = ss.accept()
            println("Клиент подключился...")
            if (cs!=null)
                Client(cs, clients)
        }
    }


    class Client(s: Socket, private val clients: MutableList<Client>){
        private var active: Boolean = true
        private var name: String? = null
        private var byteArray: ByteArray? = null
        private var intArray: IntArray? = null
        private var dataInputStream: DataInputStream? = null
        private var dataOutputStream: DataOutputStream? = null

        init {
            try {
                dataInputStream = DataInputStream(s.getInputStream())
                dataOutputStream = DataOutputStream(s.getOutputStream())
                clients.add(this)

                runBlocking { run() }
                val vol = 400
                val m1 = Matrix(vol)
                val m2 = Matrix(vol)
                m1.fillRandom(0..9000)
                m2.fillRandom(0..9000)
                //m1.print()
                println("next:")
                //m2.print()
                GlobalScope.launch {
                    sendMatrix(m1)
                    sendMatrix(m2)
                }
            } catch (e: Exception){
                e.printStackTrace()
            }
        }

        private fun run() = GlobalScope.launch {
                while (active) {
                    try {
                        var matrix: Matrix
                        if(receiveMessage()) {
                            matrix = receiveMatrix()
                            //matrix.print()
                        }else{
                            println("Error")
                        }

                    } catch (ex: SocketException) {
                        println("Client \"${this@Client.name}\": " + ex.message)
                        return@launch
                    }
                }
            }

        private fun receiveMessage(): Boolean {
            val s = dataInputStream?.readBoolean()
            println("message from client: $s")
            return s ?: false
        }

        private fun sendMatrix(matrix: Matrix){
            //val ba = matrix.getByteArray()
            val size = matrix.columns*matrix.rows
            dataOutputStream?.writeInt(size)
            dataOutputStream?.flush()
            dataOutputStream?.writeInt(matrix.rows)
            dataOutputStream?.flush()
            dataOutputStream?.writeInt(matrix.columns)
            dataOutputStream?.flush()

            val timer = measureTimeMillis {
/*
                for (ar in matrix.matrix) {
                    for (v in ar) {
                        dataOutputStream?.writeInt(v)
                       // dataOutputStream?.flush()
                    }
                }
*/
                for(ar in matrix.matrix){
                    val ba = fromIntsToBytes(ar)
                    dataOutputStream?.write(ba)
                    dataOutputStream?.flush()
                }
            }
            println("send timer = $timer")
        }

/*
        private fun receiveMatrix() : Matrix{
            val size = dataInputStream?.readInt()
            val rows = dataInputStream?.readInt()
            val columns = dataInputStream?.readInt()
            println("size=$size, rows=$rows, columns=$columns")

            if (size != null) {
                if (size > 0) {
                    intArray = IntArray(size)
//                    dataInputStream?.read(byteArray, 0, byteArray?.size ?: 0)
                    val timer = measureTimeMillis {
                        for (i in 0 until size) {
                            //byteArray?.set(i, dataInputStream?.readByte()!!)
                            intArray!![i] = dataInputStream?.readInt()!!
                        }
                    }
                    println("receive timer = $timer")
                }
            }
            //dataInputStream?.reset()
            return Matrix(intArray!!, rows ?: 0, columns ?: 0)
        }
*/

        private fun receiveMatrix() : Matrix {
            val size = dataInputStream?.readInt()
            val rows = dataInputStream?.readInt()
            val columns = dataInputStream?.readInt()
            println("size=$size, rows=$rows, columns=$columns")
            val intArray = Array(rows!!) { IntArray(columns!!) }

            if (size != null) {
                if (size > 0) {
                    val timer = measureTimeMillis {
                        for (i in 0 until rows) {
                            val ba = ByteArray(columns!! * 4)
                            dataInputStream?.read(ba, 0, columns!! * 4)
                            intArray[i] = fromBytesToInts(ba)
                        }
                    }
                    println("receive timer = $timer")

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
}

fun main() {
    Server()
}