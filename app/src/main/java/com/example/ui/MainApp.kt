package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CatalogScreen
import com.example.ui.screens.OrderHistoryScreen
import com.example.ui.screens.PaymentReceiptScreen
import com.example.ui.screens.QRPaymentGatewayScreen
import com.example.ui.theme.PrimaryGreen

@Composable
fun MainApp(
  viewModel: ShopViewModel = viewModel()
) {
  val uiState by viewModel.uiState.collectAsState()
  val snackbarHostState = remember { SnackbarHostState() }

  // Handle snackbars
  LaunchedEffect(uiState.snackbarMessage) {
    uiState.snackbarMessage?.let { msg ->
      snackbarHostState.showSnackbar(msg)
      viewModel.clearSnackbar()
    }
  }

  // Determine if bottom navigation bar should be visible
  val showBottomNav = uiState.currentScreen is Screen.Catalog ||
      uiState.currentScreen is Screen.Cart ||
      uiState.currentScreen is Screen.OrderHistory

  Scaffold(
    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    bottomBar = {
      if (showBottomNav) {
        NavigationBar(
          modifier = Modifier.testTag("main_bottom_nav")
        ) {
          // Tab 1: Tienda
          NavigationBarItem(
            selected = uiState.currentScreen is Screen.Catalog,
            onClick = { viewModel.setScreen(Screen.Catalog) },
            icon = {
              Icon(Icons.Default.Storefront, contentDescription = "Tienda")
            },
            label = { Text("Tienda", fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = Color.Black,
              indicatorColor = PrimaryGreen
            ),
            modifier = Modifier.testTag("nav_tab_catalog")
          )

          // Tab 2: Carrito
          NavigationBarItem(
            selected = uiState.currentScreen is Screen.Cart,
            onClick = { viewModel.setScreen(Screen.Cart) },
            icon = {
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
                Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito")
              }
            },
            label = { Text("Carrito", fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = Color.Black,
              indicatorColor = PrimaryGreen
            ),
            modifier = Modifier.testTag("nav_tab_cart")
          )

          // Tab 3: Mis Pedidos
          NavigationBarItem(
            selected = uiState.currentScreen is Screen.OrderHistory,
            onClick = { viewModel.setScreen(Screen.OrderHistory) },
            icon = {
              Icon(Icons.Default.History, contentDescription = "Mis Pedidos")
            },
            label = { Text("Pedidos", fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = Color.Black,
              indicatorColor = PrimaryGreen
            ),
            modifier = Modifier.testTag("nav_tab_orders")
          )
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      contentAlignment = Alignment.TopCenter
    ) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .widthIn(max = 640.dp)
      ) {
        AnimatedContent(
          targetState = uiState.currentScreen,
          transitionSpec = { fadeIn() togetherWith fadeOut() },
          label = "screen_transition"
        ) { screen ->
          when (screen) {
            is Screen.Catalog -> {
              CatalogScreen(
                viewModel = viewModel,
                uiState = uiState,
                onNavigateToCart = { viewModel.setScreen(Screen.Cart) },
                onOpenOrders = { viewModel.setScreen(Screen.OrderHistory) }
              )
            }
            is Screen.Cart -> {
              CartScreen(
                viewModel = viewModel,
                uiState = uiState,
                onBackToCatalog = { viewModel.setScreen(Screen.Catalog) },
                onProceedToQrPayment = { viewModel.startCheckoutWithQr() }
              )
            }
            is Screen.QrGateway -> {
              QRPaymentGatewayScreen(
                viewModel = viewModel,
                uiState = uiState,
                onBackToCart = { viewModel.setScreen(Screen.Cart) }
              )
            }
            is Screen.Receipt -> {
              PaymentReceiptScreen(
                order = screen.order,
                onBackToCatalog = { viewModel.setScreen(Screen.Catalog) },
                onViewOrderHistory = { viewModel.setScreen(Screen.OrderHistory) }
              )
            }
            is Screen.OrderHistory -> {
              OrderHistoryScreen(
                viewModel = viewModel,
                uiState = uiState,
                onBackToCatalog = { viewModel.setScreen(Screen.Catalog) },
                onSelectOrder = { order -> viewModel.setScreen(Screen.Receipt(order)) }
              )
            }
          }
        }
      }
    }
  }
}
