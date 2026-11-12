package com.example.data

import com.example.model.Coupon
import com.example.model.Product
import com.example.model.ProductCategory

object ProductCatalog {

  val AVAILABLE_COUPONS = listOf(
    Coupon("QRPAY15", 15, "15% de descuento pagando con cualquier código QR", minSpend = 50.0),
    Coupon("BIENVENIDO", 10, "10% de bienvenida en tu primera orden", minSpend = 20.0),
    Coupon("MEGAPROMO", 25, "25% de súper descuento en compras mayores a $200", minSpend = 200.0)
  )

  val SAMPLE_PRODUCTS = listOf(
    Product(
      id = "prod_01",
      name = "Audífonos Noise-Cancelling Pro",
      subtitle = "Cancelación activa de ruido híbrida y audio Hi-Res",
      category = ProductCategory.AUDIO_GAMING,
      price = 149.99,
      originalPrice = 199.99,
      rating = 4.9f,
      reviewsCount = 1420,
      description = "Audífonos inalámbricos premium con transductores de 40mm de titanio, hasta 40 horas de autonomía con carga rápida USB-C y almohadillas viscoelásticas ultra confortables.",
      specs = listOf(
        "Batería" to "40 Horas continuas",
        "Conectividad" to "Bluetooth 5.3 + Jack 3.5mm",
        "Cancelación" to "ANC Dual activa (-38dB)",
        "Peso" to "245 gramos"
      ),
      iconName = "Headphones",
      tags = listOf("Oferta -25%", "Más Vendido", "Hi-Res Audio"),
      inStock = true,
      stockUnits = 18
    ),
    Product(
      id = "prod_02",
      name = "Smartwatch Ultra Titanium GPS",
      subtitle = "Monitor de salud avanzado, ECG y resistencia 100m",
      category = ProductCategory.TECNOLOGIA,
      price = 229.00,
      originalPrice = 289.00,
      rating = 4.8f,
      reviewsCount = 890,
      description = "Reloj inteligente construido en caja de titanio aeroespacial con pantalla AMOLED de 2.0 pulgadas protegida por cristal de zafiro. Medición continua de SpO2, ritmo cardíaco y GPS de doble frecuencia.",
      specs = listOf(
        "Pantalla" to "AMOLED 2.02\" 1000 nits",
        "Resistencia" to "10 ATM (Sumergible 100m)",
        "Sensores" to "ECG, SpO2, Temperatura, Sueño",
        "Autonomía" to "Hasta 14 días"
      ),
      iconName = "Watch",
      tags = listOf("Top Ventas", "Titanio"),
      inStock = true,
      stockUnits = 9
    ),
    Product(
      id = "prod_03",
      name = "Cafetera Espresso Barista Touch",
      subtitle = "Bomba de presión italiana 19 Bares con vaporizador",
      category = ProductCategory.HOGAR_CAFE,
      price = 189.50,
      originalPrice = 240.00,
      rating = 4.9f,
      reviewsCount = 630,
      description = "Prepara el espresso perfecto y cappuccinos cremosos en casa con control térmico preciso PID y portafiltro profesional de acero inoxidable de 54mm.",
      specs = listOf(
        "Presión" to "Bomba italiana 19 Bares",
        "Vaporizador" to "Lanza de vapor profesional 360°",
        "Capacidad" to "Depósito de 1.8 Litros",
        "Calentamiento" to "Termobloque rápido en 25 seg"
      ),
      iconName = "Coffee",
      tags = listOf("Recomendado", "Barista"),
      inStock = true,
      stockUnits = 7
    ),
    Product(
      id = "prod_04",
      name = "Teclado Mecánico Custom RGB Wireless",
      subtitle = "Switches intercambiables Hot-Swap y chasis de aluminio",
      category = ProductCategory.AUDIO_GAMING,
      price = 99.00,
      originalPrice = 129.00,
      rating = 4.7f,
      reviewsCount = 512,
      description = "Teclado mecánico layout 75% con amortiguación de sonido de silicona de grado aeroespacial, perilla multimedia de aluminio anodizado y triple conectividad (2.4Ghz, Bluetooth, USB-C).",
      specs = listOf(
        "Layout" to "75% compacto (82 teclas)",
        "Switches" to "Gateron Pro Yellow pre-lubricados",
        "Batería" to "4000 mAh recargable",
        "RGB" to "18 efectos por tecla programables"
      ),
      iconName = "Keyboard",
      tags = listOf("Gamer Choice"),
      inStock = true,
      stockUnits = 14
    ),
    Product(
      id = "prod_05",
      name = "Parlante Bluetooth 360° Waterproof",
      subtitle = "Sonido inmersivo 40W con graves profundos IPX7",
      category = ProductCategory.AUDIO_GAMING,
      price = 69.90,
      originalPrice = 89.90,
      rating = 4.6f,
      reviewsCount = 378,
      description = "Altavoz portátil resistente al agua y caídas con radiadores pasivos duales y ecualizador dinámico en tiempo real.",
      specs = listOf(
        "Potencia" to "40W RMS Stereo",
        "Protección" to "Certificación IPX7 sumergible",
        "Duración" to "24 Horas de música continua"
      ),
      iconName = "Speaker",
      tags = listOf("IPX7 Resistente"),
      inStock = true,
      stockUnits = 25
    ),
    Product(
      id = "prod_06",
      name = "Taza Térmica Inteligente con Control de App",
      subtitle = "Mantiene tu café a la temperatura exacta seleccionada",
      category = ProductCategory.HOGAR_CAFE,
      price = 79.00,
      originalPrice = 99.00,
      rating = 4.7f,
      reviewsCount = 295,
      description = "Construida en acero inoxidable con revestimiento cerámico libre de BPA, base de carga inalámbrica coaster y pantalla LED invisible que despierta al levantarla.",
      specs = listOf(
        "Control" to "50°C a 65°C personalizable",
        "Capacidad" to "414 ml (14 oz)",
        "Base" to "Plato calefactor Qi inductivo"
      ),
      iconName = "LocalCafe",
      tags = listOf("Innovación"),
      inStock = true,
      stockUnits = 11
    ),
    Product(
      id = "prod_07",
      name = "Mochila Ejecutiva Antirrobo Impermeable",
      subtitle = "Bloqueo TSA, puerto USB exterior y bolsillo oculto RFID",
      category = ProductCategory.ACCESORIOS,
      price = 59.90,
      originalPrice = 75.00,
      rating = 4.8f,
      reviewsCount = 480,
      description = "Diseño ergonómico para laptops de hasta 16 pulgadas, tejido Oxford resistente a cortes y cremalleras YKK selladas contra lluvia.",
      specs = listOf(
        "Capacidad" to "28 Litros expandible a 35L",
        "Compartimento" to "Acolchado para laptop 16\" + tablet",
        "Seguridad" to "Candado TSA integrado"
      ),
      iconName = "Backpack",
      tags = listOf("Viaje"),
      inStock = true,
      stockUnits = 20
    ),
    Product(
      id = "prod_08",
      name = "Lámpara Minimalista de Escritorio LED",
      subtitle = "Cargador inalámbrico Qi 15W integrado y luz regulable",
      category = ProductCategory.HOGAR_CAFE,
      price = 45.00,
      originalPrice = 59.00,
      rating = 4.5f,
      reviewsCount = 188,
      description = "Brazo articulado de aluminio espacial, control táctil con 5 temperaturas de color (2700K - 6500K) y temporizador de apagado automático.",
      specs = listOf(
        "Carga Qi" to "15W Fast Wireless",
        "Temperatura" to "5 modos (Cálido a Frío)",
        "IRC" to "Ra > 95 (Cuidado visual)"
      ),
      iconName = "Lightbulb",
      tags = listOf("Home Office"),
      inStock = true,
      stockUnits = 16
    ),
    Product(
      id = "prod_09",
      name = "Mouse Ergonómico Vertical Inalámbrico",
      subtitle = "Postura natural a 57° que alivia la fatiga de muñeca",
      category = ProductCategory.TECNOLOGIA,
      price = 39.99,
      originalPrice = 49.99,
      rating = 4.7f,
      reviewsCount = 310,
      description = "Sensor óptico de precisión de 4000 DPI, clics silenciosos y conectividad multi-dispositivo para alternar entre 3 computadoras.",
      specs = listOf(
        "DPI" to "1000 / 1600 / 2400 / 4000",
        "Batería" to "Recargable Type-C (4 meses)",
        "Conexión" to "Bluetooth + Dongle 2.4G"
      ),
      iconName = "Mouse",
      tags = listOf("Ergonomía"),
      inStock = true,
      stockUnits = 30
    ),
    Product(
      id = "prod_10",
      name = "Billetera Slim de Fibra de Carbono RFID",
      subtitle = "Gatillo eyector rápido para hasta 8 tarjetas y billetes",
      category = ProductCategory.ACCESORIOS,
      price = 34.50,
      originalPrice = 45.00,
      rating = 4.6f,
      reviewsCount = 240,
      description = "Protege tus tarjetas contra clonación inalámbrica RFID con mecanismo de resorte pop-up y clip para billetes de acero inoxidable.",
      specs = listOf(
        "Material" to "Fibra de carbono real 3K",
        "Capacidad" to "1-8 tarjetas + 10 billetes",
        "Peso" to "65 gramos"
      ),
      iconName = "Wallet",
      tags = listOf("Seguridad RFID"),
      inStock = true,
      stockUnits = 22
    )
  )
}
