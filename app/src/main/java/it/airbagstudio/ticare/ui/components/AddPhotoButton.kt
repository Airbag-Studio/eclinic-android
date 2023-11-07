package it.airbagstudio.ticare.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import it.airbagstudio.ticare.BuildConfig
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.theme.AppTheme
import it.airbagstudio.ticare.utils.createImageFile
import it.airbagstudio.ticare.utils.resized
import java.util.Objects


private enum class ImageSource{
    GALLERY,CAMERA
}

@Composable
fun AddPhotoButton(modifier: Modifier = Modifier,onSuccess: (List<Bitmap>) -> Unit) {
    var showChooserDialog by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    val file = remember {
        context.createImageFile()
    }
    val uri = remember {
        FileProvider.getUriForFile(
            Objects.requireNonNull(context),
            BuildConfig.APPLICATION_ID + ".provider", file
        )
    }

    var capturedImageUri by remember {
        mutableStateOf<Uri>(Uri.EMPTY)
    }

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uriList ->
            onSuccess(uriList.map { BitmapFactory.decodeFile(uri.path).resized() })
        }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
            capturedImageUri = uri
            if (it) {
                onSuccess(listOf(BitmapFactory.decodeFile(file.path).resized() ))
            }
            /*
            capturedImageUri = uri
            val bitmap = BitmapFactory.decodeFile(file.path)
            try {
                val quality = 70
                val fos = FileOutputStream(context.createImageFile(true))
                bitmap.resized().compress(Bitmap.CompressFormat.JPEG, quality, fos)
                fos.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

             */
        }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    OutlinedButton(
        modifier = modifier,
        onClick = {
            showChooserDialog = true
        }) {
        Icon(imageVector = Icons.Default.Add, contentDescription = "")
        Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
        Text(
            text = stringResource(id = R.string.photo),
            style = MaterialTheme.typography.labelLarge
        )
    }
    if (showChooserDialog) {
        CameraOrGalleryDialog {
            showChooserDialog = false
            if (it == ImageSource.CAMERA){
                val permissionCheckResult =
                    ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                    cameraLauncher.launch(uri)
                } else {
                    // Request a permission
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }else{
                galleryLauncher.launch("image/*")
            }
        }
    }
}

@Composable
private fun CameraOrGalleryDialog(onDismissRequest: (ImageSource?) -> Unit) {
    Dialog(onDismissRequest = {
        onDismissRequest(null)
    }) {
        Column(
            Modifier
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(16.dp)) {
            Text(
                text = stringResource(id = R.string.image_source),
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(modifier = Modifier.fillMaxWidth(), onClick = {
                onDismissRequest(ImageSource.CAMERA)
            }) {
                Text(text = stringResource(id = R.string.camera))
            }
            Button(modifier = Modifier.fillMaxWidth(), onClick = {
                onDismissRequest(ImageSource.GALLERY)
            }) {
                Text(text = stringResource(id = R.string.gallery))
            }
        }
    }
}

@Composable
@Preview
private fun PreviewAddPhotoButton() {
    AppTheme {
        Column(
            Modifier
                .background(Color.White)
                .padding(16.dp)
        ) {
            AddPhotoButton(){

            }
        }
    }
}