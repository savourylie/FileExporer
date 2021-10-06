package com.onionmonster.tutorial.fileexplorer

import android.content.Context
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.util.*

class FileAdapter(private val context: Context, private val file: List<File>): RecyclerView.Adapter<FileViewHolder>() {
    val TAG = "Dev/" + javaClass.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        return FileViewHolder(LayoutInflater.from(context).inflate(R.layout.file_container, parent, false))
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        holder.tvName.text = file[position].name
        holder.tvName.isSelected = true
        var items = 0

        if (file[position].isDirectory) {
            val files: Array<out File>? = file[position].listFiles()

            if (files != null) {
                for (singleFile in files) {
                    if (!singleFile.isHidden) {
                        items += 1
                    }
                }
            }

            val tvSizeText = "$items Files"
            holder.tvSize.text = tvSizeText
        } else {
            holder.tvSize.text = Formatter.formatShortFileSize(context, file[position].length())
        }

//        Log.d(TAG, file[position].name)

        if (file[position].name.lowercase(Locale.getDefault()).endsWith(".jpeg") ||
            file[position].name.lowercase(Locale.getDefault()).endsWith(".jpg")
        ) {
            holder.imgFile.setImageResource(R.drawable.jpg_wrapper)
        } else if (file[position].name.lowercase(Locale.getDefault()).endsWith(".png")) {
            holder.imgFile.setImageResource(R.drawable.png_wrapper)
        } else if (file[position].name.lowercase(Locale.getDefault()).endsWith(".pdf")) {
            holder.imgFile.setImageResource(R.drawable.pdf_wrapper)
        } else if (file[position].name.lowercase(Locale.getDefault()).endsWith(".doc")) {
            holder.imgFile.setImageResource(R.drawable.doc_wrapper)
        } else if (file[position].name.lowercase(Locale.getDefault()).endsWith(".mp3")) {
            holder.imgFile.setImageResource(R.drawable.mp3_wrapper)
        } else if (file[position].name.lowercase(Locale.getDefault()).endsWith(".mp4")) {
            holder.imgFile.setImageResource(R.drawable.mp4_wrapper)
        } else if (file[position].name.lowercase(Locale.getDefault()).endsWith(".wav")) {
            holder.imgFile.setImageResource(R.drawable.wav_wrapper)
        } else if (file[position].name.lowercase(Locale.getDefault()).endsWith(".apk")) {
            holder.imgFile.setImageResource(R.drawable.apk_wrapper)
        } else if (file[position].name.lowercase(Locale.getDefault()).endsWith(".epub")) {
            holder.imgFile.setImageResource(R.drawable.epub_wrapper)
        } else {
            holder.imgFile.setImageResource(R.drawable.folder_wrapper)
        }
    }

    override fun getItemCount(): Int {
        return file.size
    }

}