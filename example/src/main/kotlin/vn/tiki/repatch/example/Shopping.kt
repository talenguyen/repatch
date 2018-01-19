package vn.tiki.repatch.example

import vn.tiki.repatch.Reducer
import vn.tiki.repatch.Store

// Model
data class Product(val id: Int, val name: String, val price: Float)

data class CartItem(val product: Product, val quantity: Int)

data class Cart(val items: List<CartItem> = emptyList(), val quantity: Int = 0, val total: Float = 0f)

data class ShoppingState(val products: List<Product>, val cart: Cart)

// View
fun divider() = "\n--------------------\n"
fun doubbleDivider() = "\n====================\n"

fun productView(product: Product): String = """
      || ${product.name}
      || $${product.price}
      """.trimMargin()

fun productListView(products: List<Product>) = products.joinToString(divider(), divider(), divider()) { productView(it) }

fun cartItemView(cartItem: CartItem) = "(${cartItem.quantity}) ${cartItem.product.name} - ${cartItem.product.price}"

fun cartView(cart: Cart): String = """
  |Cart
  |
  | ${cart.items.joinToString("\n| ", "| ") { cartItemView(it) }}
  |
  | Total (${cart.quantity}): ${'$'}${cart.total}
""".trimMargin()

// App
val products = listOf(
  Product(1, "Football", 49.99f),
  Product(2, "Baseball", 9.99f),
  Product(3, "Basketball", 29.99f)
)

val cartComputing = Reducer<ShoppingState> { state ->
  val cart = state.cart
  val quantity = if (cart.items.isNotEmpty())
    cart.items
      .map { it.quantity }
      .reduce { acc, quantity -> acc + quantity }
  else 0

  val total = if (cart.items.isNotEmpty())
    cart.items
      .map { it.product.price * it.quantity }
      .reduce { acc, price -> acc + price }
  else 0f

  state.copy(cart = cart.copy(
    quantity = quantity,
    total = total))
}

val store = Store(ShoppingState(products, Cart()), cartComputing)

fun appView(state: ShoppingState) = """
  | ${doubbleDivider()}
  | Shopping Demo
  | ${productListView(state.products)}
  | ${cartView(state.cart)}
  """.trimMargin()

val addToCart: (Product) -> Reducer<ShoppingState> = { product ->
  Reducer { state ->
    val cart = state.cart
    val item = cart
      .items
      .firstOrNull { it.product == product }
    val items = when (item) {
      null -> {
        val cartItem = CartItem(product, 1)
        cart.items.push(cartItem)
      }
      else -> cart.items
        .map { cartItem ->
          if (cartItem == item) {
            cartItem.copy(quantity = cartItem.quantity + 1)
          } else cartItem
        }
    }
    state.copy(cart = cart.copy(items = items))
  }
}

fun main(args: Array<String>) {
  store.subscribe { println(appView(it)) }

  store.dispatch(addToCart(products[0]))
  store.dispatch(addToCart(products[0]))
  store.dispatch(addToCart(products[1]))
  store.dispatch(addToCart(products[2]))
}