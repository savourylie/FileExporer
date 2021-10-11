package com.onionmonster.tutorial.fileexplorer.fragments

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.format.Formatter.formatShortFileSize
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.onionmonster.tutorial.fileexplorer.FileAdapter
import com.onionmonster.tutorial.fileexplorer.FileOpener
import com.onionmonster.tutorial.fileexplorer.OnFileSelectedListener
import com.onionmonster.tutorial.fileexplorer.R
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class CardFragment: Fragment(), OnFileSelectedListener {

    val TAG = "Dev/" + javaClass.simpleName

    private lateinit var recyclerView: RecyclerView
    private lateinit var fileAdapter: FileAdapter
    private lateinit var fileList: List<File>
    private lateinit var imgBack: ImageView
    private lateinit var tvPathHolder: TextView
    lateinit var storage: File
    var data = ""
    val items = arrayOf("Details", "Rename", "Delete", "Share")
    private var secStorage = ""

    lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        Log.d(TAG, "onCreateView")

        root = inflater.inflate(R.layout.fragment_card, container, false)

        tvPathHolder = root.findViewById(R.id.tv_path_holder)
        imgBack = root.findViewById(R.id.image_back)

        val externalCacheDirs = context!!.externalCacheDirs

        for (file in externalCacheDirs) {
            if (Environment.isExternalStorageRemovable(file)) {
                secStorage = file.path.split("/Android")[0]
            }
        }

        storage = File(secStorage!!)

        try {
            data = requireArguments().getString("path")!!
            val file = File(data)
            storage = file
        } catch (e: Exception) {
            e.printStackTrace()
        }

        tvPathHolder.text = storage.absolutePath

        runtimePermission()

        return root
    }

    private fun runtimePermission() {
        Dexter.withContext(context)
            .withPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
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
        recyclerView.layoutManager = GridLayoutManager(context, 1)
        fileList = ArrayList()
        (fileList as ArrayList<File>).addAll(findFiles(storage))
        fileAdapter = FileAdapter(requireContext(), fileList, this)
        recyclerView.adapter = fileAdapter
    }

    override fun onFileClicked(file: File) {
        if (file.isDirectory) {
            val bundle: Bundle = Bundle()
            bundle.putString("path", file.absolutePath)
            val internalFragment = CardFragment()
            internalFragment.arguments = bundle
            fragmentManager?.beginTransaction()?.replace(R.id.fragment_container, internalFragment)
                ?.addToBackStack(null)
                ?.commit()
        } else {
            try {
                context?.let { FileOpener.openFile(it, file) }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onFileLongClicked(file: File, position: Int) {
        val optionDialog = Dialog(requireContext())
        optionDialog.setContentView(R.layout.option_dialog)
        optionDialog.setTitle("Select Options.")
        val options = optionDialog.findViewById<ListView>(R.id.list)

        val customAdapter = CustomAdapter()
        options.adapter = customAdapter
        optionDialog.show()

        options.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position).toString()

            when (selectedItem) {
                "Details" -> {
                    val detailDialog = AlertDialog.Builder(context)
                    detailDialog.setTitle("Details")
                    val details = TextView(context)
                    detailDialog.setView(details)
                    val lastModified = Date(file.lastModified())
                    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

                    val formattedDate = formatter.format(lastModified)
                    details.text = "File Name " + file.name + "\n" +
                            "Size: " + formatShortFileSize(context, file.length()) + "\n" +
                            "Path: " + file.absolutePath + "\n" +
                            "Last Modified: " + formattedDate

                    detailDialog.setPositiveButton(
                        "OK"
                    ) { _, _ -> optionDialog.cancel() }

                    val alertDialogDetails = detailDialog.create()
                    alertDialogDetails.show()
                }

                "Rename" -> {
                    val renameDialog = AlertDialog.Builder(context)
                    renameDialog.setTitle("Rename Files:")
                    val name = EditText(context)
                    renameDialog.setView(name)

                    renameDialog.setPositiveButton("OK") { _, _ ->
                        val newName= name.editableText.toString()
                        val extension = file.absolutePath.substring(file.absolutePath.lastIndexOf("."))
                        val current = File(file.absolutePath)
                        val destination = File(file.absolutePath.replace(file.name, newName) + extension)

                        if (current.renameTo(destination)) {
                            (fileList as ArrayList)[position] = destination
                            fileAdapter.notifyItemChanged(position)
//                            fileAdapter.notifyItemRemoved(position)
//                            fileAdapter.notifyItemInserted(position)
                            Toast.makeText(context, "Renamed!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Couldn't rename!", Toast.LENGTH_SHORT).show()
                        }
                    }

                    renameDialog.setNegativeButton("Cancel") { _, _ ->
                        optionDialog.cancel()
                    }

                    val alertDialogRename = renameDialog.create()
                    alertDialogRename.show()
                }

                "Delete" -> {
                    val deleteDialog = AlertDialog.Builder(context)
                    deleteDialog.setTitle("Delete" + file.name + "?")

                    deleteDialog.setPositiveButton("OK") { _, _ ->
                        file.delete()
                        (fileList as ArrayList).removeAt(position)
                        fileAdapter.notifyDataSetChanged()
                        Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show()
                    }

                    deleteDialog.setNegativeButton("No") { _, _ ->
                        optionDialog.cancel()
                    }

                    val alertDialogDelete = deleteDialog.create()
                    alertDialogDelete.show()
                }

                "Share" -> {
                    val fileName = file.name
                    Log.d(TAG, fileName)
                    val share = Intent()
                    share.action = Intent.ACTION_SEND
                    share.type = "image/jpeg"

                    val uri = if (Build.VERSION.SDK_INT < 24) {
                        Uri.fromFile(file)
                    } else {
                        Uri.parse(file.path) // My work-around for new SDKs, worked for me in Android 10 using Solid Explorer Text Editor as the external editor.
                    }
                    
                    share.putExtra(Intent.EXTRA_STREAM, uri)
                    startActivity(Intent.createChooser(share, "Share $fileName"))
                }
            }
        }

    }

    inner class CustomAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return items.size
        }

        override fun getItem(position: Int): Any {
            return items[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val myView = getLayoutInflater().inflate(R.layout.option_layout, null)
            val txtOptions = myView.findViewById<TextView>(R.id.txt_option)
            val imgOptions = myView.findViewById<ImageView>(R.id.img_option)

            txtOptions.text = items[position]

            when (items[position]) {
                "Details" -> imgOptions.setImageResource(R.drawable.details_wrapper)
                "Rename" -> imgOptions.setImageResource(R.drawable.edit_wrapper)
                "Delete" -> imgOptions.setImageResource(R.drawable.delete_wrapper)
                "Share" -> imgOptions.setImageResource(R.drawable.share_wrapper)
            }

            return myView
        }
    }
}