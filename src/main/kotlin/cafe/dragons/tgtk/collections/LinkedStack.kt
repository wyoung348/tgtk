package cafe.dragons.tgtk.collections

class LinkedStack<T>: Stack<T> {
    private class Node<T>(val elem: T, var next: Node<T>?)

    private var head: Node<T>? = null

    override fun push(elem: T): T? {
        val new = Node(elem, head)
        head = new
        return elem
    }

    override fun pop(): T? {
        val old = head
        head = head?.next
        return old?.elem
    }

    override fun peek(): T? {
        return head?.elem
    }

    override fun clear() {
        head = null
    }
}