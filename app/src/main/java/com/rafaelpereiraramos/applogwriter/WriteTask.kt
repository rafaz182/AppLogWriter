package com.rafaelpereiraramos.applogwriter

import android.os.Build
import com.google.common.io.Files
import java.lang.StringBuilder
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.LinkedBlockingDeque
import java.io.*

/**
 * Created by Rafael P. Ramos on 02/11/2018.
 */
internal class WriteTask(
    private val storageDir: File,
    private val encoding: Charset = Charset.defaultCharset()!!,
    private val fileSize: Long = 1048576,
    private val cacheSize: Long = 10485760
) : Runnable {

    private val FILE_FORMAT = ".log"
    private val BUFFER_SIZE = 512

    private val deque = LinkedBlockingDeque<LogMessage>()

    @Volatile var isRunning = false

    private val logFile: File
    private var output: BufferedWriter
    private var currentFileSize = 0
    private var currentCacheUsed: Long = 0
    private val newLine = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        System.lineSeparator()
    } else {
        "\n"
    }

    init {
        logFile = loadFile(storageDir, fileSize)
        val fos = FileOutputStream(logFile, true)
        val osw = OutputStreamWriter(fos, encoding)
        output = BufferedWriter(osw, BUFFER_SIZE)
    }

    override fun run() {

        while (isRunning) {
            val logMessage = deque.take()

            if (isFileFilled(logMessage) || logMessage is RotateLogMessage) {
                executeRotate(logMessage)
            }

            when (logMessage) {
                is WriteLogMessage -> executeWrite(logMessage)
                is StopLogMessage -> executeStop(logMessage)
            }
        }

        output.close()
    }

    fun insert(writeLogMessage: LogMessage) {
        deque.offer(writeLogMessage)
    }

    fun start() {
        if (!storageDir.exists()) storageDir.mkdirs()

        currentCacheUsed = getTotalCacheUsed()

        while (!allocCacheSpace());

        isRunning = true
    }

    /**
     * Create a file in the following format: yyyy-MM-dd.log if it is the first file of the day, or
     * yyyy-MM-dd_XX.log if the first file of the day already been created, where XX is a counter of
     * files created in same day
     *
     * @param storageDir - Default path to save log files
     * @param fileSize - Maximum size of the file to be loaded
     * @return - A file to Writer do his work
     */
    private fun loadFile(storageDir: File, fileSize: Long): File {
        val today = SimpleDateFormat("yyy-MM-dd").format(Date())
        var fileNameBuilder: StringBuilder //= StringBuilder()
        var newFile = LogFileUtil.getNewestLogFileModified(storageDir.listFiles())

        if (newFile!= null && LogFileUtil.isCreatedToday(newFile)) {
            var newFileLength = Files.toByteArray(newFile).size

            if (newFileLength < fileSize) {
                currentFileSize = newFileLength;
                return newFile;
            }

            fileNameBuilder = StringBuilder(newFile.name)

            val fileFormatPosition = fileNameBuilder.lastIndexOf(".")
            fileNameBuilder.replace(fileFormatPosition, fileNameBuilder.length, "")
            val counter = LogFileUtil.getNextCounterSuffix(newFile)

            if (counter == 1) {
                val nameSuffix = "_"
                fileNameBuilder.append(nameSuffix)
            }

            var zeroCounterPrefix = "0"

            if (counter !in 1..9)
                zeroCounterPrefix = ""

            val suffixPos = fileNameBuilder.lastIndexOf("_")
            fileNameBuilder.replace(suffixPos + 1, fileNameBuilder.length, "")

            fileNameBuilder.append(zeroCounterPrefix + counter).append(FILE_FORMAT)
            newFile = File(storageDir, fileNameBuilder.toString())

        } else {
            fileNameBuilder = StringBuilder(today)
            fileNameBuilder.append(".log")
            newFile = File(storageDir, fileNameBuilder.toString())
        }

        return newFile
    }

    /**
     * @return true if has space to new file, otherwise return false
     */
    private fun allocCacheSpace(): Boolean {
        if (currentCacheUsed <= cacheSize - fileSize)
            return true

        val listFile = storageDir.listFiles().sorted()
        val filesToDelete = ArrayList<File>()
        var totalCacheToDelete: Long = 0
        var totalCacheDeleted: Long = 0

        if (listFile == null || listFile.isEmpty())
            return true

        for (file in listFile) {
            totalCacheToDelete += Files.toByteArray(file).size.toLong()

            filesToDelete.add(file)
            if (totalCacheToDelete >= fileSize)
                break
        }

        for (file in filesToDelete) {
            var size: Long = 0
            try {
                size = Files.toByteArray(file).size.toLong()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            if (file.delete()) {
                totalCacheDeleted += size
            }
        }

        currentCacheUsed -= totalCacheDeleted

        return totalCacheDeleted >= fileSize
    }

    private fun isFileFilled(logMessage: LogMessage): Boolean {
        val message = logMessage.toString()
        val bytesLog = message.toByteArray(encoding)

        currentFileSize += bytesLog.size

        if (currentFileSize + bytesLog.size >= fileSize) {
            currentCacheUsed += currentFileSize
            currentFileSize = 0
            return true
        }

        return false
    }

    private fun getTotalCacheUsed(): Long {
        var totalUsed: Long = 0

        for (file in storageDir.listFiles()) {

            totalUsed += Files.toByteArray(file).size.toLong()
        }

        return totalUsed
    }

    private fun executeRotate(logMessage: LogMessage) {
        output.write(logMessage.toString() + newLine)
        output.close()

        while (!allocCacheSpace());

        val newFile = loadFile(storageDir, fileSize)
        val fos = FileOutputStream(newFile, true)
        val osw = OutputStreamWriter(fos, encoding)
        output = BufferedWriter(osw, BUFFER_SIZE)
    }

    private fun executeWrite(logMessage: WriteLogMessage) {
        output.write(logMessage.toString() + newLine)
    }

    private fun executeStop(logMessage: StopLogMessage) {
        output.write(logMessage.toString()+newLine)

        isRunning = false;
    }
}