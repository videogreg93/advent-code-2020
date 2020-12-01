import java.io.File

fun main(args: Array<String>) {
    readFile("test.txt").forEach {
        println(it)
    }
}


fun readFile(filename: String): List<String> {
    return File(filename).readLines()
}   