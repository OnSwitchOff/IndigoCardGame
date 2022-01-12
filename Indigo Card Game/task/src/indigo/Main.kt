package indigo

import java.util.*
import kotlin.random.Random
import kotlin.reflect.typeOf


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
        game.addPlayer(Human("Player"))
        game.addPlayer(Computer("Computer"))
    } else {
        game.addPlayer(Computer("Computer"))
        game.addPlayer(Human("Player"))
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

enum class CardRank(val sign: String, val score: Int) {
    ACE("A", 1),
    TWO("2", 0),
    THREE("3", 0),
    FOUR("4", 0),
    FIVE("5", 0),
    SIX("6", 0),
    SEVEN("7", 0),
    EIGHT("8", 0),
    NINE("9", 0),
    TEN("10", 1),
    JACK("J", 1),
    QUEEN("Q", 1),
    KING("K", 1);
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
    val value: MutableList<Card> = mutableListOf()
    abstract fun choseCard(top: Card?):Card?
    fun showHand(){
        println(hand.joinToString(" ", prefix = "Cards in hand: "){ (hand.indexOf(it) + 1).toString() + ")" + it })
    }
    fun takeCard(d: Deck) {
        hand.add(d.mainStack.pop())
    }
    open fun playCard(table: Stack<Card>, card: Card): Boolean {
        hand.remove(card)
        if(table.size > 0) {
            if ( table.peek().rank == card.rank || table.peek().suit == card.suit) {
                for (c in table) {
                    value.add(c)
                }
                table.clear()
                value.add(card)
                return true
            }
        }
        table.push(card)
        return false
    }
}

class Human(name: String): Player(name) {
    override fun choseCard(top: Card?): Card? {
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
    override fun choseCard(top: Card?): Card? {
        println(hand.joinToString(" "))
        //1) If there is only one card in hand, put it on the table
        if (hand.size == 1) {
            return hand[0]
        } else {
            //3) If there are no cards on the table:
            if (top == null) {
                val maxS = CardSuit.values().maxOf { s -> hand.count { it.suit == s } }
                val maxR = CardRank.values().maxOf { r -> hand.count { it.rank == r } }
                //3.1
                return if (maxS > 1) {
                    val temp = CardSuit.values().filter { s -> hand.count { it.suit == s } == maxS }
                    val candidates = hand.filter { it.suit in temp }
                    candidates[0]
                } else if (maxR > 1) {
                    val temp = CardRank.values().filter { r -> hand.count { it.rank == r } == maxR }
                    val candidates = hand.filter { it.rank in temp }
                    candidates[0]
                } else {
                    hand[0]
                }
            } else {
                //2) If there is only one candidate card, put it on the table
                val candidates = hand.filter { it.rank == top.rank || it.suit == top.suit }
                if (candidates.size == 1) {
                    return  candidates[0]
                } else if (candidates.size > 1) {
                    val suitCand = candidates.filter { it.suit == top.suit }
                    val rankCand = candidates.filter { it.rank == top.rank }
                    return if (suitCand.isNotEmpty()) {
                        suitCand[0]
                    } else {
                        rankCand[0]
                    }
                } else {
                    val maxS = CardSuit.values().maxOf { s -> hand.count { it.suit == s } }
                    val maxR = CardRank.values().maxOf { r -> hand.count { it.rank == r } }
                    //3.1
                    return if (maxS > 1) {
                        val temp = CardSuit.values().filter { s -> hand.count { it.suit == s } == maxS }
                        val candidates2 = hand.filter { it.suit in temp }
                        candidates2[0]
                    } else if (maxR > 1) {
                        val temp = CardRank.values().filter { r -> hand.count { it.rank == r } == maxR }
                        val candidates2 = hand.filter { it.rank in temp }
                        candidates2[0]
                    } else {
                        hand[0]
                    }
                }
            }
        }
    }

    override fun playCard(table: Stack<Card>, card: Card): Boolean {
        val result = super.playCard(table, card)
        println("${this.name} plays $card")
        return result
    }
}

class Game {
    private val players: MutableList<Player> = mutableListOf()
    private val turnSequence: Queue<Player> = LinkedList<Player>()
    private val deck: Deck = Deck()
    private val table: Stack<Card> = Stack<Card>()
    private var lastWinner: Player? = null
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

        } while (playTurn() && (table.size + players.sumOf { it.value.size }) < 52)
        if ((table.size + players.sumOf { it.value.size }) == 52) {
            printTableInfo()
            if (table.size > 0) {
                table.forEach { lastWinner?.value?.add(it) }
                table.clear()
            }
            ShowFinalScore()
        }
        println("Game Over")
    }

    private fun playTurn(): Boolean {
        val activePlayer = turnSequence.poll()!!
        turnSequence.add(activePlayer)
        var top: Card? = null
        if (table.isNotEmpty()) {
            top = table.peek()
        }
        val card: Card? = activePlayer.choseCard(top)
        return if (card == null) {
            false
        } else {
            if ( activePlayer.playCard(table, card)) {
                println("${activePlayer.name} wins cards")
                ShowScore()
                lastWinner = activePlayer
            }
            true;
        }

    }

    private fun ShowScore() {
        if (players[0] is Human) {
            println("Score: Player ${players[0].value.sumOf { it.rank.score }} - Computer ${players[1].value.sumOf { it.rank.score }}")
            println("Cards: Player ${players[0].value.size} - Computer ${players[1].value.size}")
        } else {
            println("Score: Player ${players[1].value.sumOf { it.rank.score }} - Computer ${players[0].value.sumOf { it.rank.score }}")
            println("Cards: Player ${players[1].value.size} - Computer ${players[0].value.size}")
        }


    }

    private fun ShowFinalScore() {
        val c0 = players[0].value.size
        val c1 = players[1].value.size
        var s0 = players[0].value.sumOf { it.rank.score }
        var s1 = players[1].value.sumOf { it.rank.score }
        if (c1 > c0) {
            s1 +=3
        } else {
            s0 +=3
        }
        if (players[0] is Human) {
            println("Score: Player $s0 - Computer $s1")
            println("Cards: Player $c0 - Computer $c1")
        } else {
            println("Score: Player $s1 - Computer $s0")
            println("Cards: Player $c1 - Computer $c0")
        }

    }

    private fun printTableInfo() {
        if (table.size == 0) {
            println("No cards on the table")
        } else {
            println("${table.size} cards on the table, and the top card is ${table.peek()}")
        }
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