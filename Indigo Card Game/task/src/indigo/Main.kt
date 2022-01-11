package indigo

import java.util.*
import kotlin.random.Random

fun main() {
    val deck: Deck = Deck()
    menu(deck)

}

fun menu(deck: Deck) {
    while (true) {
        println("Choose an action (reset, shuffle, get, exit):")
        when(readLine()!!){
            "reset" -> resetCommand(deck)
            "shuffle" -> shuffleCommand(deck)
            "get" -> getCommand(deck)
            "exit" -> {
                println("Bye")
                return
            }
            else -> {
                println("Wrong action.")
            }
        }
    }
}

fun getCommand(deck: Deck) {
    println("Number of cards:")
    val input = readLine()!!
    try {
        val n = input.toInt()
        if (n in 1..52) {
            println(deck.getCards(n))
        } else {
            println("Invalid number of cards.")
        }
    } catch (e: Exception) {
        println("Invalid number of cards.")
    }
}

fun resetCommand(deck: Deck) {
    deck.reset()
    println("Card deck is reset.")
}

fun shuffleCommand(deck: Deck) {
    deck.collectRemainingCard()
    deck.shuffle()
    println("Card deck is shuffled.")
}

enum class CardRank(val sign: String) {
    ACE("A"),
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    SIX("6"),
    SEVEN("7"),
    EIGHT("8"),
    NINE("9"),
    TEN("10"),
    JACK("J"),
    QUEEN("Q"),
    KING("K");
    companion object {
        override fun toString(): String = values().joinToString(" ") { it.sign }
    }
}

enum class CardSuit(val sign: Char){
    DIAMONDS('♦'),
    HEARTS('♥'),
    SPADES('♠'),
    CLUBS('♣');
    companion object {
        override fun toString(): String = values().joinToString(" ") { it.sign.toString() }
    }
}

class Deck {
    val cards: MutableList<Card> = mutableListOf()
    val mainStack: Stack<Card> = Stack<Card>()
    constructor() {
        generateCards()
    }
    private fun generateCards() {
        for (suit in CardSuit.values()) {
            for (rank in CardRank.values()){
                cards.add(Card(rank,suit))
            }
        }
        shuffle()
    }
    fun reset() {
        cards.clear()
        mainStack.clear()
        generateCards()
    }
    fun collectRemainingCard() {
        mainStack.forEach { cards.add(it) }
        mainStack.clear()
    }
    fun shuffle() {
        val length = cards.size
        for (i in 0 until length) {
            val n: Int = Random.nextInt(cards.size)
            val randomCard = cards[n]
            mainStack.push(randomCard)
            cards.remove(randomCard)
        }
    }
    fun getCards(n: Int): String {
        return if (mainStack.size < n) {
            "The remaining cards are insufficient to meet the request."
        } else {
            val temp: MutableList<Card> = mutableListOf()
            for (i in 0 until n) {
                temp.add(mainStack.pop())
            }
            temp.joinToString(" ")
        }
    }

    override fun toString(): String = cards.joinToString(" ") { it.toString() }
}

class Card(val rank: CardRank, val suit: CardSuit){
    override fun toString(): String = this.rank.sign + this.suit.sign
}