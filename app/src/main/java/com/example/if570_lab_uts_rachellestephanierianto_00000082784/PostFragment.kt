package com.example.if570_lab_uts_rachellestephanierianto_00000082784

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class PostFragment : BaseAuthFragment() {
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val captionEditText = view?.findViewById<TextInputEditText>(R.id.caption_input)
        val storyImagePlaceholder = view?.findViewById<ImageButton>(R.id.add_picture)
        val uploadButton = view?.findViewById<Button>(R.id.upload_button)
        val selectedImageView = view?.findViewById<ImageView>(R.id.picture)

        val IMAGE_PICK_CODE = 1000

        storyImagePlaceholder?.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

        uploadButton?.setOnClickListener {
            val caption = captionEditText?.text.toString().trim()
            if(caption.isNotEmpty()) {
                if (imageUri == null) {
                    saveStoryToFirestore(caption, null)
                } else {
                    uploadImageAndSaveStory(caption, imageUri!!)
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Caption tidak boleh kosong",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 1000) {
            imageUri = data?.data
            Log.d("ImageURI", "Selected image URI: $imageUri")
            view?.findViewById<ImageView>(R.id.picture)?.setImageURI(imageUri)
        }
    }

    private fun saveStoryToFirestore(caption: String, imageUrl: String?) {
        val storyData = hashMapOf(
            "caption" to caption,
            "imageUrl" to imageUrl,
            "timestamp" to com.google.firebase.Timestamp.now()
        )

        FirebaseFirestore.getInstance().collection("stories")
            .add(storyData)
            .addOnSuccessListener {
                Toast.makeText(
                    requireContext(),
                    "Cerita berhasil diunggah",
                    Toast.LENGTH_SHORT
                ).show()
                view?.findViewById<TextInputEditText>(R.id.caption_input)?.text?.clear()
                view?.findViewById<ImageView>(R.id.picture)?.setImageResource(0)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Gagal menyimpan cerita", e)
                Toast.makeText(requireContext(), "Gagal menyimpan cerita", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadImageAndSaveStory(story: String, imageUri: Uri) {
        val storageRef = FirebaseStorage.getInstance().reference.child("images/${UUID.randomUUID()}")
        val uploadTask = storageRef.putFile(imageUri)

        uploadTask.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString()
                saveStoryToFirestore(story, imageUrl)
            }.addOnFailureListener { e ->
                Log.e("Firebase", "Gagal mendapatkan URL gambar", e)
                Toast.makeText(requireContext(), "Gagal mendapatkan URL gambar", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Log.e("Firebase", "Gagal mengunggah gambar", e)
            Toast.makeText(requireContext(), "Gagal mengunggah gambar", Toast.LENGTH_SHORT).show()
        }
    }

}