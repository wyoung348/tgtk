package cafe.dragons.tgtk.collections

interface Stack<T> {
    fun push(elem: T): T?
    fun pop(): T?
    fun peek(): T? {
        val elem = pop()
        if (elem != null) push(elem)
        return elem
    }
}