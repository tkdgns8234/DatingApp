package com.hoon.datingapp.ui.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.hoon.datingapp.databinding.ActivityProfileBinding
import com.hoon.datingapp.util.Constants
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

/*
image crop with registerForActivityResult
https://github.com/CanHub/Android-Image-Cropper/issues/26
 */

class ProfileActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityProfileBinding.inflate(layoutInflater)
    }

    private var imageURI: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.imageBtnProfile.setOnClickListener { loadImage() }
        binding.floatingBtn.setOnClickListener { loadImage() }
        binding.btnSave.setOnClickListener { initSaveBtn() }
    }

    private fun initSaveBtn() {
        val name = binding.etName.text.toString()
        val uri = imageURI?.toString()

        if (name.isNotEmpty() && uri != null) {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra(Constants.INTENT_KEY_PROFILE_NAME, name)
                putExtra(Constants.INTENT_KEY_PROFILE_IMAGE_URI, uri)
            }
            setResult(RESULT_OK, intent)
            finish()
        } else {
            Toast.makeText(this, "이미지와 이름을 모두 입력해주세요", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        galleryImageLauncher.launch(intent)
    }

    private val galleryImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK && it.data != null) {
                val uri = it.data!!.data
                cropImage(uri)

                Glide
                    .with(this)
                    .load(imageURI)
                    .into(binding.imageBtnProfile)
            }
        }

    private fun cropImage(uri: Uri?) {
        val intent = CropImage
            .activity(uri) // crop 이미지 uri 지정
            .setCropShape(CropImageView.CropShape.RECTANGLE) // crop 모양 지정
            .getIntent(this)

        cropImageLauncher.launch(intent)
    }

    private var cropImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            CropImage.getActivityResult(result.data)?.let { cropResult ->
                imageURI = cropResult.uri
            }
        }
    }
}