package com.onionmonster.tutorial.fileexplorer

import java.io.File

interface OnFileSelectedListener {
     fun onFileClicked(file: File)
     fun onFileLongClicked(file: File, position: Int)
}