package utils

import model.CleanupEventResultDao
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.util.*

object Excel {
    private fun writeRow(row: XSSFRow, resultDao: CleanupEventResultDao, date: String) {
        val values = listOf(
            date,
            resultDao.emailAddress,
            resultDao.organization,
            resultDao.partnerOrganization ?: "",
            resultDao.province,
            resultDao.location,
            resultDao.zipCode,
            resultDao.numberOfParticipants.toString(),
            resultDao.totalWeight.toString(),
            resultDao.amountOfTrashBags?.toString() ?: "",
            resultDao.cleanedAreaSize ?: "",
            resultDao.cigaretteButtsCount?.toString() ?: "",
            resultDao.canCount?.toString() ?: "",
            resultDao.petBottleCount?.toString() ?: "",
            resultDao.glassBottleCount?.toString() ?: "",
            resultDao.hazardousWaste ?: "",
            resultDao.strangeFinds ?: "",
            resultDao.miscellaneous ?: "",
            resultDao.wayOfRecognition
        )

        values.mapIndexed { i, v ->
            row
                .createCell(i)
                .setCellValue(v)
        }
    }

    fun createExcelFile(list: List<CleanupEventResultDao>, date: String): File {
        val workbook = XSSFWorkbook()
        val workSheet = workbook.createSheet()

        val labels = listOf(
            "Datum",
            "E-Mail-Addresse",
            "Organisation",
            "Partnerorganisationen",
            "Bundesland",
            "Ort",
            "PLZ",
            "Anzahl der Teilnehmer:innen",
            "Gesamtgewicht in kg",
            "Anzahl der Müllsäcke in Liter",
            "Größe des gesäuberten Gebiets",
            "Anzahl der Zigarettenstummel",
            "Anzahl der Dosen",
            "Anzahl der PET Flaschen",
            "Anzahl der Glasflaschen",
            "Gefährliche Abfälle",
            "Sonderbare Funde",
            "Sonstiges",
            "Woher erfahren"
        )

        val row0 = workSheet
            .createRow(0)

        labels.mapIndexed { i, l ->
            row0
                .createCell(i)
                .setCellValue(l)
        }

        list.mapIndexed { i, result ->
            val row = workSheet.createRow(i + 1)

            writeRow(row, result, date)
        }

        val baseDirectory = "${System.getProperty("user.dir")}/admin_files"

        val directory = File(baseDirectory)
        if (!directory.isDirectory) {
            directory.mkdir()
        }

        val file = File("$baseDirectory/${UUID.randomUUID()}.xlsx")

        workbook.write(file.outputStream())
        workbook.close()

        return file
    }
}