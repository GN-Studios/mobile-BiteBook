package com.example.bitebook.data.local

import com.example.bitebook.data.local.dao.ImageCacheDao
import com.example.bitebook.data.local.entity.ImageCacheEntity
import java.io.File
import java.net.HttpURLConnection
import java.net.URI
import java.security.MessageDigest

class ImageCacheStore(
    private val imageCacheDao: ImageCacheDao,
    private val cacheDir: File
) {
    suspend fun cacheImage(remoteUrl: String): String? {
        if (remoteUrl.isBlank()) {
            return null
        }

        val existing = imageCacheDao.getByUrl(remoteUrl)
        if (existing != null && File(existing.localPath).exists()) {
            return existing.localPath
        }

        val file = File(cacheDir, buildFileName(remoteUrl))
        download(remoteUrl, file)

        imageCacheDao.upsert(
            ImageCacheEntity(
                remoteUrl = remoteUrl,
                localPath = file.absolutePath,
                lastFetchedAt = System.currentTimeMillis()
            )
        )

        return file.absolutePath
    }

    private fun download(remoteUrl: String, target: File) {
        val connection = URI(remoteUrl).toURL().openConnection() as HttpURLConnection
        connection.connectTimeout = 10_000
        connection.readTimeout = 10_000
        connection.instanceFollowRedirects = true

        connection.inputStream.use { input ->
            target.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }

    private fun buildFileName(remoteUrl: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
            .digest(remoteUrl.toByteArray())
            .joinToString("") { "%02x".format(it) }
        val extension = remoteUrl.substringAfterLast('.', "")
            .substringBefore('?')
            .takeIf { it.length in 2..5 }

        return if (extension == null) digest else "$digest.$extension"
    }
}
