import javax.swing.*

class MainFrame: JFrame() {

    private val c: Client = Client()
    private val chatArea: JTextArea = JTextArea()
    private val inputField: JTextField
    private val sendBtn: JButton = JButton()
    private var status = 0

    init {
        setSize(500, 450)
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE

        chatArea.isEditable = false
        inputField = JTextField()

        sendBtn.text = "Подключиться"
        val gl = GroupLayout(contentPane)
        layout = gl
        createLayout(gl)

        isVisible = true

        sendBtn.addActionListener {
            when (status){
                0 -> {
                    if (c.connect()) {
                        status++
                        chatArea.append("Соединение успешно!\n")
                        sendBtn.text = "Войти"
                        inputField.requestFocus()
                    }
                }
                1 -> {
                    status++
                    c.login(inputField.text)
                    inputField.text = ""
                }
                2 -> {
                    println("status = 2")
                }
                3 -> {
                    c.send(inputField.text)
                    inputField.text = ""
                }
            }
            println("status = $status")
        }

        c.addLoginListener {
            if (it){
                chatArea.append("Успешный вход...\n")
                status++
                sendBtn.text = "Отправить"
            } else {
                chatArea.append("Неудачный вход...\n")
            }
        }

        c.addMessageListener {
            println(it)
            chatArea.append(it + "\n")
        }
    }

    private fun createLayout(gl: GroupLayout) {
        gl.setHorizontalGroup(
            gl.createSequentialGroup()
                .addGap(8)
                .addGroup(
                    gl.createParallelGroup()
                        .addComponent(chatArea, 300, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                        .addGroup(
                            gl.createSequentialGroup()
                                .addComponent(inputField, 250, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                                .addGap(8)
                                .addComponent(sendBtn, 40, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                        )
                )
                .addGap(8)

        )
        gl.setVerticalGroup(
            gl.createSequentialGroup()
                .addGap(8)
                .addComponent(chatArea, 300, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                .addGap(8)
                .addGroup(
                    gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(inputField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(sendBtn, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                )
                .addGap(8)
        )
    }
}

fun main() {
    MainFrame()
    //DatabaseOperations()
}