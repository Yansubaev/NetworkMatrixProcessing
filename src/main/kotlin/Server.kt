import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket

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
            if (cs!=null) Client(cs, clients)
        }
    }

    class Client(s: Socket, private val clients: MutableList<Client>){
        var active: Boolean = true
        var sender: PrintWriter? = null
        var receiver: BufferedReader? = null
        var name: String? = null

        init {
            try {
                sender = PrintWriter(
                    s.getOutputStream()
                )
                receiver = BufferedReader(
                    InputStreamReader(
                        s.getInputStream()
                    )
                )
                clients.add(this)
                runBlocking { run() }

            } catch (e: Exception){
                e.printStackTrace()
            }
        }

        private fun run() = GlobalScope.launch{
            while (active){
                val data = receiver?.readLine()
                parse(data ?: "")
            }
        }

        private fun parse(data: String){
            if (data.trim().isEmpty()) return
            val delim = ":"
            val parts = data.split(delimiters = *arrayOf(delim), limit = 2)
            if (parts[0].equals("LOGIN", true)){
                var ok = true
                for (c in clients){
                    if (c.name?.equals(parts[1])==true){
                        ok = false
                        break
                    }
                }
                if (ok) {
                    name = parts[1]
                    send("LOGIN:OK")
                } else {
                    send("LOGIN:FAILED")
                }
            }else {
                println(data)
                for(c in clients){
                    c.send("MESSAGE:$name: $data")
                }
            }
        }

        private fun send(s: String) {
            sender?.println(s)
            sender?.flush()
        }
    }
}

fun main() {
    Server()
}