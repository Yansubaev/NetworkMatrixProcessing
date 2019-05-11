class Matrix{
    var rows: Int
    var columns: Int
    var matrix: Array<IntArray>

    init {
        this.rows = 1
        this.columns = 1
       // this.matrix = Array(rows){ Array(columns) {0} }
        this.matrix = Array(rows){ IntArray(columns) }
    }

    @Throws(MatrixException::class)
    constructor(rows: Int, columns: Int) {
        if (rows <= 0 || columns <= 0){
            throw MatrixException(
                Throwable("Invalid matrix dimensions specified")
            )
        }
        this.rows = rows
        this.columns = columns
        this.matrix = Array(rows){IntArray(columns)}
    }

    @Throws(MatrixException::class)
    constructor(size: Int){
        if (size <= 0){
            throw MatrixException(
                Throwable("Invalid matrix dimensions specified")
            )
        }
        this.columns = size
        this.rows = size
        this.matrix = Array(rows){IntArray(columns)}
    }
    constructor(matrix: Array<IntArray>){
        this.matrix = matrix
    }

    fun checkSquare(): Boolean = rows == columns

    fun print() {
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                print(matrix[i][j].toString() + " ")
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
}

@Throws(MatrixException::class)
fun sumMatrixes(matrix1: Matrix, matrix2: Matrix) : Matrix{
    if (!checkMatrixes(matrix1, matrix2, false)) {
        throw MatrixException(
            Throwable("Invalid matrix dimensions specified")
        )
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
    if (!checkMatrixes(matrix1, matrix2, true)) {
        throw MatrixException(
            Throwable("Invalid matrix dimensions specified")
        )
    } else {

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
    }
}

private fun checkMatrixes(matrix1: Matrix, matrix2: Matrix, forMultiply: Boolean): Boolean =
    if (forMultiply) {
        matrix1.rows == matrix2.rows && matrix1.columns == matrix2.columns || matrix1.columns == matrix2.rows
    } else {
        matrix1.rows == matrix2.rows && matrix1.columns == matrix2.columns
    }

class MatrixException(cause: Throwable) : Exception() {
    override var cause: Throwable? = cause

    fun cause() : Throwable? = cause
}