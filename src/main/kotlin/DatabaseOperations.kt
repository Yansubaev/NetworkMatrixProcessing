import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer
import java.sql.*

class DatabaseOperations {
    private var connectionURL: String = "jdbc:mysql://localhost:3306/Matrixes"
    private var userName: String = "denis"
    private var passwd: String = "ukraina"

    init {
        try{
            Class.forName("com.mysql.jdbc.Driver")
            val connection: Connection = DriverManager.getConnection(connectionURL, userName, passwd)
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

    fun putMatrix(ins: InputStream){
        val query = "INSERT INTO Matrixes (`rows`, `columns`, `vals`) VALUES( '4','4', ? )"
        try{
            val connection: Connection = DriverManager.getConnection(connectionURL, userName, passwd)
            connection.autoCommit = false
            var ps: PreparedStatement = connection.prepareStatement(query)
            ps.setBinaryStream(1, ins)
            ps.executeUpdate()
            connection.commit()
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

    fun getMatrix(){
        var ba: ByteArray? = null
        try{
            println("DATABASE")
            val connection: Connection = DriverManager.getConnection(connectionURL, userName, passwd)
            val st: Statement = connection.createStatement()
            val rs: ResultSet = st.executeQuery("SELECT vals FROM Matrixes WHERE ID='1'")
            while(rs.next()){
                ba = rs.getBytes("vals")
            }
            var ib: IntBuffer = ByteBuffer.wrap(ba).order(ByteOrder.BIG_ENDIAN).asIntBuffer()
            var iar = IntArray(ib.remaining())
            ib.get(iar)

            for(v in iar)
                println(v)

            connection.close()
            println("Query successful")
            connection.close()
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
}