package com.example.model

enum class PaymentMethod(
  val id: String,
  val title: String,
  val subtitle: String,
  val iconEmoji: String,
  val badgeColorHex: Long,
  val defaultAccount: String
) {
  YAPE_PLIN(
    id = "yape_plin",
    title = "Yape / Plin",
    subtitle = "Transferencia instantánea sin comisiones",
    iconEmoji = "📱",
    badgeColorHex = 0xFF7000FF,
    defaultAccount = "+51 987 654 321 (QRPay Store SAC)"
  ),
  MERCADO_PAGO(
    id = "mercado_pago",
    title = "Mercado Pago QR",
    subtitle = "Escanea con app Mercado Pago o billeteras",
    iconEmoji = "💳",
    badgeColorHex = 0xFF009EE3,
    defaultAccount = "tienda.qrpay.mp"
  ),
  BANCO_QR(
    id = "banco_qr",
    title = "Transferencia Interbancaria QR",
    subtitle = "Compatible con BCP, BBVA, Interbank y Scotiabank",
    iconEmoji = "🏦",
    badgeColorHex = 0xFF00873B,
    defaultAccount = "CCI: 002-194-000123456789-90"
  ),
  CRYPTO_LIGHTNING(
    id = "crypto_qr",
    title = "Bitcoin Lightning / USDT",
    subtitle = "Pago rápido Web3 instantáneo con cero fee",
    iconEmoji = "⚡",
    badgeColorHex = 0xFFF59E0B,
    defaultAccount = "lnbc1qrpaystorefastpay001"
  )
}

enum class PaymentStatus(
  val label: String,
  val description: String
) {
  PENDING("Esperando escaneo", "Abre tu aplicación de pagos y enfoca el código QR."),
  SCAN_DETECTED("¡QR Escaneado!", "Detectamos la apertura de tu app bancaria."),
  VERIFYING("Verificando fondos...", "Validando comprobante de pago en el gateway."),
  SUCCESS("¡Pago Aprobado!", "Tu compra ha sido acreditada exitosamente."),
  EXPIRED("Código QR Expirado", "El tiempo límite de 10 minutos se ha agotado."),
  CANCELLED("Pago Cancelado", "Has cancelado la operación.")
}

data class Coupon(
  val code: String,
  val discountPercent: Int,
  val description: String,
  val minSpend: Double = 0.0
)

data class Order(
  val id: String,
  val orderNumber: String,
  val timestamp: Long,
  val items: List<CartItem>,
  val subtotal: Double,
  val discount: Double,
  val couponCode: String?,
  val shippingFee: Double,
  val tax: Double,
  val total: Double,
  val paymentMethod: PaymentMethod,
  val paymentRef: String,
  val customerName: String,
  val customerPhone: String,
  val deliveryAddress: String,
  val status: PaymentStatus = PaymentStatus.SUCCESS
)

data class PaymentSession(
  val orderNumber: String,
  val amount: Double,
  val method: PaymentMethod,
  val qrPayload: String,
  val referenceNumber: String,
  val merchantName: String = "QRPay Store Oficial",
  val createdAt: Long = System.currentTimeMillis(),
  val timeoutSeconds: Int = 600, // 10 minutes
  val status: PaymentStatus = PaymentStatus.PENDING
)
