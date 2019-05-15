fun main(args: Array<String>) {
    val dbo = DatabaseOperations()
    val m = Matrix(15, 7)
    m.fillRandom(-900..964)
    println("This matrix:")
    m.print()
    println("tipo tut matrica")

    dbo.putMatrix(m)
    val m1 = dbo.getMatrix()
    m1.print()
}