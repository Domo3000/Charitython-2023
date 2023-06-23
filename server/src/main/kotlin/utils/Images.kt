package utils

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.format.Format
import com.sksamuel.scrimage.nio.JpegWriter
import com.sksamuel.scrimage.nio.PngWriter
import io.ktor.http.content.*
import java.io.File
import java.util.*

fun PartData.FileItem.saveImage(format: Format = Format.PNG): String? {
    val baseDirectory = "${System.getProperty("user.dir")}/files"

    val directory = File(baseDirectory)
    if (!directory.isDirectory) {
        directory.mkdir()
    }

    val fileBytes = streamProvider().readBytes()

    try {
        val originalImage: ImmutableImage = ImmutableImage.loader().fromBytes(fileBytes)
        val scaledImage = originalImage.scaleToWidth(500)

        val (writer, extension) = when(format) {
            Format.PNG -> PngWriter.MaxCompression to "png"
            Format.JPEG -> JpegWriter.Default to "jpeg"
            else -> throw UnsupportedOperationException("format $format is not implemented")
        }

        val fileName = "${UUID.randomUUID()}.$extension"
        val imagePath = "$baseDirectory/$fileName"

        scaledImage.output(writer, File(imagePath))

        return fileName
    } catch (_: Exception) {
        return null
    }
}