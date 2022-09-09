package com.hoon.datingapp.presentation.view.profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.hoon.datingapp.databinding.ActivityProfileBinding
import com.hoon.datingapp.extensions.toast
import com.hoon.datingapp.presentation.view.main.MainActivity
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

/*
image crop with registerForActivityResult
https://github.com/CanHub/Android-Image-Cropper/issues/26
 */

class ProfileActivity : AppCompatActivity() {

    companion object {
        fun newIntent(context: Context) =
            Intent(context, ProfileActivity::class.java)
    }

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
            }
        }

    private fun cropImage(uri: Uri?) {
        val intent =
            CropImage
                .activity(uri) // crop 이미지 uri 지정
                .setCropShape(CropImageView.CropShape.RECTANGLE) // crop 모양 지정
                .getIntent(this)

        cropImageLauncher.launch(intent)
    }

    private fun initSaveBtn() {
        val name = binding.etName.text.toString()
        val uri = imageURI?.toString()

        if (name.isNotEmpty() && uri != null) {
            val intent = MainActivity.newIntent(this, name, uri)
            setResult(RESULT_OK, intent)
            finish()
        } else {
            toast("이미지와 이름을 모두 입력해주세요")
        }
    }

    private var cropImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            CropImage.getActivityResult(result.data)?.let { cropResult ->
                imageURI = cropResult.uri

                Glide
                    .with(this)
                    .load(imageURI)
                    .into(binding.imageBtnProfile)
            }
        }
    }
}