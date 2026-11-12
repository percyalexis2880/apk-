package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PaymentMethod
import com.example.model.PaymentStatus
import com.example.ui.ShopUiState
import com.example.ui.ShopViewModel
import com.example.ui.components.QrCodeView
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CoralSale
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PrimaryGreen
import java.util.Locale

@Composable
fun QRPaymentGatewayScreen(
  viewModel: ShopViewModel,
  uiState: ShopUiState,
  onBackToCart: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val session = uiState.activePaymentSession
  val scrollState = rememberScrollState()

  // Handle hardware back button
  BackHandler {
    onBackToCart()
  }

  // Pulsing animation for waiting status
  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.95f,
    targetValue = 1.05f,
    animationSpec = infiniteRepeatable(
      animation = tween(900, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_scale"
  )

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
            onClick = onBackToCart,
            modifier = Modifier.testTag("qr_gateway_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Volver al Carrito"
            )
          }
          Column {
            Text(
              text = "Pasarela de Pagos QR",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = session?.orderNumber ?: "Orden de Compra",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.primary,
              fontWeight = FontWeight.Bold
            )
          }
        }

        IconButton(
          onClick = { viewModel.refreshQr() },
          modifier = Modifier.testTag("refresh_qr_button")
        ) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Regenerar QR",
            tint = MaterialTheme.colorScheme.onSurface
          )
        }
      }
    }

    Column(
      modifier = Modifier
        .weight(1f)
        .verticalScroll(scrollState)
        .padding(horizontal = 16.dp, vertical = 12.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Amount To Pay Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "Total a Pagar",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = "$${String.format(Locale.US, "%.2f", uiState.grandTotal)}",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Countdown Timer Pill
          val minutes = uiState.remainingSeconds / 60
          val seconds = uiState.remainingSeconds % 60
          val isTimeRunningOut = uiState.remainingSeconds < 60

          Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (isTimeRunningOut) CoralSale.copy(alpha = 0.15f) else PrimaryGreen.copy(alpha = 0.15f)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = null,
                tint = if (isTimeRunningOut) CoralSale else PrimaryGreen,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Expira en ${String.format(Locale.US, "%02d:%02d", minutes, seconds)}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isTimeRunningOut) CoralSale else PrimaryGreen
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Provider Selector Tabs
      Text(
        text = "Selecciona tu billetera o método de pago:",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        PaymentMethod.entries.forEach { method ->
          val isSelected = method == uiState.selectedPaymentMethod
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, PrimaryGreen) else null,
            shadowElevation = if (isSelected) 3.dp else 1.dp,
            modifier = Modifier
              .clickable { viewModel.selectPaymentMethod(method) }
              .testTag("method_${method.id}")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
              Text(text = method.iconEmoji, fontSize = 18.sp)
              Spacer(modifier = Modifier.width(6.dp))
              Column {
                Text(
                  text = method.title,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = method.subtitle,
                  fontSize = 10.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  maxLines = 1
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // QR Code Interactive Display
      val qrPayload = session?.qrPayload ?: "https://qrpay.com/pay"
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.scale(if (uiState.isVerifyingPayment) pulseScale else 1f)
      ) {
        QrCodeView(
          content = qrPayload,
          sizeDp = 240.dp,
          centerEmoji = uiState.selectedPaymentMethod.iconEmoji,
          showScanBeam = !uiState.isVerifyingPayment
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Merchant Account details card
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        modifier = Modifier
          .fillMaxWidth()
          .clickable {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("QR Payload", qrPayload)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "¡Código copiado al portapapeles!", Toast.LENGTH_SHORT).show()
          }
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Destinatario: ${session?.merchantName ?: "QRPay Store Oficial"}",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = uiState.selectedPaymentMethod.defaultAccount,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = "Referencia: ${session?.referenceNumber ?: "REF-849201"}",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.primary,
              fontWeight = FontWeight.SemiBold
            )
          }

          IconButton(
            onClick = {
              val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
              val clip = ClipData.newPlainText("QR Payload", qrPayload)
              clipboard.setPrimaryClip(clip)
              Toast.makeText(context, "¡Código copiado al portapapeles!", Toast.LENGTH_SHORT).show()
            }
          ) {
            Icon(
              imageVector = Icons.Default.ContentCopy,
              contentDescription = "Copiar",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Real-Time Payment Status Indicator
      val status = session?.status ?: PaymentStatus.PENDING
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
          containerColor = when (status) {
            PaymentStatus.SUCCESS -> PrimaryGreen.copy(alpha = 0.15f)
            PaymentStatus.SCAN_DETECTED -> CyanAccent.copy(alpha = 0.15f)
            PaymentStatus.VERIFYING -> AmberWarning.copy(alpha = 0.15f)
            PaymentStatus.EXPIRED -> CoralSale.copy(alpha = 0.15f)
            else -> MaterialTheme.colorScheme.surface
          }
        )
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (uiState.isVerifyingPayment) {
            CircularProgressIndicator(
              modifier = Modifier.size(28.dp),
              strokeWidth = 3.dp,
              color = MaterialTheme.colorScheme.primary
            )
          } else {
            Icon(
              imageVector = when (status) {
                PaymentStatus.SUCCESS -> Icons.Default.CheckCircle
                PaymentStatus.SCAN_DETECTED -> Icons.Default.QrCodeScanner
                PaymentStatus.VERIFYING -> Icons.Default.Sync
                else -> Icons.Default.Payment
              },
              contentDescription = null,
              tint = when (status) {
                PaymentStatus.SUCCESS -> PrimaryGreen
                PaymentStatus.SCAN_DETECTED -> CyanAccent
                PaymentStatus.VERIFYING -> AmberWarning
                else -> MaterialTheme.colorScheme.primary
              },
              modifier = Modifier.size(28.dp)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column {
            Text(
              text = status.label,
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = status.description,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // 3-Step Guided Instructions
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "¿Cómo pagar con este código QR?",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
          )

          Spacer(modifier = Modifier.height(8.dp))

          InstructionStep(
            number = "1",
            text = "Abre tu app bancaria (${uiState.selectedPaymentMethod.title}) en tu celular."
          )
          InstructionStep(
            number = "2",
            text = "Selecciona la opción de escanear y apunta a este código QR."
          )
          InstructionStep(
            number = "3",
            text = "Revisa el monto de $${String.format(Locale.US, "%.2f", uiState.grandTotal)} y confirma la transacción."
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Simulated Gateway Controls (Testing & Verification)
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(14.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Security,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Simulador de Pasarela en Vivo",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "Para probar el flujo completo de pago QR sin salir de la app:",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
          )

          Spacer(modifier = Modifier.height(12.dp))

          Button(
            onClick = { viewModel.simulateScanAndPay() },
            enabled = !uiState.isVerifyingPayment,
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("simulate_qr_payment_btn"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = PrimaryGreen,
              contentColor = Color.Black
            )
          ) {
            if (uiState.isVerifyingPayment) {
              CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color.Black,
                strokeWidth = 2.dp
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text("Verificando con Gateway...", fontWeight = FontWeight.Bold)
            } else {
              Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("Simular Pago Realizado (Aprobar)", fontWeight = FontWeight.Black)
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedButton(
            onClick = {
              Toast.makeText(context, "Enlace de pago QR compartido", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(42.dp),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Compartir Código QR")
          }
        }
      }

      Spacer(modifier = Modifier.height(40.dp))
    }
  }
}

@Composable
private fun InstructionStep(number: String, text: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    verticalAlignment = Alignment.Top
  ) {
    Box(
      modifier = Modifier
        .size(22.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.primaryContainer),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = number,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onPrimaryContainer
      )
    }
    Spacer(modifier = Modifier.width(10.dp))
    Text(
      text = text,
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurface
    )
  }
}
