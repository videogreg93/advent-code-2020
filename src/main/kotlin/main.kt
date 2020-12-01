import java.io.File

fun main(args: Array<String>) {
   day1()
}

fun day1() {
    // For part one simply remove the third loop in getBothNumbers
    val numbers = readFile("1-1.txt").map { it.toInt() }
    val pair = getBothNumbers(numbers)
    println(pair.first * pair.second * pair.third)
}

private fun getBothNumbers(numbers: List<Int>): Triple<Int,Int,Int> {
    numbers.forEachIndexed { index, number ->
        numbers.subList(index + 1, numbers.lastIndex).forEach { secondNumber ->
            numbers.subList(index + 2, numbers.lastIndex).forEach { thirdNumber ->
                val sum = number + secondNumber + thirdNumber
                if (sum == 2020) return Triple(number, secondNumber, thirdNumber)
            }
        }
    }
    error("Numbers not found")
}


fun readFile(filename: String): List<String> {
    return File(filename).readLines()
}