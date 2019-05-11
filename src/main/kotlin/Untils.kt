import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer

fun main(args: Array<String>) {
    val dbo = DatabaseOperations()
    var m1 = Matrix(4)
    var m2 = Matrix(4)

    m1.fill(3)
    m2.fill(4)

    var m3 = multiplyMatrixes(m2, m1)

    for(v in m3.matrix[0]){
        println(v)
    }
    println("______________")


    var baos = ByteArrayOutputStream()
    var dos = DataOutputStream(baos)
    for(i in m3.matrix[0])
        dos.writeInt(i)
    var ba = baos.toByteArray()
/*
    for(v in ba)
        println(v)

    var ib: IntBuffer = ByteBuffer.wrap(ba).order(ByteOrder.BIG_ENDIAN).asIntBuffer()
    println("______________")
    var iar = IntArray(ib.remaining())
    ib.get(iar)

    for(v in iar)
        println(v)

*/
    var bais = ByteArrayInputStream(ba)
    //dbo.putMatrix(bais)
    dbo.getMatrix()
}