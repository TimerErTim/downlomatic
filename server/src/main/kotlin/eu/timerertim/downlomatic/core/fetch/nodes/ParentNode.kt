package eu.timerertim.downlomatic.core.fetch.nodes


sealed interface ParentNode {
    fun addChild(other: ChildNode)
}


