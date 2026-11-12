package com.example.model

data class Product(
  val id: String,
  val name: String,
  val subtitle: String,
  val category: ProductCategory,
  val price: Double,
  val originalPrice: Double? = null,
  val rating: Float,
  val reviewsCount: Int,
  val description: String,
  val specs: List<Pair<String, String>>,
  val iconName: String,
  val tags: List<String> = emptyList(),
  val inStock: Boolean = true,
  val stockUnits: Int = 12
)

enum class ProductCategory(val displayName: String, val icon: String) {
  TODOS("Todos", "Storefront"),
  TECNOLOGIA("Tecnología", "Devices"),
  AUDIO_GAMING("Audio & Gaming", "Headphones"),
  HOGAR_CAFE("Hogar & Café", "Coffee"),
  ACCESORIOS("Accesorios", "Watch")
}

data class CartItem(
  val product: Product,
  val quantity: Int = 1
) {
  val totalPrice: Double get() = product.price * quantity
}
