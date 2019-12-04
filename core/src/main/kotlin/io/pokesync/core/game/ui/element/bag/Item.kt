package io.pokesync.core.game.ui.element.bag

/**
 * An item.
 * @author Sino
 */
data class Item(val id: Int, var quantity: Int) {
    init {
        require(quantity >= 0) { "Illegal quantity of $quantity" }
    }
}