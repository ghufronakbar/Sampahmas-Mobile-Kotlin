package com.example.sampahmasgabungan

object WishlistData {
    val wishlist: MutableList<ProductVariable> = mutableListOf()

    fun addToWishlist(item: ProductVariable) {
        wishlist.add(item)
    }

    fun removeFromWishlist(item: ProductVariable) {
        wishlist.remove(item)
    }
}
