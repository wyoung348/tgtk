package cafe.dragons.tgtk.automata

interface FSM<T> {
    fun getCurrentState(): State<T>
    fun transition(symbol: T): Boolean
}