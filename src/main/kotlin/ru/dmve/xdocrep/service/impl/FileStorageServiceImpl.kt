package ru.dmve.xdocrep.service.impl

import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import ru.dmve.xdocrep.properties.FileStorageProperties
import ru.dmve.xdocrep.service.FileStorageService
import ru.dmve.xdocrep.service.ex.FileStorageException
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption


@Service
class FileStorageServiceImpl(fileStorageProperties: FileStorageProperties): FileStorageService {

    var fileStorageLocation: Path? = null

    override fun storeFile(file: MultipartFile): String {
        // Normalize file name
        val fileName: String = StringUtils.cleanPath(file.originalFilename!!)
        return try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw FileStorageException("Sorry! Filename contains invalid path sequence $fileName")
            }

            // Copy file to the target location (Replacing existing file with the same name)
            val targetLocation: Path = fileStorageLocation!!.resolve(fileName)
            Files.copy(file.inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING)
            fileName
        } catch (ex: IOException) {
            throw FileStorageException("Could not store file $fileName. Please try again!", ex)
        }
    }

    init {
        fileStorageLocation = Paths.get(fileStorageProperties.uploadDir!!)
                .toAbsolutePath().normalize()
        try {
            Files.createDirectories(fileStorageLocation)
        } catch (ex: Exception) {
            throw FileStorageException("Could not create the directory where the uploaded files will be stored.", ex)
        }
    }
}