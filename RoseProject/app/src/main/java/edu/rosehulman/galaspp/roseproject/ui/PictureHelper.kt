package edu.rosehulman.galaspp.roseproject.ui
import android.app.Activity.RESULT_OK
import androidx.fragment.app.FragmentActivity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import edu.rosehulman.galaspp.roseproject.Constants
import edu.rosehulman.galaspp.roseproject.ui.profile.ProfileFragment
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

private const val RC_TAKE_PICTURE = 1
private const val RC_CHOOSE_PICTURE = 2

class PictureHelper(
        var context: Context,
        var activity: FragmentActivity,
        var fragment: Fragment,
        var listener: PictureListener
) {
    private var currentPhotoPath = ""

    private fun showPictureDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Choose a photo source")
        builder.setMessage("Would you like to take a new picture?\nOr choose an existing one?")
        builder.setPositiveButton("Take Picture") { _, _ ->
            launchCameraIntent()
        }

        builder.setNegativeButton("Choose Picture") { _, _ ->
            launchChooseIntent()
        }
        builder.create().show()
    }

    // Everything camera- and storage-related is from
    // https://developer.android.com/training/camera/photobasics
    private fun launchCameraIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(activity.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                        Log.e(Constants.TAG, ex.toString())
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    // authority declared in manifest
                    val photoURI: Uri = FileProvider.getUriForFile(
                            context,
                            "edu.rosehulman.galaspp.roseproject",
                            it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    fragment.startActivityForResult(takePictureIntent, RC_TAKE_PICTURE)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun launchChooseIntent() {
        // https://developer.android.com/guide/topics/providers/document-provider
        val choosePictureIntent = Intent(
                Intent.ACTION_OPEN_DOCUMENT,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        choosePictureIntent.addCategory(Intent.CATEGORY_OPENABLE)
        choosePictureIntent.type = "image/*"
        if (choosePictureIntent.resolveActivity(context.packageManager) != null) {
//            Log.d(Constants.TAG, "Start activity")
            fragment.startActivityForResult(choosePictureIntent, RC_CHOOSE_PICTURE)
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                RC_TAKE_PICTURE -> {
                    sendCameraPhotoToAdapter()
                }
                RC_CHOOSE_PICTURE -> {
                    sendGalleryPhotoToAdapter(data)
                }
            }
        }
    }

    private fun sendCameraPhotoToAdapter() {
        addPhotoToGallery()
        listener.getPictureTask(currentPhotoPath)
    }

    private fun sendGalleryPhotoToAdapter(data: Intent?) {
        if (data != null && data.data != null) {
            val location = data.data!!.toString()
            listener.getPictureTask(location)
        }
    }

    // Works Not working on phone
    private fun addPhotoToGallery() {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(currentPhotoPath)
            mediaScanIntent.data = Uri.fromFile(f)
            activity.sendBroadcast(mediaScanIntent)
        }
    }

    fun getPicture() {
        showPictureDialog()
    }

    interface PictureListener{
        fun getPictureTask(currentPhotoPath: String)
    }
}