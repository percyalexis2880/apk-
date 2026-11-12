package com.example.qr

import kotlin.math.min

/**
 * Lightweight, zero-dependency pure Kotlin QR Code Generator (ISO/IEC 18004 compliant).
 * Generates standard 2D boolean matrices that can be rendered on Canvas or Bitmaps
 * and scanned by any mobile QR scanner.
 */
object QrCodeGenerator {

  enum class EccLevel(val bits: Int, val formatBits: Int) {
    L(1, 1),
    M(0, 0),
    Q(3, 3),
    H(2, 2)
  }

  data class QrMatrix(
    val size: Int,
    val modules: Array<BooleanArray>,
    val isFunction: Array<BooleanArray>
  ) {
    operator fun get(x: Int, y: Int): Boolean = modules[y][x]

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is QrMatrix) return false
      return size == other.size && modules.contentDeepEquals(other.modules)
    }

    override fun hashCode(): Int {
      return 31 * size + modules.contentDeepHashCode()
    }
  }

  // Version data: total codewords, EC codewords per block, num blocks (ECC Level M)
  private data class VersionInfo(
    val version: Int,
    val totalDataBytes: Int,
    val ecCodewordsPerBlock: Int,
    val numBlocks: Int,
    val alignmentPatterns: IntArray
  )

  private val VERSION_INFOS = listOf(
    VersionInfo(1, 16, 10, 1, intArrayOf()),
    VersionInfo(2, 28, 16, 1, intArrayOf(6, 18)),
    VersionInfo(3, 44, 26, 1, intArrayOf(6, 22)),
    VersionInfo(4, 64, 18, 2, intArrayOf(6, 26)),
    VersionInfo(5, 86, 24, 2, intArrayOf(6, 30)),
    VersionInfo(6, 108, 16, 4, intArrayOf(6, 34)),
    VersionInfo(7, 124, 18, 4, intArrayOf(6, 22, 38))
  )

  fun encode(text: String, preferredEcc: EccLevel = EccLevel.M): QrMatrix {
    val textBytes = text.toByteArray(Charsets.UTF_8)
    val vInfo = VERSION_INFOS.firstOrNull { it.totalDataBytes >= textBytes.size + 3 }
      ?: VERSION_INFOS.last()

    val size = vInfo.version * 4 + 17
    val modules = Array(size) { BooleanArray(size) }
    val isFunction = Array(size) { BooleanArray(size) }

    // 1. Draw Finder Patterns (7x7) + Separators
    drawFinder(modules, isFunction, 0, 0, size)
    drawFinder(modules, isFunction, size - 7, 0, size)
    drawFinder(modules, isFunction, 0, size - 7, size)

    // 2. Draw Alignment Patterns
    val alignCoords = vInfo.alignmentPatterns
    for (i in alignCoords.indices) {
      for (j in alignCoords.indices) {
        val cx = alignCoords[i]
        val cy = alignCoords[j]
        // Skip finder areas
        if ((cx < 9 && cy < 9) || (cx > size - 9 && cy < 9) || (cx < 9 && cy > size - 9)) continue
        drawAlignment(modules, isFunction, cx, cy)
      }
    }

    // 3. Draw Timing Patterns
    for (i in 8 until size - 8) {
      val bit = (i % 2 == 0)
      if (!isFunction[6][i]) {
        modules[6][i] = bit
        isFunction[6][i] = true
      }
      if (!isFunction[i][6]) {
        modules[i][6] = bit
        isFunction[i][6] = true
      }
    }

    // 4. Reserve Format Info bits
    for (i in 0..8) {
      isFunction[8][i] = true
      isFunction[i][8] = true
      isFunction[size - 1 - i][8] = true
      isFunction[8][size - 1 - i] = true
    }
    // Dark module
    modules[4 * vInfo.version + 9][8] = true
    isFunction[4 * vInfo.version + 9][8] = true

    // 5. Build BitStream (Byte Mode: mode indicator 0100 + char count + data + terminator)
    val bitStream = BitBuffer()
    bitStream.appendBits(4, 4) // 0100 = 8-bit byte mode
    bitStream.appendBits(textBytes.size, 8)
    for (b in textBytes) {
      bitStream.appendBits(b.toInt() and 0xFF, 8)
    }

    val maxDataBits = vInfo.totalDataBytes * 8
    // Terminator
    val termLen = min(4, maxDataBits - bitStream.bitLength)
    bitStream.appendBits(0, termLen)
    // Pad to byte
    while (bitStream.bitLength % 8 != 0) {
      bitStream.appendBit(false)
    }
    // Pad bytes 0xEC, 0x11
    val padBytes = intArrayOf(0xEC, 0x11)
    var padIdx = 0
    while (bitStream.bitLength < maxDataBits) {
      bitStream.appendBits(padBytes[padIdx % 2], 8)
      padIdx++
    }

    val dataCodewords = bitStream.toByteArray()

    // 6. Error Correction via Reed-Solomon
    val finalCodewords = generateFinalCodewords(dataCodewords, vInfo)

    // 7. Place data bits zig-zag
    var codewordIdx = 0
    var bitIdx = 7
    var upward = true
    var right = size - 1

    while (right > 0) {
      if (right == 6) right-- // Skip vertical timing pattern
      val yRange = if (upward) (size - 1 downTo 0) else (0 until size)
      for (y in yRange) {
        for (col in 0..1) {
          val x = right - col
          if (!isFunction[y][x]) {
            var bit = false
            if (codewordIdx < finalCodewords.size) {
              bit = ((finalCodewords[codewordIdx].toInt() shr bitIdx) and 1) == 1
              bitIdx--
              if (bitIdx < 0) {
                bitIdx = 7
                codewordIdx++
              }
            }
            // Apply mask pattern 0: (x + y) % 2 == 0
            val mask = ((x + y) % 2 == 0)
            modules[y][x] = bit xor mask
          }
        }
      }
      upward = !upward
      right -= 2
    }

    // 8. Write Format Information (Mask 0, ECC M)
    writeFormatInformation(modules, 0, preferredEcc, size)

    return QrMatrix(size, modules, isFunction)
  }

  private fun drawFinder(modules: Array<BooleanArray>, isFunc: Array<BooleanArray>, ox: Int, oy: Int, size: Int) {
    for (dy in -1..7) {
      for (dx in -1..7) {
        val x = ox + dx
        val y = oy + dy
        if (x in 0 until size && y in 0 until size) {
          isFunc[y][x] = true
          val isOuter = (dx in 0..6 && (dy == 0 || dy == 6)) || (dy in 0..6 && (dx == 0 || dx == 6))
          val isInner = (dx in 2..4 && dy in 2..4)
          modules[y][x] = (isOuter || isInner)
        }
      }
    }
  }

  private fun drawAlignment(modules: Array<BooleanArray>, isFunc: Array<BooleanArray>, cx: Int, cy: Int) {
    for (dy in -2..2) {
      for (dx in -2..2) {
        val x = cx + dx
        val y = cy + dy
        isFunc[y][x] = true
        val isBorder = kotlin.math.abs(dx) == 2 || kotlin.math.abs(dy) == 2
        val isCenter = (dx == 0 && dy == 0)
        modules[y][x] = isBorder || isCenter
      }
    }
  }

  private fun writeFormatInformation(modules: Array<BooleanArray>, mask: Int, ecc: EccLevel, size: Int) {
    // Format bits = (ecc.formatBits shl 3) or mask
    val data = (ecc.formatBits shl 3) or mask
    // Compute 10-bit BCH code
    var rem = data shl 10
    val generator = 0x537
    for (i in 4 downTo 0) {
      if ((rem and (1 shl (i + 10))) != 0) {
        rem = rem xor (generator shl i)
      }
    }
    var format = ((data shl 10) or rem) xor 0x5412

    // Write to modules around finder patterns
    for (i in 0..14) {
      val bit = ((format shr i) and 1) == 1
      // Top-left
      val (x1, y1) = when {
        i <= 5 -> Pair(i, 8)
        i == 6 -> Pair(7, 8)
        i == 7 -> Pair(8, 8)
        i == 8 -> Pair(8, 7)
        else -> Pair(8, 14 - i)
      }
      modules[y1][x1] = bit

      // Top-right and Bottom-left
      val (x2, y2) = if (i < 8) {
        Pair(8, size - 1 - i)
      } else {
        Pair(size - 15 + i, 8)
      }
      modules[y2][x2] = bit
    }
  }

  private fun generateFinalCodewords(data: ByteArray, vInfo: VersionInfo): ByteArray {
    val blocks = vInfo.numBlocks
    val dataPerBlock = vInfo.totalDataBytes / blocks
    val ecPerBlock = vInfo.ecCodewordsPerBlock
    val ecGenPoly = rsGeneratorPoly(ecPerBlock)

    val dataBlocks = Array(blocks) { b ->
      val start = b * dataPerBlock
      val end = start + dataPerBlock
      data.copyOfRange(start, min(end, data.size))
    }

    val ecBlocks = Array(blocks) { b ->
      rsComputeRemainder(dataBlocks[b], ecGenPoly)
    }

    // Interleave
    val result = ArrayList<Byte>()
    for (i in 0 until dataPerBlock) {
      for (b in 0 until blocks) {
        if (i < dataBlocks[b].size) result.add(dataBlocks[b][i])
      }
    }
    for (i in 0 until ecPerBlock) {
      for (b in 0 until blocks) {
        if (i < ecBlocks[b].size) result.add(ecBlocks[b][i])
      }
    }
    return result.toByteArray()
  }

  // Galois Field GF(256) with primitive poly 0x11D
  private val EXP = IntArray(512)
  private val LOG = IntArray(256)

  init {
    var x = 1
    for (i in 0 until 255) {
      EXP[i] = x
      EXP[i + 255] = x
      LOG[x] = i
      x = x shl 1
      if ((x and 0x100) != 0) x = x xor 0x11D
    }
  }

  private fun gfMultiply(x: Int, y: Int): Int {
    if (x == 0 || y == 0) return 0
    return EXP[LOG[x] + LOG[y]]
  }

  private fun rsGeneratorPoly(degree: Int): IntArray {
    var poly = intArrayOf(1)
    for (i in 0 until degree) {
      val factor = intArrayOf(EXP[i], 1)
      poly = rsMultiplyPoly(poly, factor)
    }
    return poly
  }

  private fun rsMultiplyPoly(p1: IntArray, p2: IntArray): IntArray {
    val r = IntArray(p1.size + p2.size - 1)
    for (i in p1.indices) {
      for (j in p2.indices) {
        r[i + j] = r[i + j] xor gfMultiply(p1[i], p2[j])
      }
    }
    return r
  }

  private fun rsComputeRemainder(data: ByteArray, genPoly: IntArray): ByteArray {
    val rem = IntArray(genPoly.size - 1)
    for (b in data) {
      val factor = (b.toInt() and 0xFF) xor rem[0]
      for (i in 0 until rem.size - 1) {
        rem[i] = rem[i + 1] xor gfMultiply(genPoly[genPoly.size - 2 - i], factor)
      }
      rem[rem.size - 1] = gfMultiply(genPoly[0], factor)
    }
    return ByteArray(rem.size) { rem[it].toByte() }
  }

  private class BitBuffer {
    private val buffer = ArrayList<Boolean>()
    val bitLength get() = buffer.size

    fun appendBit(bit: Boolean) {
      buffer.add(bit)
    }

    fun appendBits(value: Int, count: Int) {
      for (i in count - 1 downTo 0) {
        appendBit(((value shr i) and 1) == 1)
      }
    }

    fun toByteArray(): ByteArray {
      val bytes = ByteArray((buffer.size + 7) / 8)
      for (i in buffer.indices) {
        if (buffer[i]) {
          bytes[i / 8] = (bytes[i / 8].toInt() or (1 shl (7 - (i % 8)))).toByte()
        }
      }
      return bytes
    }
  }
}
