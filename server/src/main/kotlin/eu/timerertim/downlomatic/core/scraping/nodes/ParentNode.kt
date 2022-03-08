package eu.timerertim.downlomatic.core.scraping.nodes


sealed interface ParentNode {
    fun addChild(other: ChildNode)
}


