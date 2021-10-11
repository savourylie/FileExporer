package com.onionmonster.tutorial.fileexplorer

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException

class FileOpener {
    companion object {
        @Throws(IOException::class)
        fun openFile(context: Context, file: File) {
            val selectedFile: File = file
            val uri: Uri = FileProvider
                .getUriForFile(
                    context,
                    context.applicationContext.packageName + ".provider",
                    file
                )

            val intent: Intent = Intent(Intent.ACTION_VIEW)

            if (uri.toString().contains(".doc")) {
                intent.setDataAndType(uri, "application/msword")
            } else if (uri.toString().contains(".pdf")) {
                intent.setDataAndType(uri, "application/pdf")
            } else if (uri.toString().contains(".mp3") || uri.toString().contains(".wav")) {
                intent.setDataAndType(uri, "audio/x-wav")
            } else if (uri.toString().contains(".jpeg") || uri.toString().contains(".jpg")) {
                intent.setDataAndType(uri, "image/jpeg")
            } else if (uri.toString().contains(".mp4")) {
                intent.setDataAndType(uri, "video/*")
            } else {
                intent.setDataAndType(uri, "*/*")
            }

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            context.startActivity(intent)
        }
    }
}