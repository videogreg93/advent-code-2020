import java.io.File
import java.util.regex.Pattern
import kotlin.math.ceil
import kotlin.streams.toList

fun main(args: Array<String>) {
    day7()
}

fun day7() {
    val input = readFile("7.txt")
    val rules = input.map { rule ->
        val color = rule.split(" ").take(2).reduce { acc, s -> "$acc $s" }
        if (rule.contains("no other bags")) {
            Rule(color, listOf())
        } else {
            val contents = Pattern
                .compile("[\\d] [\\w]* [\\w]* bag[s]?")
                .matcher(rule)
                .results().map {
                    it.group()
                }.toList().map {
                    val splitRule = it.split(" ")
                    val contentColor = splitRule[1] + " " + splitRule[2]
                    val count = splitRule[0].toInt()
                    contentColor to count
                }
            Rule(color, contents)
        }
    }.map { it.color to it }.toMap()
    val goldMap = HashMap<String, Int>()
    rules.values.forEach { rule ->
        calculateRule(rules, rule, goldMap)
    }
    println(goldMap.values.sum())
}

private fun calculateRule(rules: Map<String, Rule>, rule: Rule, goldMap: HashMap<String, Int>): Int {
    when {
        rule.color == "shiny gold" -> return 1
        rule.contents.isEmpty() -> {
            goldMap[rule.color] = 0
            return 0
        }
        else -> {
            val goldCount = rule.contents.sumBy { innerRule ->
                goldMap.getOrPut(innerRule.first) {
                    innerRule.second * calculateRule(rules, rules.getValue(innerRule.first), goldMap)
                }
            }
            goldMap[rule.color] = goldCount
        }
    }
}

data class Rule(val color: String, val contents: List<Pair<String, Int>>)

fun day6part2() {
    val groups = File("6.txt").readText().split("\n\n")
    val sum = groups.sumBy { group ->
        group
            .split("\n")
            .map { it.toCharArray() }
            .reduce { acc, s ->
                acc.intersect(s.toList()).toCharArray()
            }.size
    }
    println(sum)
}


fun day6() {
    val groups = File("6.txt").readText().split("\n\n")
    val sum = groups.sumBy { group ->
        group.filter {
            it.isLetter()
        }.toSet().size
    }
    println(sum)
}

fun day5() {
    val ids = ArrayList<Int>()
    val lines = readFile("5.txt")
    lines.forEach { line ->
        val rowIdentifier = line.subSequence(0, 7)
        val columnIdentifier = line.subSequence(7, 10)
        var bottomRow = 0
        var topRow = 127
        rowIdentifier.forEach {
            when (it) {
                'F' -> topRow -= ceil((topRow - bottomRow) / 2.0).toInt()
                'B' -> bottomRow += ceil((topRow - bottomRow) / 2.0).toInt()
                else -> error("$it is not either F or B")
            }
        }
        val row = bottomRow
        var bottomColumn = 0
        var topColumn = 7
        columnIdentifier.forEach {
            when (it) {
                'L' -> topColumn -= ceil((topColumn - bottomColumn) / 2.0).toInt()
                'R' -> bottomColumn += ceil((topColumn - bottomColumn) / 2.0).toInt()
                else -> error("$it is not either L or R")
            }
        }
        val column = bottomColumn
        val seatId = (row * 8) + column
        ids.add(seatId)
    }
    ids.sort()
    println(ids)
    var expected = 45
    for (i in 0..(953 - 45)) {
        if (ids[i] != expected && ids.contains(expected - 1) && ids.contains(expected + 1)) {
            println("My boarding pass is $expected")
            return
        }
        expected++
    }
}

typealias Validator = (String) -> Boolean

fun day4part2() {
    val input = File("4.txt").readText().split("\n\n")
    val requiredFields = listOf<Pair<String, Validator>>(
        Pair("byr") {
            if (it.length != 4) return@Pair false
            return@Pair it.toIntOrNull()?.let { number ->
                number in 1920..2002
            } ?: false
        },
        Pair("iyr") {
            if (it.length != 4) return@Pair false
            return@Pair it.toIntOrNull()?.let { number ->
                number in 2010..2020
            } ?: false
        },
        Pair("eyr") {
            if (it.length != 4) return@Pair false
            return@Pair it.toIntOrNull()?.let { number ->
                number in 2020..2030
            } ?: false
        },
        Pair("hgt") {
            val hgtRegex = Regex("[0-9]+(in|cm)")
            return@Pair when {
                !it.matches(hgtRegex) -> false
                it.contains("cm") -> {
                    it.split("cm").getOrNull(0)?.toIntOrNull()?.let { number ->
                        number in 150..193
                    } ?: false
                }
                it.contains("in") -> {
                    it.split("in").getOrNull(0)?.toIntOrNull()?.let { number ->
                        number in 59..76
                    } ?: false
                }
                else -> false
            }
        },
        Pair("hcl") {
            val hexRegex = Regex("[#][a-zA-Z0-9]{6}")
            return@Pair it.matches(hexRegex)
        },
        Pair("ecl") {
            val eclRegex = Regex("(amb|blu|brn|gry|grn|hzl|oth)")
            return@Pair it.matches(eclRegex)
        },
        Pair("pid") {
            val pidRegex = Regex("[0-9]{9}")
            return@Pair it.matches(pidRegex)
        }
    )
    var validPassports = 0
    val regex = Regex("[ \r\n]")
    input.forEach { passport ->
        val fields = passport.split(regex).mapNotNull {
            val array = it.split(":")
            if (array.size != 2) return@mapNotNull null
            array[0] to array[1]
        }
        if (!fields.map { it.first }.containsAll(requiredFields.map { it.first })) return@forEach
        val results = requiredFields.map { requiredField ->
            val field =
                fields.find { it.first == requiredField.first } ?: error("Could not find ${requiredField.first}")
            requiredField.second.invoke(field.second)
        }
        if (!results.contains(false)) validPassports++
    }
    println(validPassports)
}

fun day4() {
    val input = File("4.txt").readText().split("\n\n")
    val requiredFields = listOf("byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid")
    var validPassports = 0
    val regex = Regex("[ \r\n]")
    input.forEach { passport ->
        val fields = passport.split(regex).map { it.split(":")[0] }
        if (fields.containsAll(requiredFields)) validPassports++
    }
    println(validPassports)
}


fun day3() {
    val lines = readFile("3.txt")
    val map = lines.map { line ->
        line.map {
            it
        }
    }
    val directions = listOf(Pair(1, 1), Pair(3, 1), Pair(5, 1), Pair(7, 1), Pair(1, 2))
    val result = directions
        .map { getTreesForSlope(map, lines[0].length, lines.size, it.first, it.second) }
        .reduce { acc, i -> acc * i }
    println(result)
}

private fun getTreesForSlope(
    map: List<List<Char>>,
    mapWidth: Int,
    mapHeight: Int,
    deltaX: Int,
    deltaY: Int
): Int {
    var cursorX = 0
    var cursorY = 0
    var treeCount = 0
    while (cursorY < mapHeight) {
        cursorX = (cursorX + deltaX) % mapWidth
        cursorY += deltaY
        if (map.getOrNull(cursorY)?.getOrNull(cursorX) == '#') {
            treeCount++
        }
    }
    return treeCount
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