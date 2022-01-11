package indigo

import java.util.*
import kotlin.random.Random


fun main() {
    //menu()
    val game = Game()
    println("Indigo Card Game")
    var answer: String = ""
    do {
        println("Play first?")
        answer = readLine()!!.uppercase(Locale.getDefault())
    } while (answer != "YES" && answer != "NO")
    if (answer == "YES") {
        game.addPlayer(Human("Viktor"))
        game.addPlayer(Computer("Computer"))
    } else {
        game.addPlayer(Computer("Computer"))
        game.addPlayer(Human("Viktor"))
    }
    game.start()
}

fun menu() {
    val deck: Deck = Deck()
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

abstract class Player(internal val name: String) {
    val hand: MutableList<Card> = mutableListOf()
    abstract fun choseCard():Card?
    fun showHand(){
        println(hand.joinToString(" ", prefix = "Cards in hand: "){ (hand.indexOf(it) + 1).toString() + ")" + it })
    }
    fun takeCard(d: Deck) {
        hand.add(d.mainStack.pop())
    }
    open fun playCard(table: Stack<Card>, card: Card) {
        table.push(card)
        hand.remove(card)
    }
}

class Human(name: String): Player(name) {
    override fun choseCard(): Card? {
        showHand()
        while(true) {
            println("Choose a card to play (1-${hand.size}):")
            try {
                val input = readLine()!!
                if (input == "exit") {
                    return null
                }
                val n = input.toInt()
                if (n in 1..hand.size) {
                    return  hand[n - 1]
                }
            } catch (e: Exception) {

            }
        }
    }
}
class Computer(name: String): Player(name) {
    override fun choseCard(): Card? = hand[0]
    override fun playCard(table: Stack<Card>, card: Card) {
        super.playCard(table, card)
        println("${this.name} plays $card")
    }
}

class Game {
    private val players: MutableList<Player> = mutableListOf()
    private val turnSequence: Queue<Player> = LinkedList<Player>()
    private val deck: Deck = Deck()
    private val table: Stack<Card> = Stack<Card>()
    fun addPlayer(player: Player) {
        players.add(player)
        turnSequence.add(player)
    }
    fun start() {
        deck.getCards(4).forEach { table.push(it) }
        println(table.joinToString(" ", prefix = "Initial cards on the table: "))
        do  {
            printTableInfo()
            if(players.all { it.hand.size == 0 }) {
                for (i in 1..6) {
                    players.forEach { it.takeCard(deck) }
                }
            }
        } while (playTurn() && table.size < 52)
        if (table.size == 52) printTableInfo()
        println("Game Over")
    }

    private fun playTurn(): Boolean {
        val activePlayer = turnSequence.poll()!!
        turnSequence.add(activePlayer)
        val card: Card? = activePlayer.choseCard()
        return if (card == null) {
            false
        } else {
            activePlayer.playCard(table, card)
            true;
        }

    }

    private fun printTableInfo() {
        println("${table.size} cards on the table, and the top card is ${table.peek()}")
    }


}





class Deck {
    private val cards: MutableList<Card> = mutableListOf()
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
    fun getCards(n: Int): MutableList<Card> {
        val temp: MutableList<Card> = mutableListOf()
        for (i in 0 until n) {
            temp.add(mainStack.pop())
        }
        return temp
    }

    override fun toString(): String = cards.joinToString(" ") { it.toString() }
}

class Card(val rank: CardRank, val suit: CardSuit){
    override fun toString(): String = this.rank.sign + this.suit.sign
}