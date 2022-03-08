package eu.timerertim.downlomatic.core.scraping.nodes

sealed interface ChildNode {
    fun attachTo(other: ParentNode) = other.addChild(this)
}
