package eu.timerertim.downlomatic.core.fetch.nodes

sealed interface ChildNode {
    fun attachTo(other: ParentNode) = other.addChild(this)
}