package cafe.dragons.tgtk.scene.token

import cafe.dragons.tgtk.scene.TextProperty

typealias Dictionary = MutableMap<String, TextProperty>

interface Token {
    fun toString(dict: Dictionary): String?
}

class StaticToken(private val value: String): Token {
    override fun toString(dict: Dictionary) = value
}

class SimpleToken(private val key: String): Token {
    override fun toString(dict: Dictionary) = dict[key]?.toString() ?: key
}

class ConditionalToken(private val key: String, private val cond: Condition): Token {
    class Condition { // TODO
        fun eval(dict: Dictionary) = true // TODO
    }
    override fun toString(dict: Dictionary) = if (cond.eval(dict)) dict[key]?.toString() ?: key else null
}

class CompoundToken: Token, MutableList<Token> by ArrayList() {
    override fun toString(dict: Dictionary): String {
        val builder = StringBuilder()
        for (t in this)
            builder.append(t.toString(dict) ?: "")
        return builder.toString()
    }
}