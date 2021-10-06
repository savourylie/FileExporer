package com.onionmonster.tutorial.fileexplorer.fragments

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.karumi.dexter.Dexter
import com.karumi.dexter.DexterBuilder
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.onionmonster.tutorial.fileexplorer.FileAdapter
import com.onionmonster.tutorial.fileexplorer.R
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class InternalFragment: Fragment() {

    val TAG = "Dev/" + javaClass.simpleName

    private lateinit var recyclerView: RecyclerView
    private lateinit var fileAdapter: FileAdapter
    private lateinit var fileList: List<File>
    private lateinit var imgBack: ImageView
    private lateinit var tvPathHolder: TextView
    lateinit var storage: File
    lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        Log.d(TAG, "onCreateView")

        root = inflater.inflate(R.layout.fragment_internal, container, false)

        tvPathHolder = root.findViewById(R.id.tv_path_holder)
        imgBack = root.findViewById(R.id.image_back)

        val internalStorage: String = System.getenv("EXTERNAL_STORAGE") // Internal storage is called EXTERNAL STORAGE on Android WTF
        storage = File(internalStorage)

        tvPathHolder.text = storage.absolutePath

        runtimePermission()

        return root
    }

    fun runtimePermission() {
        Dexter.withContext(context)
            .withPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object: MultiplePermissionsListener {
                override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport) {
                    displayFiles()
                }

                override fun onPermissionRationaleShouldBeShown(
                    list: List<PermissionRequest>,
                    permissionToken: PermissionToken
                ) {
                    permissionToken.continuePermissionRequest()
                }
        }).check()
    }

    fun findFiles(file: File): ArrayList<File> {
        Log.d(TAG, Throwable().stackTrace[0].methodName)

        val arrayList: ArrayList<File> = ArrayList()

        val files: Array<File>? = file.listFiles()

        if (files != null) {
            for (singleFile in files) {
                Log.d(TAG, "No filter: $singleFile")
            }

            for (singleFile in files) {
                if (singleFile.isDirectory && !singleFile.isHidden) {
                    arrayList.add(singleFile)
                }
            }

            for (singleFile in files) {
                if (
                    singleFile.name.lowercase(Locale.getDefault()).endsWith(".jpeg") ||
                    singleFile.name.lowercase(Locale.getDefault()).endsWith(".jpg") ||
                    singleFile.name.lowercase(Locale.getDefault()).endsWith(".png") ||
                    singleFile.name.lowercase(Locale.getDefault()).endsWith(".mp3") ||
                    singleFile.name.lowercase(Locale.getDefault()).endsWith(".wav") ||
                    singleFile.name.lowercase(Locale.getDefault()).endsWith(".mp4") ||
                    singleFile.name.lowercase(Locale.getDefault()).endsWith(".pdf") ||
                    singleFile.name.lowercase(Locale.getDefault()).endsWith(".doc") ||
                    singleFile.name.lowercase(Locale.getDefault()).endsWith(".epub") ||
                    singleFile.name.toLowerCase().endsWith(".apk")
                ) {
                    arrayList.add(singleFile)
                }
            }
        }
        return arrayList
    }

    private fun displayFiles() {
        recyclerView = root.findViewById(R.id.recycler_internal)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        fileList = ArrayList()
        (fileList as ArrayList<File>).addAll(findFiles(storage))
        fileAdapter = FileAdapter(requireContext(), fileList)
        recyclerView.adapter = fileAdapter
    }
}