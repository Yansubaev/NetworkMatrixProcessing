import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer
import kotlin.random.Random

class Matrix{
    var rows: Int
    var columns: Int
    var matrix: Array<IntArray>

    //region Constructors

    init {
        this.rows = 1
        this.columns = 1
        this.matrix = Array(rows){ IntArray(columns) }
    }

    @Throws(MatrixException::class)
    constructor(rows: Int, columns: Int) {
        if (rows <= 0 || columns <= 0){
            throw MatrixException("Invalid matrix dimension specified")
        }
        this.rows = rows
        this.columns = columns
        this.matrix = Array(rows){IntArray(columns)}
    }

    @Throws(MatrixException::class)
    constructor(size: Int){
        if (size <= 0){
            throw MatrixException("Invalid matrix dimension specified")
        }
        this.columns = size
        this.rows = size
        this.matrix = Array(rows){IntArray(columns)}
    }

    constructor(matrix: Array<IntArray>){
        this.rows = matrix.size
        this.columns = matrix[0].size
        this.matrix = matrix
    }

    @Throws(MatrixException::class)
    constructor(byteArray: ByteArray?, rows: Int, columns: Int) {
        if (rows <= 0 || columns <= 0){
            throw MatrixException("Invalid matrix dimension specified")
        }
        val ib: IntBuffer = ByteBuffer.wrap(byteArray).order(ByteOrder.BIG_ENDIAN).asIntBuffer()
        val iar = IntArray(ib.remaining())
        ib.get(iar)

        this.rows = rows
        this.columns = columns
        this.matrix = fromOneArrayToDoubleArray(iar, rows, columns)
    }
    @Throws(MatrixException::class)
    constructor(intArray: IntArray, rows: Int, columns: Int) {
        if (rows <= 0 || columns <= 0){
            throw MatrixException("Invalid matrix dimension specified")
        }
        this.rows = rows
        this.columns = columns
        this.matrix = fromOneArrayToDoubleArray(intArray, rows, columns)
    }

    //endregion Constructors

    //region Class Methods
    private fun fromOneArrayToDoubleArray(array: IntArray, rows: Int, columns: Int): Array<IntArray>{
        val m = Array(rows){ IntArray(columns) }
        for (k in 0 until array.size){
            m[k/columns][k%columns] = array[k]
        }
        return m
    }

    fun checkSquare(): Boolean = rows == columns

    fun print() {
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                print(matrix[i][j].toString() + "\t")
            }
            println()
        }
    }

    fun fill(number: Int) {
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                matrix[i][j] = number
            }
        }
    }

    fun fillRandom(range: IntRange){
        val r = Random
        for (i in 0 until rows){
            for(j in 0 until columns){
                matrix[i][j] = r.nextInt(range.first, range.last)
            }
        }
    }

    fun getByteArrayInputStream() : ByteArrayInputStream{
        val baos = ByteArrayOutputStream()
        val dos = DataOutputStream(baos)
        for(i in 0 until rows){
            for (j in 0 until columns){
                dos.writeInt(matrix[i][j])
            }
        }
        val ba = baos.toByteArray()

        return ByteArrayInputStream(ba)
    }

    fun getByteArray() : ByteArray{
        val baos = ByteArrayOutputStream()
        val dos = DataOutputStream(baos)
        for(i in 0 until rows){
            for (j in 0 until columns){
                dos.writeInt(matrix[i][j])
            }
        }
        return baos.toByteArray()
    }

    //endregion Class Methods
}

//region Static methods

@Throws(MatrixException::class)
fun sumMatrixes(matrix1: Matrix, matrix2: Matrix) : Matrix{
    if (!checkMatrixes(matrix1, matrix2, false)) {
        throw MatrixException("Invalid matrix dimension specified")
    } else {
        val size: Int = if (!matrix1.checkSquare()) {
            if (matrix1.rows > matrix1.columns) matrix1.columns else matrix1.rows
        } else {
            matrix1.rows
        }

        val matrix = Matrix(size)

        for (i in 0 until matrix1.rows) {
            for (j in 0 until matrix1.columns) {
                matrix.matrix[i][j] = matrix1.matrix[i][j] + matrix2.matrix[i][j]
            }
        }
        return matrix
    }
}

@Throws(MatrixException::class)
fun multiplyMatrixes(matrix1: Matrix, matrix2: Matrix): Matrix {
    val m1 = matrix1.matrix
    val m2 = matrix2.matrix
    val m1ColLength = m1[0].size // m1 columns length
    val m2RowLength = m2.size    // m2 rows length
    if (m1ColLength != m2RowLength) {
        throw MatrixException("Invalid matrix dimension specified")
        // matrix multiplication is not possible
    } else {

        val mRRowLength = m1.size    // m result rows length
        val mRColLength = m2[0].size // m result columns length
        val mResult = Array(mRRowLength) { IntArray(mRColLength) }
        for (i in 0 until mRRowLength) {         // rows from m1
            for (j in 0 until mRColLength) {     // columns from m2
                for (k in 0 until m1ColLength) { // columns from m1
                    mResult[i][j] += m1[i][k] * m2[k][j]
                }
            }
        }
        return Matrix(mResult)

/*
        val size: Int
        val n: Int

        if (!matrix1.checkSquare()) {
            size = if (matrix1.rows > matrix1.columns) matrix1.columns else matrix1.rows
            n = if (matrix1.rows > matrix1.columns) matrix1.rows else matrix1.columns
        } else {
            size = matrix1.rows
            n = matrix1.rows
        }

        val matrix = Matrix(size)
        matrix.fill(0)

        for (i in 0 until size) {
            for (j in 0 until size) {
                for (k in 0 until n) {
                    matrix.matrix[i][j] += matrix1.matrix[i][k] * matrix2.matrix[k][j]
                }
            }
        }
        return matrix
*/
    }
}

private fun checkMatrixes(matrix1: Matrix, matrix2: Matrix, forMultiply: Boolean): Boolean =
    if (forMultiply) {
        matrix1.rows == matrix2.rows && matrix1.columns == matrix2.columns || matrix1.columns == matrix2.rows
    } else {
        matrix1.rows == matrix2.rows && matrix1.columns == matrix2.columns
    }

//endregion Static methods

class MatrixException(var mes: String) : Exception() {
    override val message: String?
        get() = mes
}