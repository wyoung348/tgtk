package cafe.dragons.tgtk.collections

class LinkedList<T>: MutableList<T> {

    private var szCache = 0
    private var head: Node<T> = Node.Head()
    private var tail: Node<T> = Node.Tail()
    init {
        head.next = tail
        tail.prev = head
    }

    private fun go(index: Int): Node<T> {
        var cur = head
        for (i in 0..index) cur = cur.next
        return cur
    }
    private fun find(elem: T): Pair<Int, Node<T>> {
        var cur = head.next
        for (i in 0 until size) {
            if (cur is Node.Elem && cur.elem == elem) return Pair(i, cur)
            cur = cur.next
        }
        return Pair(-1, head) // object is not in our list
    }
    private fun revfind(elem: T): Pair<Int, Node<T>> {
        var cur = tail.prev
        for(i in (size - 1) downTo 0) {
            if (cur is Node.Elem && cur.elem == elem) return Pair(i, cur)
            cur = cur.prev
        }
        return Pair(-1, head)
    }
    private fun link(a: Node<T>, b: Node<T>) {
        a.next = b
        b.prev = a
    }
    private fun prepend(node: Node<T>, to: Node<T>) {
        link(to.prev, node)
        link(node, to)
    }
    private fun append(node: Node<T>, to: Node<T>): Node<T> {
        link(node, to.next)
        link(to, node)
        return node
    }
    private fun checkBounds(index: Int) {
        if (index > size) throw IndexOutOfBoundsException()
    }

    override val size: Int
        get() = szCache

    override fun contains(element: T): Boolean = find(element).second is Node.Elem

    override fun containsAll(elements: Collection<T>): Boolean {
        var contall = true
        for (elem in elements)
            contall = contains(elem) && contall
        return contall
    }

    override fun get(index: Int): T {
        checkBounds(index)
        return (go(index) as Node.Elem).elem // if this throws then size is off
    }

    override fun indexOf(element: T): Int = find(element).first

    override fun isEmpty(): Boolean = size <= 0

    override fun lastIndexOf(element: T): Int = revfind(element).first

    override fun add(element: T): Boolean {
        prepend(Node.Elem(element), tail)
        szCache++
        return true
    }

    override fun add(index: Int, element: T) {
        checkBounds(index)
        val ref = go(index)
        prepend(Node.Elem(element), ref)
        szCache++
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        checkBounds(index)
        val iter = elements.iterator()
        val ref = go(index)
        var cur = ref.prev
        while (iter.hasNext()) {
            link(cur, Node.Elem(iter.next()))
            if (iter.hasNext()) cur = cur.next // don't do this on the last iteration so we can relink the list
        }
        link(cur, ref)
        szCache += elements.size
        return true
    }

    override fun addAll(elements: Collection<T>): Boolean {
        for (elem in elements)
            prepend(Node.Elem(elem), tail)
        szCache += elements.size
        return true
    }

    override fun clear() {
        link(head, tail)
        szCache = 0
    }

    override fun iterator(): MutableIterator<T> = Iterator(this)

    override fun listIterator(): MutableListIterator<T> = Iterator(this)

    override fun listIterator(index: Int): MutableListIterator<T> = Iterator(this, index)

    override fun remove(element: T): Boolean {
        val ref = find(element).second
        if (ref is Node.Elem)
            link(ref.prev, ref.next)
        return ref is Node.Elem
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        var removedAny = false
        for (elem in elements)
            removedAny = remove(elem) || removedAny
        return removedAny
    }

    override fun removeAt(index: Int): T {
        checkBounds(index)
        val ref = go(index)
        link(ref.prev, ref.next)
        return (ref as Node.Elem).elem
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        val iter = listIterator()
        var changed = false
        var cur: T
        while (iter.hasNext()) {
            cur = iter.next()
            if (cur !in elements) {
                iter.remove()
                changed = true
            }
        }
        return changed
    }

    override fun set(index: Int, element: T): T {
        checkBounds(index)
        (go(index) as Node.Elem).elem = element
        return element
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> {
        checkBounds(fromIndex)
        checkBounds(toIndex)
        if (toIndex > fromIndex) throw IndexOutOfBoundsException()
        val new = LinkedList<T>()
        var cur = go(fromIndex)
        for (i in fromIndex until toIndex) {
            new.add((cur as Node.Elem).elem)
            cur = cur.next
        }
        return new
    }

    private sealed class Node<T> {
        abstract var prev: Node<T>
        abstract var next: Node<T>

        class Elem<T>(var elem: T): Node<T>() {
            override lateinit var prev: Node<T>
            override lateinit var next: Node<T>
        }
        class Head<T>: Node<T>() {
            override var prev: Node<T> = this
                set(value) {
                    field = this
                    throw UnsupportedOperationException()
                }
            override lateinit var next: Node<T>
        }
        class Tail<T>: Node<T>() {
            override lateinit var prev: Node<T>
            override var next: Node<T> = this
                set(value) {
                    field = this
                    throw UnsupportedOperationException()
                }
        }
    }

    class Iterator<T> internal constructor(
        private var me: LinkedList<T>,
        private var index: Int = 0
    ): MutableListIterator<T> {
        private var cur: Node<T> = me.go(index)

        override fun add(element: T) {
            cur = me.append(Node.Elem(element), cur)
            me.szCache++
            index++
        }
        override fun remove() {
            me.link(cur.prev, cur.next)
            cur = cur.prev
            index--
        }
        override fun set(element: T) {
            if (cur is Node.Elem) (cur as Node.Elem).elem = element
            else add(element)
        }
        override fun hasPrevious(): Boolean = cur.prev is Node.Elem
        override fun previousIndex(): Int = if (hasPrevious()) index - 1 else -1
        override fun previous(): T {
            if (!hasPrevious()) throw NoSuchElementException("No previous element in list")
            cur = cur.prev
            return (cur as Node.Elem).elem // we know this is safe because we didn't throw
        }
        override fun hasNext(): Boolean = cur.next is Node.Elem
        override fun nextIndex(): Int = if (hasNext()) index + 1 else -1
        override fun next(): T {
            if (!hasNext()) throw NoSuchElementException("No next element in list")
            cur = cur.next
            return (cur as Node.Elem).elem
        }
    }
}