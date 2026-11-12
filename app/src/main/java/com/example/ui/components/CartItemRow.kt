package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CartItem
import com.example.ui.theme.CoralSale
import java.util.Locale

@Composable
fun CartItemRow(
  cartItem: CartItem,
  onIncrement: () -> Unit,
  onDecrement: () -> Unit,
  onRemove: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("cart_item_${cartItem.product.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Product Icon Avatar
      val iconVector: ImageVector = when (cartItem.product.iconName) {
        "Headphones" -> Icons.Default.Headphones
        "Watch" -> Icons.Default.Watch
        "Coffee" -> Icons.Default.Coffee
        "Keyboard" -> Icons.Default.Keyboard
        "Speaker" -> Icons.Default.Speaker
        "Lightbulb" -> Icons.Default.Lightbulb
        "Mouse" -> Icons.Default.Mouse
        "Devices" -> Icons.Default.Devices
        else -> Icons.Default.ShoppingBag
      }

      Box(
        modifier = Modifier
          .size(56.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = iconVector,
          contentDescription = cartItem.product.name,
          modifier = Modifier.size(28.dp),
          tint = MaterialTheme.colorScheme.primary
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      // Product Details
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = cartItem.product.name,
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.SemiBold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Text(
          text = "$${String.format(Locale.US, "%.2f", cartItem.product.price)} c/u",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
          text = "Total: $${String.format(Locale.US, "%.2f", cartItem.totalPrice)}",
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.Bold
        )
      }

      // Quantity controls
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
          .clip(RoundedCornerShape(24.dp))
          .background(MaterialTheme.colorScheme.surfaceVariant)
          .padding(horizontal = 4.dp, vertical = 2.dp)
      ) {
        IconButton(
          onClick = onDecrement,
          modifier = Modifier
            .size(32.dp)
            .testTag("decrement_${cartItem.product.id}")
        ) {
          Icon(
            imageVector = if (cartItem.quantity == 1) Icons.Default.Delete else Icons.Default.Remove,
            contentDescription = "Disminuir cantidad",
            tint = if (cartItem.quantity == 1) CoralSale else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(16.dp)
          )
        }

        Text(
          text = cartItem.quantity.toString(),
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(horizontal = 4.dp)
        )

        IconButton(
          onClick = onIncrement,
          modifier = Modifier
            .size(32.dp)
            .testTag("increment_${cartItem.product.id}")
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Aumentar cantidad",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }
}
