package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.Order
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PrimaryGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PaymentReceiptScreen(
  order: Order,
  onBackToCatalog: () -> Unit,
  onViewOrderHistory: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val scrollState = rememberScrollState()
  val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
  val formattedDate = dateFormat.format(Date(order.timestamp))

  BackHandler {
    onBackToCatalog()
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Top Bar
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
            modifier = Modifier.testTag("receipt_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Volver a la Tienda"
            )
          }
          Text(
            text = "Comprobante de Pago QR",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
        }

        IconButton(
          onClick = {
            val shareText = """
              🧾 Comprobante de Pago QRPay
              Orden: ${order.orderNumber}
              Fecha: $formattedDate
              Total Pagado: $${String.format(Locale.US, "%.2f", order.total)}
              Método: ${order.paymentMethod.title}
              Referencia: ${order.paymentRef}
              Estado: Acreditado con Código QR
            """.trimIndent()
            val intent = Intent(Intent.ACTION_SEND).apply {
              type = "text/plain"
              putExtra(Intent.EXTRA_SUBJECT, "Comprobante de Pago ${order.orderNumber}")
              putExtra(Intent.EXTRA_TEXT, shareText)
            }
            context.startActivity(Intent.createChooser(intent, "Compartir Comprobante"))
          },
          modifier = Modifier.testTag("share_receipt_button")
        ) {
          Icon(Icons.Default.Share, contentDescription = "Compartir Comprobante")
        }
      }
    }

    Column(
      modifier = Modifier
        .weight(1f)
        .verticalScroll(scrollState)
        .padding(horizontal = 16.dp, vertical = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Success Hero Illustration Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Success badge
          Box(
            modifier = Modifier
              .size(80.dp)
              .clip(CircleShape)
              .background(PrimaryGreen.copy(alpha = 0.15f))
              .border(2.dp, PrimaryGreen, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = "Éxito",
              tint = PrimaryGreen,
              modifier = Modifier.size(48.dp)
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          Text(
            text = "¡Pago Realizado con Éxito!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Black,
            color = PrimaryGreen,
            textAlign = TextAlign.Center
          )

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = "Tu transacción fue aprobada en la pasarela QR.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
          )

          Spacer(modifier = Modifier.height(10.dp))

          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
          ) {
            Text(
              text = "Orden: ${order.orderNumber}",
              style = MaterialTheme.typography.labelLarge,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onPrimaryContainer,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Digital Ticket Receipt Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("digital_receipt_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(3.dp)
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          // Store branding header
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "QRPAY STORE OFICIAL",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black
              )
              Text(
                text = "RUC / Tax ID: 20608920191",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            Icon(
              imageVector = Icons.Default.QrCode2,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(32.dp)
            )
          }

          Spacer(modifier = Modifier.height(12.dp))
          HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
          Spacer(modifier = Modifier.height(12.dp))

          // Key-Value rows
          ReceiptRow(label = "Fecha y Hora", value = formattedDate)
          ReceiptRow(label = "Método de Pago", value = "${order.paymentMethod.iconEmoji} ${order.paymentMethod.title}")
          ReceiptRow(label = "Ref. Transacción", value = order.paymentRef)
          ReceiptRow(label = "Cliente", value = order.customerName)
          ReceiptRow(label = "Teléfono", value = order.customerPhone)
          ReceiptRow(label = "Destino", value = order.deliveryAddress)

          Spacer(modifier = Modifier.height(12.dp))
          HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
          Spacer(modifier = Modifier.height(12.dp))

          // Items List
          Text(
            text = "Detalle de Productos",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(8.dp))

          order.items.forEach { item ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "${item.quantity}x ${item.product.name}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f)
              )
              Text(
                text = "$${String.format(Locale.US, "%.2f", item.totalPrice)}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))
          HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
          Spacer(modifier = Modifier.height(12.dp))

          // Financial breakdown
          ReceiptRow(label = "Subtotal", value = "$${String.format(Locale.US, "%.2f", order.subtotal)}")
          if (order.discount > 0) {
            ReceiptRow(
              label = "Descuento (${order.couponCode ?: "PROMO"})",
              value = "-$${String.format(Locale.US, "%.2f", order.discount)}",
              valueColor = PrimaryGreen
            )
          }
          ReceiptRow(
            label = "Envío",
            value = if (order.shippingFee == 0.0) "GRATIS" else "$${String.format(Locale.US, "%.2f", order.shippingFee)}"
          )
          ReceiptRow(label = "Impuestos (IGV 18%)", value = "$${String.format(Locale.US, "%.2f", order.tax)}")

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "TOTAL PAGADO",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Black
            )
            Text(
              text = "$${String.format(Locale.US, "%.2f", order.total)}",
              style = MaterialTheme.typography.headlineSmall,
              fontWeight = FontWeight.Black,
              color = MaterialTheme.colorScheme.primary
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Bottom Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedButton(
          onClick = onViewOrderHistory,
          modifier = Modifier
            .weight(1f)
            .height(50.dp)
            .testTag("view_orders_button"),
          shape = RoundedCornerShape(14.dp)
        ) {
          Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Mis Pedidos")
        }

        Button(
          onClick = onBackToCatalog,
          modifier = Modifier
            .weight(1f)
            .height(50.dp)
            .testTag("continue_shopping_button"),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryGreen,
            contentColor = Color.Black
          )
        ) {
          Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Seguir Comprando", fontWeight = FontWeight.Bold)
        }
      }

      Spacer(modifier = Modifier.height(40.dp))
    }
  }
}

@Composable
private fun ReceiptRow(
  label: String,
  value: String,
  valueColor: Color = Color.Unspecified
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 2.dp),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Text(
      text = value,
      style = MaterialTheme.typography.bodySmall,
      fontWeight = FontWeight.SemiBold,
      color = if (valueColor != Color.Unspecified) valueColor else MaterialTheme.colorScheme.onSurface
    )
  }
}
