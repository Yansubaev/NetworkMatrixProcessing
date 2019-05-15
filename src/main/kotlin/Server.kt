import kotlinx.coroutines.*
import java.io.*
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException

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
        private var dataInputStream: DataInputStream? = null
        private var dataOutputStream: DataOutputStream? = null

        init {
            try {
                dataInputStream = DataInputStream(s.getInputStream())
                dataOutputStream = DataOutputStream(s.getOutputStream())
                clients.add(this)

                runBlocking { run() }
                val matrix = Matrix(13, 15)
                matrix.fillRandom(-99..99)
                sendMatrix(matrix)
            } catch (e: Exception){
                e.printStackTrace()
            }
        }

        private fun run() = GlobalScope.launch {
                while (active) {
                    try {
                        val matrix = receiveMatrix()
                    } catch (ex: SocketException) {
                        println("Client \"${this@Client.name}\": " + ex.message)
                        return@launch
                    }
                }
            }

        private fun sendMatrix(matrix: Matrix){
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
}

fun main() {
    Server()
}