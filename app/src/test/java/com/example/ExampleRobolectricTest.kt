package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.qr.QrCodeGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("QRPay Cart", appName)
  }

  @Test
  fun `qr code generator produces valid matrix`() {
    val matrix = QrCodeGenerator.encode("yape://pay?to=987654321&amount=100.00")
    assertNotNull(matrix)
    assertTrue(matrix.size >= 21)
    // Finder pattern at (0,0) must be true
    assertTrue(matrix[0, 0])
  }
}
