package io.pokesync.rom.nitro

import io.pokesync.lib.bytes.asReadOnlyByteBuf
import io.pokesync.lib.bytes.readString
import io.pokesync.lib.bytes.toByteString
import io.pokesync.rom.UnexpectedFileExtensionException
import io.pokesync.rom.graphics.ColourFormat
import io.pokesync.rom.graphics.ColourPalette
import io.pokesync.rom.graphics.DrawOrder
import kotlin.math.max
import kotlin.math.sqrt

/**
 * Reads a BTX0 [File] as a [Texture].
 */
fun File.readAsBTX0(): Texture {
    val header = FileHeader.readFromFile(bytes)
    if (header.extension != FileExtension.TEXTURE.nitroId) {
        throw UnexpectedFileExtensionException(header.extension)
    }

    if (header.sectionCount > 1) {
        throw Exception()
    }

    if (bytes.size() < header.fileSize) {
        throw Exception("Insufficient amount of bytes in file")
    }

    val iterator = bytes
        .asReadOnlyByteBuf()
        .skipBytes(header.headerSize)

    val framesBlockAddress = iterator.readIntLE()
    if (framesBlockAddress != iterator.readerIndex()) {
        throw Exception()
    }

    val framesBlockExt = iterator.readString(4)
    if (framesBlockExt != FileExtension.FRAME.nitroId) {
        throw UnexpectedFileExtensionException(framesBlockExt)
    }

    val framesSectionSize = iterator.readIntLE()

    iterator.skipBytes(4)

    val framesBytesLength = iterator.readUnsignedShortLE()
    val framesMetaAddress = iterator.readUnsignedShortLE()

    iterator.skipBytes(4)

    val framesByteAddress = iterator.readIntLE()

    iterator.skipBytes(4)

    val framesCompressedBytesLength = iterator.readUnsignedShortLE() * 8
    val framesCompressedMetaAddr = iterator.readUnsignedShortLE()

    iterator.skipBytes(4)

    val framesCompressedBytesAddr = iterator.readIntLE() * 8
    val framesCompressedMetaBytesAddr = iterator.readIntLE()

    iterator.skipBytes(4)

    val paletteBytesLength = iterator.readIntLE() * 8
    val paletteMetaAddress = iterator.readIntLE()
    val paletteBytesAddress = iterator.readIntLE()

    if ((framesBlockAddress + framesMetaAddress) != iterator.readerIndex()) {
        throw Exception()
    }

    iterator.skipBytes(1)

    val frameCount = iterator.readUnsignedByte()

    iterator.skipBytes(2)

    iterator.skipBytes(2)
    iterator.skipBytes(2)
    iterator.skipBytes(4)

    for (i in 0 until frameCount) {
        iterator.skipBytes(2)
        iterator.skipBytes(2)
    }

    iterator.skipBytes(2)
    iterator.skipBytes(2)

    val frames = mutableListOf<Frame>()
    for (i in 0 until frameCount) {
        val frameOffset = iterator.readUnsignedShortLE()
        val parameters = iterator.readUnsignedShortLE()

        iterator.readUnsignedByte()
        iterator.readUnsignedByte()
        iterator.readUnsignedByte()
        iterator.readUnsignedByte()

        val colourFormat = ColourFormat.getByNitroOrdinal((parameters shr 10) and 7)!!
        val width = (8 shl ((parameters shr 4) and 7))
        val height = (8 shl ((parameters shr 7) and 7))

        val address = (frameOffset * 8) + framesBlockAddress + framesByteAddress
        val byteLength = width * height * colourFormat.bits / 8

        val frameBytes = iterator
            .duplicate()
            .readerIndex(address)
            .toByteString(byteLength)

        frames.add(Frame(width, height, 0, colourFormat, DrawOrder.Companion.Horizontal, frameBytes))
    }

    val frameLabels = mutableListOf<String>()
    for (i in 0 until frameCount) {
        frameLabels.add(iterator.readString(16).replace(0.toChar().toString(), ""))
    }

    if ((framesBlockAddress + paletteMetaAddress) != iterator.readerIndex()) {
        throw Exception()
    }

    iterator.skipBytes(1)

    val paletteCount = iterator.readUnsignedByte()

    iterator.skipBytes(2)

    iterator.skipBytes(2)
    iterator.skipBytes(2)
    iterator.skipBytes(4)

    for (i in 0 until paletteCount) {
        iterator.skipBytes(2)
        iterator.skipBytes(2)
    }

    iterator.skipBytes(2)
    iterator.skipBytes(2)

    val palettes = mutableListOf<ColourPalette>()
    for (i in 0 until paletteCount) {
        val paletteOffset = iterator.readUnsignedShortLE()

        val paletteByteAddress = (paletteOffset * 8) + framesBlockAddress + paletteBytesAddress
        val paletteBytes = iterator.duplicate().readerIndex(paletteByteAddress).toByteString()

        iterator.skipBytes(2)

        val palette = ColourPalette.readNitroColourPalette(paletteBytes)

        palettes.add(palette)
    }

    val paletteLabels = mutableListOf<String>()
    for (i in 0 until paletteCount) {
        paletteLabels.add(iterator.readString(16).replace(0.toChar().toString(), ""))
    }

    return Texture(frames, frameLabels, palettes, paletteLabels)
}

/**
 * Reads a NCGR [File] as a [Frame].
 */
fun File.readAsNCGR(): Frame {
    val header = FileHeader.readFromFile(bytes)
    if (header.extension != FileExtension.GRAPHIC.nitroId) {
        throw UnexpectedFileExtensionException(header.extension)
    }

    if (header.sectionCount > 1) {
        throw Exception()
    }

    if (bytes.size() < header.fileSize) {
        throw Exception("Insufficient amount of bytes in file")
    }

    val iterator = bytes
        .asReadOnlyByteBuf()
        .skipBytes(header.headerSize)

    val characterDataExtension = iterator.readString(4)
    if (characterDataExtension.reversed() != FileExtension.CHARACTER.nitroId) {
        throw UnexpectedFileExtensionException(characterDataExtension)
    }

    val sectionSize = iterator.readIntLE()

    var height = iterator.readUnsignedShortLE()
    if (height != 65535) {
        height *= 8
    }

    var width = iterator.readUnsignedShortLE()
    if (width != 65535) {
        width *= 8
    }

    val colourFormat = ColourFormat.getByNitroOrdinal(iterator.readIntLE())!!
    val vramMode = iterator.readIntLE()
    val drawOrderId = iterator.readIntLE()
    val drawOrder = when ((drawOrderId and 0xFF)) {
        0    -> DrawOrder.Companion.Linear
        else -> DrawOrder.Companion.Horizontal
    }

    val tileBytesLength = iterator.readIntLE()

    if (width == 65535 || height == 65535) {
        val bitsPerPixel = colourFormat.bits
        val pixelCount = tileBytesLength * 8 / bitsPerPixel
        val updatedSize = sqrt(pixelCount.toDouble()).toInt()

        width = updatedSize
        height = updatedSize
    }

    iterator.skipBytes(4)

    val characterTileBytes = iterator.readBytes(tileBytesLength).toByteString()

    return Frame(width, height, vramMode, colourFormat, drawOrder, characterTileBytes)
}

/**
 * Reads a Nitro Colour Resource [File] to extract a collection of [ColourPalette]s.
 */
fun File.readAsNCLR(): List<ColourPalette> {
    val fileHeader = FileHeader.readFromFile(bytes)
    if (fileHeader.extension != FileExtension.COLOURS.nitroId) {
        throw UnexpectedFileExtensionException(fileHeader.extension)
    }

    if (fileHeader.sectionCount > 1) {
        throw Exception()
    }

    if (bytes.size() < fileHeader.fileSize) {
        throw Exception("Insufficient amount of bytes in file")
    }

    val iterator = bytes
        .asReadOnlyByteBuf()
        .skipBytes(fileHeader.headerSize)

    val paletteBlockExtension = iterator.readString(4)
    if (paletteBlockExtension.reversed() != FileExtension.PALETTE.nitroId) {
        throw UnexpectedFileExtensionException(paletteBlockExtension)
    }

    val sectionSize = iterator.readIntLE()
    val colourFormat = ColourFormat.getByNitroOrdinal(iterator.readUnsignedShortLE())!!

    iterator.readUnsignedShortLE()
    iterator.readIntLE()

    val paletteListByteLength = iterator.readIntLE()
    val paletteListByteOffset = iterator.readIntLE()

    var colourCount = colourFormat.colourCount()
    if (paletteListByteLength / 2 < colourCount) {
        colourCount = paletteListByteLength / 2
    }

    if (iterator.readerIndex() != (paletteListByteOffset + 24)) {
        throw Exception()
    }

    val paletteCount = max(1, paletteListByteLength / (colourCount * 2))
    val palettes = mutableListOf<ColourPalette>()

    for (i in 0 until paletteCount) {
        val paletteBytes = iterator.readBytes(colourCount * 2).toByteString()
        val colourPalette = ColourPalette.readNitroColourPalette(paletteBytes)

        palettes.add(colourPalette)
    }

    return palettes
}

/**
 * Reads a binary file as a text bank.
 */
fun File.readAsTextBank(): List<TextLabel> {
    val iterator = bytes.asReadOnlyByteBuf()

    val entryCount = iterator.readShortLE()
    var goldenKey = (iterator.readShortLE() * 765) and 0xFFFF

    val textAddresses = IntArray(entryCount.toInt())
    val textLengths = IntArray(entryCount.toInt())

    for (entryId in 0 until entryCount) {
        val v1 = (goldenKey * (entryId + 1)) and 0xFFFF
        val v2 = (v1 or (v1 shl 16))

        textAddresses[entryId] = iterator.readIntLE() xor v2
        textLengths[entryId] = iterator.readIntLE() xor v2
    }

    val textLabels = mutableListOf<TextLabel>()
    for (entryId in 0 until entryCount) {
        goldenKey = (596947 * (entryId + 1)) and 0xFFFF

        val bldr = StringBuilder()
        val expectedTextLength = textLengths[entryId]
        for (charIdx in 0 until expectedTextLength) {
            val characterId = (iterator.readUnsignedShortLE() xor goldenKey) and 0xFFFFF

            goldenKey += 18749
            goldenKey = goldenKey and 65535

            if (characterId == 65535) {
                break
            }

            val character = CHARACTER_TABLE[characterId]
            if (character != null) {
                bldr.append(character)
            }
        }

        textLabels.add(TextLabel(bldr.toString()))
    }

    return textLabels
}

/**
 * Reads a binary file as a bank of [MapLabel]s.
 */
fun File.readAsMapLabelBank(): List<MapLabel> {
    val buffer = bytes.asReadOnlyByteBuf()

    val entrySize = 16
    val entryCount = buffer.readableBytes() / entrySize

    val entries = mutableListOf<MapLabel>()
    for (i in 0 until entryCount) {
        entries.add(MapLabel(buffer.readString(entrySize)))
    }

    return entries
}

/**
 * Reads a binary file as a map matrix file.
 */
fun File.readAsMapMatrix(): MapMatrix {
    val iterator = bytes.asReadOnlyByteBuf()

    val worldWidth = iterator.readUnsignedByte().toInt()
    val worldLength = iterator.readUnsignedByte().toInt()

    val labelLength = iterator.readUnsignedByte().toInt()
    val label = iterator.readString(labelLength)

    val matrix = Array(worldWidth) { IntArray(worldLength) }
    for (z in 0 until worldLength) {
        for (x in 0 until worldWidth) {
            matrix[x][z] = iterator.readUnsignedShortLE()
        }
    }

    return MapMatrix(worldWidth, worldLength, label, matrix)
}

/**
 * Reads a binary file as a map structure file.
 */
fun File.readAsMapSruct(): MapStructure {
    val iterator = bytes.asReadOnlyByteBuf()

    val collisionFlagsBlockLen = iterator.readIntLE()
    val landscapeBlockLen = iterator.readIntLE()
    val modelsBlockLen = iterator.readIntLE()

    iterator.readIntLE() // TODO research. something called BDHC

    iterator.skipBytes(2)
    iterator.skipBytes(iterator.readUnsignedShortLE()) // skips section 0

    val mapSize = 32
    val tiles = Array(mapSize) { arrayOfNulls<MapStructure.Tile>(mapSize) }

    for (x in 0 until mapSize) {
        for (z in 0 until mapSize) {
            val collisionFlag = iterator.readUnsignedByte().toInt()
            val movementType = iterator.readUnsignedByte().toInt()

            tiles[x][z] = MapStructure.Tile(collisionFlag, movementType)
        }
    }

    val objEntrySize = 48
    val objCount = landscapeBlockLen / objEntrySize
    val objects = mutableListOf<MapStructure.ObjectEntry>()
    for (i in 0 until objCount) {
        val objectId = iterator.readIntLE()

        val yFlag = iterator.readUnsignedShortLE()
        val y = iterator.readUnsignedShortLE()
        val zFlag = iterator.readUnsignedShortLE()
        val z = iterator.readUnsignedShortLE()
        val xFlag = iterator.readUnsignedShortLE()
        val x = iterator.readUnsignedShortLE()

        iterator.skipBytes(12)

        val width = iterator.readUnsignedIntLE().toInt()
        val height = iterator.readUnsignedIntLE().toInt()
        val length = iterator.readUnsignedIntLE().toInt()

        iterator.skipBytes(8)

        objects.add(MapStructure.ObjectEntry(objectId, xFlag, x, zFlag, z, yFlag, y, width, height, length))
    }

    return MapStructure(tiles, objects)
}