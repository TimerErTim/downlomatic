package eu.timerertim.downlomatic.graphics.component.util

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import eu.timerertim.downlomatic.graphics.window.sdp

@Composable
fun <T> TreeList(
    root: Node<T>,
    modifier: Modifier = Modifier,
    renderer: @Composable (T, Boolean) -> Unit
) {
    Box(modifier) {
        TreeNodeContent(root, renderer)
    }
}

@Composable
private fun <T> TreeNodeBlock(
    node: TreeNode<T>,
    renderer: @Composable (T, Boolean) -> Unit
) {
    CollapsableBlock(expanded = node.expanded, onExpandedChange = {
        node.expanded = it
    }, trailingLabel = {
        renderer(node.value, it)
    }) {
        Box(modifier = Modifier.padding(start = 20.sdp)) {
            TreeNodeContent(node, renderer)
        }
    }
}

@Composable
private fun <T> TreeNodeContent(
    node: Node<T>,
    renderer: @Composable (T, Boolean) -> Unit
) {
    Column {
        node.children.forEach {
            TreeNodeBlock(it, renderer)
        }

        Column(modifier = Modifier.padding(start = 20.sdp)) {
            node.leafs.forEach {
                renderer(it, false)
            }
        }
    }
}

sealed class Node<T> {
    val children = mutableStateListOf<TreeNode<T>>()
    val leafs = mutableStateListOf<T>()

    abstract fun filter(predicate: (T) -> Boolean): Node<T>
}

class TreeNode<T> private constructor(
    val value: T,
    private val expandedState: MutableState<Boolean>,
    children: List<TreeNode<T>> = emptyList(),
    leafs: List<T> = emptyList()
) : Node<T>() {
    constructor(value: T) : this(value, mutableStateOf(false))

    init {
        this.children += children
        this.leafs += leafs
    }

    var expanded by expandedState

    override fun filter(predicate: (T) -> Boolean): TreeNode<T> {
        if (predicate(value)) return this.copy()

        val newChildren = children.map {
            it.filter(predicate)
        }.filter { it.leafs.isNotEmpty() || it.children.isNotEmpty() }
        val newLeafs = leafs.filter(predicate)

        return TreeNode(value, expandedState, newChildren, newLeafs)
    }

    fun copy(value: T = this.value) = TreeNode(value, expandedState, children, leafs)
}
