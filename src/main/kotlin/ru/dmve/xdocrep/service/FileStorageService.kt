package ru.dmve.xdocrep.service

import org.springframework.web.multipart.MultipartFile

interface FileStorageService {
    fun storeFile(file: MultipartFile): String
}