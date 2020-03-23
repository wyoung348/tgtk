package cafe.dragons.tgtk.scene.tokens

interface Condition {
    fun eval(dict: Dictionary): Boolean
}

class TrueCondition: Condition {
    override fun eval(dict: Dictionary) = true
}

class FalseCondition: Condition {
    override fun eval(dict: Dictionary) = false
}

open class CompoundCondition(
    private val l: Condition,
    private val r: Condition,
    private val op: (Boolean, Boolean) -> Boolean
): Condition {
    override fun eval(dict: Dictionary) = op(l.eval(dict), r.eval(dict))
}
class AndCondition(l: Condition, r: Condition): CompoundCondition(l, r, { a, b -> a && b })
class OrCondition(l: Condition, r: Condition): CompoundCondition(l, r, { a, b -> a || b })
class XorCondition(l: Condition, r: Condition): CompoundCondition(l, r, { a, b -> a xor b })
