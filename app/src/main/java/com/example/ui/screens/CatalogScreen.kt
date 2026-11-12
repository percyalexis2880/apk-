package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.Product
import com.example.model.ProductCategory
import com.example.ui.ShopUiState
import com.example.ui.ShopViewModel
import com.example.ui.SortOption
import com.example.ui.components.ProductCard
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PrimaryGreen
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
  viewModel: ShopViewModel,
  uiState: ShopUiState,
  onNavigateToCart: () -> Unit,
  onOpenOrders: () -> Unit,
  modifier: Modifier = Modifier
) {
  var showSortMenu by remember { mutableStateOf(false) }

  // Filter and sort products
  val filteredProducts = remember(
    uiState.products,
    uiState.selectedCategory,
    uiState.searchQuery,
    uiState.sortOption
  ) {
    uiState.products
      .filter { product ->
        val matchesCategory = uiState.selectedCategory == ProductCategory.TODOS ||
            product.category == uiState.selectedCategory
        val matchesQuery = uiState.searchQuery.isBlank() ||
            product.name.contains(uiState.searchQuery, ignoreCase = true) ||
            product.description.contains(uiState.searchQuery, ignoreCase = true) ||
            product.tags.any { it.contains(uiState.searchQuery, ignoreCase = true) }
        matchesCategory && matchesQuery
      }
      .let { list ->
        when (uiState.sortOption) {
          SortOption.POPULAR -> list.sortedByDescending { it.reviewsCount }
          SortOption.PRICE_ASC -> list.sortedBy { it.price }
          SortOption.PRICE_DESC -> list.sortedByDescending { it.price }
          SortOption.RATING -> list.sortedByDescending { it.rating }
        }
      }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Header Bar
    Surface(
      color = MaterialTheme.colorScheme.surface,
      shadowElevation = 3.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                  Brush.linearGradient(listOf(PrimaryGreen, CyanAccent))
                ),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.QrCode2,
                contentDescription = "Logo QRPay",
                tint = Color.Black,
                modifier = Modifier.size(26.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "QRPay Cart",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Paga al instante con código QR",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
              )
            }
          }

          // Cart badge button
          IconButton(
            onClick = onNavigateToCart,
            modifier = Modifier.testTag("catalog_cart_button")
          ) {
            BadgedBox(
              badge = {
                if (uiState.totalItemsCount > 0) {
                  Badge(
                    containerColor = PrimaryGreen,
                    contentColor = Color.Black
                  ) {
                    Text(
                      text = uiState.totalItemsCount.toString(),
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
              }
            ) {
              Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Ver Carrito",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(26.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Bar and Sort action
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = { Text("Buscar productos, marcas...") },
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
              )
            },
            trailingIcon = {
              if (uiState.searchQuery.isNotEmpty()) {
                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                  Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Limpiar búsqueda"
                  )
                }
              }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
              unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
              focusedBorderColor = MaterialTheme.colorScheme.primary,
              unfocusedBorderColor = Color.Transparent
            ),
            modifier = Modifier
              .weight(1f)
              .testTag("search_text_field")
          )

          Spacer(modifier = Modifier.width(8.dp))

          Box {
            IconButton(
              onClick = { showSortMenu = true },
              modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                .testTag("sort_filter_button")
            ) {
              Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = "Ordenar catálogo",
                tint = MaterialTheme.colorScheme.onSurface
              )
            }

            DropdownMenu(
              expanded = showSortMenu,
              onDismissRequest = { showSortMenu = false }
            ) {
              SortOption.entries.forEach { option ->
                DropdownMenuItem(
                  text = {
                    Text(
                      text = option.label,
                      fontWeight = if (uiState.sortOption == option) FontWeight.Bold else FontWeight.Normal,
                      color = if (uiState.sortOption == option) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                  },
                  onClick = {
                    viewModel.setSortOption(option)
                    showSortMenu = false
                  }
                )
              }
            }
          }
        }
      }
    }

    // Main scrollable grid
    LazyVerticalGrid(
      columns = GridCells.Fixed(2),
      contentPadding = PaddingValues(16.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
      modifier = Modifier
        .fillMaxSize()
        .testTag("products_grid")
    ) {
      // Hero Promo Banner Item
      item(span = { GridItemSpan(2) }) {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("hero_promo_banner")
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(145.dp)
          ) {
            Image(
              painter = painterResource(id = R.drawable.banner_promo),
              contentDescription = "Banner Promocional QRPay",
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )

            // Gradient Overlay for readability
            Box(
              modifier = Modifier
                .fillMaxSize()
                .background(
                  Brush.horizontalGradient(
                    listOf(
                      Color.Black.copy(alpha = 0.82f),
                      Color.Black.copy(alpha = 0.3f),
                      Color.Transparent
                    )
                  )
                )
            )

            Column(
              modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
              verticalArrangement = Arrangement.Center
            ) {
              Surface(
                color = PrimaryGreen,
                shape = RoundedCornerShape(8.dp)
              ) {
                Text(
                  text = "CUPÓN 15% OFF",
                  color = Color.Black,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Black,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
              }

              Spacer(modifier = Modifier.height(6.dp))

              Text(
                text = "Paga con QR y Ahorra",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
              )

              Text(
                text = "Usa el código QRPAY15 al finalizar",
                color = CyanAccent,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
              )
            }
          }
        }
      }

      // Category Chips Carousel Item
      item(span = { GridItemSpan(2) }) {
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(ProductCategory.entries) { category ->
            val isSelected = category == uiState.selectedCategory
            FilterChip(
              selected = isSelected,
              onClick = { viewModel.selectCategory(category) },
              label = {
                Text(
                  text = category.displayName,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
              },
              leadingIcon = {
                val icon = when (category.icon) {
                  "Devices" -> Icons.Default.Devices
                  "Headphones" -> Icons.Default.Headphones
                  "Coffee" -> Icons.Default.Coffee
                  "Watch" -> Icons.Default.Watch
                  else -> Icons.Default.Storefront
                }
                Icon(
                  imageVector = icon,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp)
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
              ),
              shape = RoundedCornerShape(12.dp)
            )
          }
        }
      }

      // Results count text
      item(span = { GridItemSpan(2) }) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "${filteredProducts.size} productos encontrados",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Text(
            text = uiState.sortOption.label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
          )
        }
      }

      // Product items
      items(filteredProducts, key = { it.id }) { product ->
        ProductCard(
          product = product,
          onCardClick = { viewModel.openProductDetail(product) },
          onAddToCart = { viewModel.addToCart(product, 1) }
        )
      }

      // Bottom Spacer
      item(span = { GridItemSpan(2) }) {
        Spacer(modifier = Modifier.height(60.dp))
      }
    }
  }

  // Product Detail Bottom Sheet
  if (uiState.selectedProductForDetail != null) {
    ProductDetailSheet(
      product = uiState.selectedProductForDetail,
      onDismiss = { viewModel.dismissProductDetail() },
      onAddToCart = { qty ->
        viewModel.addToCart(uiState.selectedProductForDetail, qty)
        viewModel.dismissProductDetail()
      },
      onBuyNowWithQr = { qty ->
        viewModel.addToCart(uiState.selectedProductForDetail, qty)
        viewModel.dismissProductDetail()
        viewModel.startCheckoutWithQr()
      }
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailSheet(
  product: Product,
  onDismiss: () -> Unit,
  onAddToCart: (Int) -> Unit,
  onBuyNowWithQr: (Int) -> Unit
) {
  var quantity by remember { mutableIntStateOf(1) }
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = MaterialTheme.colorScheme.primaryContainer
        ) {
          Text(
            text = product.category.displayName,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontWeight = FontWeight.Bold
          )
        }

        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Cerrar")
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = product.name,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold
      )

      Text(
        text = product.subtitle,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Price & Rating Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "$${String.format(Locale.US, "%.2f", product.price)}",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
          )
          if (product.originalPrice != null) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "$${String.format(Locale.US, "%.2f", product.originalPrice)}",
              style = MaterialTheme.typography.titleMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Star, contentDescription = null, tint = AmberWarning)
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "${product.rating} (${product.reviewsCount} opiniones)",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Description
      Text(
        text = "Descripción",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = product.description,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Technical Specs
      Text(
        text = "Especificaciones Técnicas",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(6.dp))
      product.specs.forEach { (key, value) ->
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(text = key, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
          Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Quantity selector
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Cantidad:",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold
        )

        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp)
        ) {
          IconButton(
            onClick = { if (quantity > 1) quantity-- },
            modifier = Modifier.size(36.dp)
          ) {
            Icon(Icons.Default.Remove, contentDescription = "Menos")
          }
          Text(
            text = quantity.toString(),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp)
          )
          IconButton(
            onClick = { if (quantity < product.stockUnits) quantity++ },
            modifier = Modifier.size(36.dp)
          ) {
            Icon(Icons.Default.Add, contentDescription = "Más")
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Action buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Button(
          onClick = { onAddToCart(quantity) },
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .weight(1f)
            .height(50.dp)
            .testTag("detail_add_to_cart_btn")
        ) {
          Icon(Icons.Default.ShoppingCart, contentDescription = null)
          Spacer(modifier = Modifier.width(6.dp))
          Text("Al Carrito", fontWeight = FontWeight.Bold)
        }

        Button(
          onClick = { onBuyNowWithQr(quantity) },
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .weight(1f)
            .height(50.dp)
            .testTag("detail_buy_now_qr_btn")
        ) {
          Icon(Icons.Default.QrCode2, contentDescription = null)
          Spacer(modifier = Modifier.width(6.dp))
          Text("Pagar con QR", fontWeight = FontWeight.Bold)
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
