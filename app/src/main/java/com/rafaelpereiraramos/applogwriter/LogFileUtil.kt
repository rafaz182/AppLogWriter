package com.rafaelpereiraramos.applogwriter

import java.io.File
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*
import java.util.regex.Pattern

/**
 * Created by Rafael P. Ramos on 02/11/2018.
 */
class LogFileUtil {

    companion object {

        fun getNewestLogFileModified(listFile: Array<File>): File? =
            getNewestModified(listFile, "log")

        fun getNewestModified(listFile: Array<File>, fileFormatWithoutDot: String): File? {
            if (listFile.size == 0) return null

            val pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}\\p{Punct}((\\d{2,}\\.$fileFormatWithoutDot)|$fileFormatWithoutDot)")
            val datePattern = SimpleDateFormat("yyyy-MM-dd")
            val newestDate = Calendar.getInstance()
            val currentDate = Calendar.getInstance()
            var newestIndex = -1
            var newestFile: File? = null

            newestDate.time = Date(Long.MIN_VALUE)

            for (file in listFile) {
                //if (false) continue

                if (pattern.matcher(file.name).matches()) {
                    val dateStr = getFileDate(file)
                    val fileDate = datePattern.parse(dateStr)
                    currentDate.time = fileDate

                    if (currentDate.after(newestDate)) {
                        newestFile = file
                        newestDate.time = fileDate
                        newestIndex = -1
                    } else if (currentDate.equals(newestDate)) {
                        val suffixIndex = getCounterSuffix(file)

                        if (suffixIndex > newestIndex) {
                            newestFile = file
                            newestDate.time = fileDate
                            newestIndex = suffixIndex
                        }
                    }
                }
            }

            return newestFile
        }

        fun getFileDate(file: File): String {
            val name = StringBuilder(file.name)
            var suffixPos = name.lastIndexOf("_")

            if (suffixPos == -1)
                suffixPos = name.lastIndexOf(".")

            name.replace(suffixPos, name.length, "")

            return name.toString()
        }

        fun getCounterSuffix(file: File): Int {
            val name = StringBuilder(file.name)
            val suffixPos = name.lastIndexOf("_")

            if (suffixPos == -1)
                return 0

            val formatPos = name.lastIndexOf(".")
            name.replace(formatPos, name.length, "")

            val number = name.substring(suffixPos + 1, name.length)

            return Integer.parseInt(number)
        }

        fun getNextCounterSuffix(file: File): Int {
            return getCounterSuffix(file) + 1
        }

        fun isCreatedToday(file: File): Boolean {
            val date = getFileDate(file)

            return date == SimpleDateFormat("yyyy-MM-dd").format(Date())
        }
    }
}