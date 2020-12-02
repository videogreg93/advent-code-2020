import java.io.File

fun main(args: Array<String>) {
    day2part2()
}

fun day2part2() {
    val lines = readFile("2.txt")
    var validPasswords = 0
    lines.forEach { line ->
        val attributes = line.split(" ")
        val values = attributes[0].split("-")
        val firstPosition = values[0].toInt() - 1
        val secondPosition = values[1].toInt() - 1
        val letter = attributes[1][0]
        val password = attributes[2]
        if ((password[firstPosition] == letter).xor(password[secondPosition] == letter)) {
            validPasswords++
        }
    }
    println(validPasswords)
}

fun day2() {
    val lines = readFile("2.txt")
    var validPasswords = 0
    lines.forEach { line ->
        val attributes = line.split(" ")
        val values = attributes[0].split("-")
        val min = values[0].toInt()
        val max = values[1].toInt()
        val letter = attributes[1][0]
        val password = attributes[2]
        val actualCount = password.split(letter).size - 1
        if (actualCount in min..max) {
            validPasswords++
        }
    }
    println(validPasswords)
}


fun day1() {
    // For part one simply remove the third loop in getBothNumbers
    val numbers = readFile("1-1.txt").map { it.toInt() }
    val pair = getBothNumbers(numbers)
    println(pair.first * pair.second * pair.third)
}

private fun getBothNumbers(numbers: List<Int>): Triple<Int, Int, Int> {
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