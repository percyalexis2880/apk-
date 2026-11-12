package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ProductCatalog
import com.example.ui.ShopUiState
import com.example.ui.ShopViewModel
import com.example.ui.components.CartItemRow
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PrimaryGreen
import java.util.Locale

@Composable
fun CartScreen(
  viewModel: ShopViewModel,
  uiState: ShopUiState,
  onBackToCatalog: () -> Unit,
  onProceedToQrPayment: () -> Unit,
  modifier: Modifier = Modifier
) {
  var couponInput by remember { mutableStateOf("") }
  var showCustomerEditDialog by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Header
    Surface(
      color = MaterialTheme.colorScheme.surface,
      shadowElevation = 2.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = onBackToCatalog,
            modifier = Modifier.testTag("cart_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Volver al Catálogo"
            )
          }
          Column {
            Text(
              text = "Mi Carrito de Compras",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "${uiState.totalItemsCount} productos seleccionados",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        if (uiState.cartItems.isNotEmpty()) {
          IconButton(
            onClick = { viewModel.clearCart() },
            modifier = Modifier.testTag("clear_cart_button")
          ) {
            Icon(
              imageVector = Icons.Default.DeleteSweep,
              contentDescription = "Vaciar Carrito",
              tint = MaterialTheme.colorScheme.error
            )
          }
        }
      }
    }

    if (uiState.cartItems.isEmpty()) {
      // Empty Cart View
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(24.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Box(
            modifier = Modifier
              .size(96.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.ShoppingBag,
              contentDescription = null,
              modifier = Modifier.size(48.dp),
              tint = MaterialTheme.colorScheme.primary
            )
          }

          Spacer(modifier = Modifier.height(18.dp))

          Text(
            text = "Tu carrito está vacío",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
          )

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "Explora nuestro catálogo y agrega los productos que desees pagar con QR.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 24.dp),
            lineHeight = 20.sp
          )

          Spacer(modifier = Modifier.height(24.dp))

          Button(
            onClick = onBackToCatalog,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .height(48.dp)
              .testTag("start_shopping_button")
          ) {
            Text("Explorar Catálogo", fontWeight = FontWeight.Bold)
          }
        }
      }
    } else {
      // Cart Content with items and checkout summaries
      LazyColumn(
        modifier = Modifier
          .weight(1f)
          .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // Free Shipping Progress
        item {
          val freeShippingThreshold = 100.0
          val progress = (uiState.subtotal / freeShippingThreshold).toFloat().coerceIn(0f, 1f)
          val remaining = (freeShippingThreshold - uiState.subtotal).coerceAtLeast(0.0)

          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            )
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.LocalShipping,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = if (remaining <= 0.0) "¡Envío Gratis desbloqueado!" else "Envío Gratis por compras > $100",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                  )
                }
                if (remaining > 0.0) {
                  Text(
                    text = "Faltan $${String.format(Locale.US, "%.2f", remaining)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                  )
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(6.dp)
                  .clip(RoundedCornerShape(4.dp)),
                color = PrimaryGreen,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
              )
            }
          }
        }

        // Cart Items List
        items(uiState.cartItems, key = { it.product.id }) { item ->
          CartItemRow(
            cartItem = item,
            onIncrement = { viewModel.updateCartQuantity(item.product.id, 1) },
            onDecrement = { viewModel.updateCartQuantity(item.product.id, -1) },
            onRemove = { viewModel.removeFromCart(item.product.id) }
          )
        }

        // Coupon Code Section
        item {
          Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(1.dp)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.LocalOffer,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Cupón de Descuento",
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Bold
                )
              }

              Spacer(modifier = Modifier.height(10.dp))

              if (uiState.appliedCoupon != null) {
                // Applied coupon badge
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(PrimaryGreen.copy(alpha = 0.15f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.CheckCircle,
                      contentDescription = null,
                      tint = PrimaryGreen,
                      modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                      Text(
                        text = "${uiState.appliedCoupon.code} (-${uiState.appliedCoupon.discountPercent}%)",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                      )
                      Text(
                        text = "Ahorras $${String.format(Locale.US, "%.2f", uiState.discountAmount)}",
                        fontSize = 11.sp,
                        color = PrimaryGreen
                      )
                    }
                  }

                  IconButton(
                    onClick = { viewModel.removeCoupon() },
                    modifier = Modifier.size(32.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.Close,
                      contentDescription = "Quitar Cupón",
                      tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }
              } else {
                // Input field
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  OutlinedTextField(
                    value = couponInput,
                    onValueChange = { couponInput = it },
                    placeholder = { Text("Ej: QRPAY15", fontSize = 13.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                      focusedBorderColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                      .weight(1f)
                      .height(52.dp)
                      .testTag("coupon_input")
                  )

                  Spacer(modifier = Modifier.width(8.dp))

                  Button(
                    onClick = {
                      viewModel.applyCoupon(couponInput)
                      couponInput = ""
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                      .height(52.dp)
                      .testTag("apply_coupon_btn")
                  ) {
                    Text("Aplicar", fontWeight = FontWeight.Bold)
                  }
                }

                // Quick Suggestion Chips
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                  horizontalArrangement = Arrangement.spacedBy(6.dp),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  ProductCatalog.AVAILABLE_COUPONS.forEach { coupon ->
                    Surface(
                      shape = RoundedCornerShape(8.dp),
                      color = MaterialTheme.colorScheme.surfaceVariant,
                      modifier = Modifier
                        .clickable { viewModel.applyCoupon(coupon.code) }
                    ) {
                      Text(
                        text = "${coupon.code} (-${coupon.discountPercent}%)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                      )
                    }
                  }
                }
              }

              if (uiState.couponErrorMessage != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = uiState.couponErrorMessage,
                  color = MaterialTheme.colorScheme.error,
                  fontSize = 12.sp
                )
              }
            }
          }
        }

        // Delivery & Contact Info
        item {
          Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(1.dp)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = if (uiState.customerInfo.isDelivery) Icons.Default.LocalShipping else Icons.Default.Store,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = if (uiState.customerInfo.isDelivery) "Entrega a Domicilio" else "Recojo en Tienda",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                  )
                }

                Switch(
                  checked = uiState.customerInfo.isDelivery,
                  onCheckedChange = { isDeliv ->
                    viewModel.updateCustomerInfo(uiState.customerInfo.copy(isDelivery = isDeliv))
                  }
                )
              }

              Spacer(modifier = Modifier.height(6.dp))

              Text(
                text = "Cliente: ${uiState.customerInfo.name} (${uiState.customerInfo.phone})",
                style = MaterialTheme.typography.bodySmall
              )
              if (uiState.customerInfo.isDelivery) {
                Text(
                  text = "Dirección: ${uiState.customerInfo.address}",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }

        // Financial Breakdown Summary
        item {
          Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(1.dp)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text(
                text = "Resumen de Compra",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )

              Spacer(modifier = Modifier.height(12.dp))

              // Subtotal
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(text = "Subtotal (${uiState.totalItemsCount} productos)", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = "$${String.format(Locale.US, "%.2f", uiState.subtotal)}", fontWeight = FontWeight.SemiBold)
              }

              // Discount if any
              if (uiState.discountAmount > 0.0) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(text = "Descuento cupón (${uiState.appliedCoupon?.code})", color = PrimaryGreen)
                  Text(text = "-$${String.format(Locale.US, "%.2f", uiState.discountAmount)}", color = PrimaryGreen, fontWeight = FontWeight.Bold)
                }
              }

              // Shipping
              Spacer(modifier = Modifier.height(6.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(text = "Costo de Envío", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                  text = if (uiState.shippingFee == 0.0) "GRATIS" else "$${String.format(Locale.US, "%.2f", uiState.shippingFee)}",
                  fontWeight = FontWeight.SemiBold,
                  color = if (uiState.shippingFee == 0.0) PrimaryGreen else MaterialTheme.colorScheme.onSurface
                )
              }

              // Taxes
              Spacer(modifier = Modifier.height(6.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(text = "Impuestos (IGV 18%)", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = "$${String.format(Locale.US, "%.2f", uiState.taxAmount)}", fontWeight = FontWeight.SemiBold)
              }

              Spacer(modifier = Modifier.height(10.dp))
              HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
              Spacer(modifier = Modifier.height(10.dp))

              // Grand Total
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Total a Pagar",
                  style = MaterialTheme.typography.titleLarge,
                  fontWeight = FontWeight.Black
                )
                Text(
                  text = "$${String.format(Locale.US, "%.2f", uiState.grandTotal)}",
                  style = MaterialTheme.typography.headlineSmall,
                  fontWeight = FontWeight.Black,
                  color = MaterialTheme.colorScheme.primary
                )
              }
            }
          }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
      }

      // Sticky Bottom Checkout Bar
      Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 12.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Pasarela de pagos encriptada con código QR dinámico",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Button(
            onClick = onProceedToQrPayment,
            modifier = Modifier
              .fillMaxWidth()
              .height(54.dp)
              .testTag("proceed_to_qr_checkout_btn"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = PrimaryGreen,
              contentColor = Color.Black
            )
          ) {
            Icon(
              imageVector = Icons.Default.QrCode2,
              contentDescription = null,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "Pagar con QR • $${String.format(Locale.US, "%.2f", uiState.grandTotal)}",
              fontSize = 16.sp,
              fontWeight = FontWeight.Black
            )
          }
        }
      }
    }
  }
}
