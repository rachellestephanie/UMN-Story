package com.example.if570_lab_uts_rachellestephanierianto_00000082784

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : BaseAuthFragment() {
    private var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nameInput = view.findViewById<TextInputEditText>(R.id.name_input)
        val numberInput = view.findViewById<TextInputEditText>(R.id.number_input)
        val saveButton = view.findViewById<Button>(R.id.save_button)

        val nameDisplay = view.findViewById<TextView>(R.id.name_display)
        val numberDisplay = view.findViewById<TextView>(R.id.number_display)

        var nameData: String? = null
        var nimData: String? = null

        var profileDocRef: DocumentReference? = null

        db.collection("profile")
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    profileDocRef = documents.documents[0].reference
                    nameData = documents.documents[0].getString("name")
                    nimData = documents.documents[0].getString("nim")
                }
                nameDisplay?.text = nameData
                numberDisplay?.text = nimData
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error getting documents: ", e)
            }

        saveButton.setOnClickListener {
            val nameTitle = nameInput.text.toString()
            val numberTitle = numberInput.text.toString()
            Log.d("name title", nameTitle)
            Log.d("number title", numberTitle)
            if(nameTitle.isNotEmpty() && numberTitle.isNotEmpty()) {
                profileDocRef?.update("name", nameTitle, "nim", numberTitle)
                    ?.addOnSuccessListener { documentReference ->
                        Log.d(
                            "Firestore",
                            "DocumentSnapshot added with ID: $(documentReference.id)"
                        )

                        nameDisplay?.text = nameTitle
                        numberDisplay?.text = numberTitle
                    }
                    ?.addOnFailureListener{ e ->
                        Log.w("Firestore", "Error adding document", e)
                        Toast.makeText(
                            requireContext(),
                            "Error adding data",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                nameInput.text?.clear()
                numberInput.text?.clear()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Name or NIM can not be empty",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}