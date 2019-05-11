import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class Client {
    private var s: Socket? = null
    private val host: String = "127.0.0.1"
    private val port: Int = 5603
    private var sender: PrintWriter? = null
    private var receiver: BufferedReader? = null
    private var active = false

    private var onLogin: MutableList<((result: Boolean) -> Unit)> = mutableListOf()
    private var onReceiveMessage: MutableList<((result: String)-> Unit)> = mutableListOf()

    fun addLoginListener(m: (Boolean) -> Unit) {
        onLogin.add(m)
    }

    fun addMessageListener(message: (String) -> Unit){
        onReceiveMessage.add(message)
    }

    fun connect(): Boolean {
        try {
            s = Socket(host, port)
            sender = PrintWriter(
                s?.getOutputStream()
            )
            receiver = BufferedReader(
                InputStreamReader(
                    s?.getInputStream()
                )
            )
            active = true
            runn()
            return true
        } catch (e: Exception) {
            return false
        }
    }

    private fun runn() = GlobalScope.launch {
        while (active) {
            val data = receiver?.readLine()
            parse(data ?: "")
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

    fun login(login: String) {
        try {
            //Проверка допустимости логина...
            //...
            //Отправка команды залогинивания
            sender?.println("LOGIN:" + login.trim())
            sender?.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun send(message: String) {
        sender?.println(message)
        sender?.flush()
    }
}