import java.sql.*

class DatabaseOperations {
    private var connectionURL: String = "jdbc:mysql://localhost:3306/Matrixes"
    private var userName: String = "denis"
    private var password: String = "ukraina"

    init {
        try{
            Class.forName("com.mysql.jdbc.Driver")
            val connection: Connection = DriverManager.getConnection(connectionURL, userName, password)
            connection.close()
            println("Connection successful")
            //first(connection)
        } catch (ex: ClassNotFoundException){
            ex.printStackTrace()
            println("JDBC driver not found!")
        } catch (ex: SQLException){
            ex.printStackTrace()
        } catch (ex: Exception){
            ex.printStackTrace()
            println("Other exception")
        }
    }

    fun putMatrix(matrix: Matrix){
        val ins = matrix.getByteArrayInputStream()
        val rows = matrix.rows
        val columns = matrix.columns
        val query = "INSERT INTO Matrixes (`rows`, `columns`, `vals`) VALUES( ?, ?, ? )"
        try{
            val connection: Connection = DriverManager.getConnection(connectionURL, userName, password)
            connection.autoCommit = false
            val ps: PreparedStatement = connection.prepareStatement(query)
            ps.setInt(1, rows)
            ps.setInt(2, columns)
            ps.setBinaryStream(3, ins)
            ps.executeUpdate()
            connection.commit()
            connection.close()
            println("Putting successful")
        } catch (ex: ClassNotFoundException){
            ex.printStackTrace()
            println("JDBC driver not found!")
        } catch (ex: SQLException){
            ex.printStackTrace()
        } catch (ex: Exception){
            ex.printStackTrace()
            println("Other exception")
        }
    }

    fun getMatrix(): Matrix{
        var ba: ByteArray? = null
        var m: Matrix? = null
        try{
            val connection: Connection = DriverManager.getConnection(connectionURL, userName, password)
            val st: Statement = connection.createStatement()
            val rs: ResultSet = st.executeQuery("SELECT * FROM Matrixes WHERE ID='16'")
            var rows = 0
            var columns = 0
            while(rs.next()){
                ba = rs.getBytes("vals")
                rows = rs.getInt("rows")
                columns = rs.getInt("columns")
            }
            m = Matrix(ba, rows, columns)
            connection.close()
            println("Getting successful")
        } catch (ex: ClassNotFoundException){
            ex.printStackTrace()
            println("JDBC driver not found!")
        } catch (ex: SQLException){
            ex.printStackTrace()
        } catch (ex: Exception){
            ex.printStackTrace()
            println("Other exception")
        }
        return m ?: Matrix(0)
    }
}