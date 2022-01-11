package indigo

fun main() {
    println(CardRank)
    println(CardSuit)
    println(Deck())
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
    constructor() {
        for (suit in CardSuit.values()) {
            for (rank in CardRank.values()){
                cards.add(Card(rank,suit))
            }
        }
    }
    override fun toString(): String = cards.joinToString(" ") { it.toString() }
}

class Card(val rank: CardRank, val suit: CardSuit){
    override fun toString(): String = this.rank.sign + this.suit.sign
}