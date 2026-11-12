package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ProductCatalog
import com.example.model.CartItem
import com.example.model.Coupon
import com.example.model.Order
import com.example.model.PaymentMethod
import com.example.model.PaymentSession
import com.example.model.PaymentStatus
import com.example.model.Product
import com.example.model.ProductCategory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

sealed class Screen {
  data object Catalog : Screen()
  data object Cart : Screen()
  data object QrGateway : Screen()
  data class Receipt(val order: Order) : Screen()
  data object OrderHistory : Screen()
}

enum class SortOption(val label: String) {
  POPULAR("Más Populares"),
  PRICE_ASC("Precio: Menor a Mayor"),
  PRICE_DESC("Precio: Mayor a Menor"),
  RATING("Mejor Calificados")
}

data class CustomerInfo(
  val name: String = "Percy Alexis",
  val phone: String = "+51 987 123 456",
  val address: String = "Av. Javier Prado Este 2450, San Borja, Lima",
  val isDelivery: Boolean = true,
  val notes: String = ""
)

data class ShopUiState(
  val products: List<Product> = ProductCatalog.SAMPLE_PRODUCTS,
  val selectedCategory: ProductCategory = ProductCategory.TODOS,
  val searchQuery: String = "",
  val sortOption: SortOption = SortOption.POPULAR,
  val selectedProductForDetail: Product? = null,
  val cartItems: List<CartItem> = listOf(
    // Start with 2 sample items in cart for instant user satisfaction
    CartItem(ProductCatalog.SAMPLE_PRODUCTS[0], 1),
    CartItem(ProductCatalog.SAMPLE_PRODUCTS[4], 1)
  ),
  val appliedCoupon: Coupon? = ProductCatalog.AVAILABLE_COUPONS[0], // QRPAY15 default applied!
  val couponErrorMessage: String? = null,
  val customerInfo: CustomerInfo = CustomerInfo(),
  val activePaymentSession: PaymentSession? = null,
  val selectedPaymentMethod: PaymentMethod = PaymentMethod.YAPE_PLIN,
  val remainingSeconds: Int = 600,
  val orders: List<Order> = emptyList(),
  val currentScreen: Screen = Screen.Catalog,
  val snackbarMessage: String? = null,
  val isVerifyingPayment: Boolean = false
) {
  val totalItemsCount: Int get() = cartItems.sumOf { it.quantity }

  val subtotal: Double get() = cartItems.sumOf { it.totalPrice }

  val discountAmount: Double
    get() {
      val coupon = appliedCoupon ?: return 0.0
      if (subtotal < coupon.minSpend) return 0.0
      return subtotal * (coupon.discountPercent / 100.0)
    }

  val shippingFee: Double
    get() {
      if (cartItems.isEmpty() || !customerInfo.isDelivery) return 0.0
      return if (subtotal >= 100.0) 0.0 else 9.90
    }

  val taxAmount: Double
    get() {
      // 18% IGV / Tax calculated on net subtotal after discount
      val taxable = (subtotal - discountAmount).coerceAtLeast(0.0)
      return taxable * 0.18
    }

  val grandTotal: Double
    get() {
      val afterDiscount = (subtotal - discountAmount).coerceAtLeast(0.0)
      return afterDiscount + shippingFee + taxAmount
    }
}

class ShopViewModel : ViewModel() {

  private val _uiState = MutableStateFlow(ShopUiState())
  val uiState: StateFlow<ShopUiState> = _uiState.asStateFlow()

  private var timerJob: Job? = null
  private var simulationJob: Job? = null

  fun setScreen(screen: Screen) {
    _uiState.update { it.copy(currentScreen = screen) }
  }

  fun selectCategory(category: ProductCategory) {
    _uiState.update { it.copy(selectedCategory = category) }
  }

  fun setSearchQuery(query: String) {
    _uiState.update { it.copy(searchQuery = query) }
  }

  fun setSortOption(option: SortOption) {
    _uiState.update { it.copy(sortOption = option) }
  }

  fun openProductDetail(product: Product) {
    _uiState.update { it.copy(selectedProductForDetail = product) }
  }

  fun dismissProductDetail() {
    _uiState.update { it.copy(selectedProductForDetail = null) }
  }

  fun addToCart(product: Product, quantity: Int = 1) {
    _uiState.update { state ->
      val existingIndex = state.cartItems.indexOfFirst { it.product.id == product.id }
      val updated = if (existingIndex >= 0) {
        state.cartItems.mapIndexed { idx, item ->
          if (idx == existingIndex) item.copy(quantity = item.quantity + quantity) else item
        }
      } else {
        state.cartItems + CartItem(product, quantity)
      }
      state.copy(
        cartItems = updated,
        snackbarMessage = "Añadido: ${product.name} (x$quantity)"
      )
    }
  }

  fun updateCartQuantity(productId: String, delta: Int) {
    _uiState.update { state ->
      val updated = state.cartItems.mapNotNull { item ->
        if (item.product.id == productId) {
          val newQty = item.quantity + delta
          if (newQty > 0) item.copy(quantity = newQty) else null
        } else {
          item
        }
      }
      state.copy(cartItems = updated)
    }
  }

  fun removeFromCart(productId: String) {
    _uiState.update { state ->
      val removedItem = state.cartItems.find { it.product.id == productId }
      val updated = state.cartItems.filterNot { it.product.id == productId }
      state.copy(
        cartItems = updated,
        snackbarMessage = removedItem?.let { "Eliminado: ${it.product.name}" }
      )
    }
  }

  fun clearCart() {
    _uiState.update { it.copy(cartItems = emptyList()) }
  }

  fun applyCoupon(code: String) {
    val clean = code.trim().uppercase()
    val match = ProductCatalog.AVAILABLE_COUPONS.find { it.code == clean }
    if (match != null) {
      if (_uiState.value.subtotal >= match.minSpend) {
        _uiState.update {
          it.copy(
            appliedCoupon = match,
            couponErrorMessage = null,
            snackbarMessage = "¡Cupón ${match.code} aplicado con éxito (-${match.discountPercent}%)!"
          )
        }
      } else {
        _uiState.update {
          it.copy(
            couponErrorMessage = "Monto mínimo requerido: $${String.format(Locale.US, "%.2f", match.minSpend)}"
          )
        }
      }
    } else {
      _uiState.update {
        it.copy(couponErrorMessage = "Cupón no válido o vencido.")
      }
    }
  }

  fun removeCoupon() {
    _uiState.update { it.copy(appliedCoupon = null, couponErrorMessage = null) }
  }

  fun updateCustomerInfo(info: CustomerInfo) {
    _uiState.update { it.copy(customerInfo = info) }
  }

  fun clearSnackbar() {
    _uiState.update { it.copy(snackbarMessage = null) }
  }

  // --- QR Payment Gateway logic ---

  fun startCheckoutWithQr() {
    val state = _uiState.value
    if (state.cartItems.isEmpty()) return

    val orderRef = "QRP-" + (100000..999999).random()
    val method = state.selectedPaymentMethod
    val total = state.grandTotal

    val payload = generateQrPayload(method, total, orderRef)

    val session = PaymentSession(
      orderNumber = orderRef,
      amount = total,
      method = method,
      qrPayload = payload,
      referenceNumber = "REF-" + UUID.randomUUID().toString().take(8).uppercase(),
      status = PaymentStatus.PENDING
    )

    _uiState.update {
      it.copy(
        activePaymentSession = session,
        remainingSeconds = 600,
        isVerifyingPayment = false,
        currentScreen = Screen.QrGateway
      )
    }

    startCountdownTimer()
  }

  fun selectPaymentMethod(method: PaymentMethod) {
    _uiState.update { state ->
      val currentSession = state.activePaymentSession
      if (currentSession != null) {
        val updatedPayload = generateQrPayload(method, currentSession.amount, currentSession.orderNumber)
        state.copy(
          selectedPaymentMethod = method,
          activePaymentSession = currentSession.copy(
            method = method,
            qrPayload = updatedPayload
          )
        )
      } else {
        state.copy(selectedPaymentMethod = method)
      }
    }
  }

  private fun generateQrPayload(method: PaymentMethod, amount: Double, orderRef: String): String {
    val formattedAmount = String.format(Locale.US, "%.2f", amount)
    return when (method) {
      PaymentMethod.YAPE_PLIN -> {
        // Standard Yape QR payload format
        "yape://pay?to=987654321&amount=$formattedAmount&concept=Orden_$orderRef&merchant=QRPayStore"
      }
      PaymentMethod.MERCADO_PAGO -> {
        "https://mpago.la/qr/checkout?ref=$orderRef&amt=$formattedAmount&curr=USD"
      }
      PaymentMethod.BANCO_QR -> {
        "00020101021226580016com.qrpay.banco5204411153036045407${formattedAmount}5802PE5915QRPAY_STORE6004LIMA62170513${orderRef}6304"
      }
      PaymentMethod.CRYPTO_LIGHTNING -> {
        "lightning:lnbc${formattedAmount.replace(".", "")}u1p3qrpaystorepp5xyz...order=$orderRef"
      }
    }
  }

  private fun startCountdownTimer() {
    timerJob?.cancel()
    timerJob = viewModelScope.launch {
      while (true) {
        delay(1000)
        val current = _uiState.value.remainingSeconds
        if (current <= 1) {
          _uiState.update {
            it.copy(
              remainingSeconds = 0,
              activePaymentSession = it.activePaymentSession?.copy(status = PaymentStatus.EXPIRED)
            )
          }
          break
        } else {
          _uiState.update { it.copy(remainingSeconds = current - 1) }
        }
      }
    }
  }

  fun refreshQr() {
    startCheckoutWithQr()
  }

  /**
   * Simulates full payment verification pipeline:
   * 1. SCAN_DETECTED (instant feedback that QR camera was focused)
   * 2. VERIFYING (contacting bank network / blockchain confirmation)
   * 3. SUCCESS (Order created, cart cleared, navigate to digital ticket receipt)
   */
  fun simulateScanAndPay() {
    if (_uiState.value.isVerifyingPayment) return

    simulationJob?.cancel()
    simulationJob = viewModelScope.launch {
      _uiState.update {
        it.copy(
          isVerifyingPayment = true,
          activePaymentSession = it.activePaymentSession?.copy(status = PaymentStatus.SCAN_DETECTED)
        )
      }
      delay(1200)

      _uiState.update {
        it.copy(
          activePaymentSession = it.activePaymentSession?.copy(status = PaymentStatus.VERIFYING)
        )
      }
      delay(1600)

      val state = _uiState.value
      val session = state.activePaymentSession ?: return@launch

      val order = Order(
        id = UUID.randomUUID().toString(),
        orderNumber = session.orderNumber,
        timestamp = System.currentTimeMillis(),
        items = state.cartItems,
        subtotal = state.subtotal,
        discount = state.discountAmount,
        couponCode = state.appliedCoupon?.code,
        shippingFee = state.shippingFee,
        tax = state.taxAmount,
        total = state.grandTotal,
        paymentMethod = session.method,
        paymentRef = session.referenceNumber,
        customerName = state.customerInfo.name,
        customerPhone = state.customerInfo.phone,
        deliveryAddress = if (state.customerInfo.isDelivery) state.customerInfo.address else "Recojo en Tienda Central",
        status = PaymentStatus.SUCCESS
      )

      timerJob?.cancel()

      _uiState.update {
        it.copy(
          isVerifyingPayment = false,
          activePaymentSession = session.copy(status = PaymentStatus.SUCCESS),
          cartItems = emptyList(), // Cart fulfilled!
          orders = listOf(order) + it.orders,
          currentScreen = Screen.Receipt(order)
        )
      }
    }
  }

  override fun onCleared() {
    super.onCleared()
    timerJob?.cancel()
    simulationJob?.cancel()
  }
}
