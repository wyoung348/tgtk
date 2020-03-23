package cafe.dragons.tgtk.automata

typealias Consumer<T> = (T) -> Unit
typealias Predicate<T> = (T) -> Boolean
typealias Edge<T> = Pair<Predicate<T>, State<T>>

class State<T>(
    val name: String,
    val type: Type,
    val onEnter: Consumer<T>,
    val onLeave: Consumer<T>
) {
    enum class Type { NORMAL, ACCEPT, REJECT }
    internal val edges: MutableMap<String, Edge<T>> = LinkedHashMap()
    override fun toString(): String = "State \"$name\""
}

class PredicateFSM<T> private constructor(
    private val start: State<T>,
    private val states: Map<String, State<T>>
): FSM<T> {
    var acceptState: Boolean = false
        private set
    var rejectState: Boolean = false
        private set
    private var currentState: State<T> = start

    override fun getCurrentState(): State<T> = start
    override fun transition(symbol: T): Boolean {
        for (edge in currentState.edges.values) {
            if (edge.first(symbol)) {
                currentState.onLeave(symbol)
                currentState = edge.second
                currentState.onEnter(symbol)
                when (currentState.type) {
                    State.Type.ACCEPT -> acceptState = true
                    State.Type.REJECT -> rejectState = true
                }
                return true
            }
        }
        // did not transition
        rejectState = true
        return false
    }

    class PFSMBuilder<T> {
        private lateinit var start: State<T>
        private val states: MutableMap<String, State<T>> = LinkedHashMap()

        fun add(
            name: String,
            type: State.Type = State.Type.NORMAL,
            onEnter: Consumer<T> = {},
            onLeave: Consumer<T> = {}
        ): PFSMBuilder<T> {
            val s = State<T>(name, type, onEnter, onLeave)
            states[name] = s
            if (!this::start.isInitialized) start = s
            return this
        }
        // throws if state1 or state2 don't exist
        fun link(state1: String, state2: String, vararg pres: Predicate<T>): PFSMBuilder<T> {
            for (pre in pres)
                states[state1]!!.edges[state2] = Edge(pre, states[state2]!!)
            return this
        }
        // throws if state doesn't exist
        fun setStart(state: String) { start = states[state]!! }
        fun build(): PredicateFSM<T> { return PredicateFSM(start, states.toMap()) }
    }
}
