// HomeFragment.kt
package com.example.if570_lab_uts_rachellestephanierianto_00000082784

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var storyAdapter: StoryAdapter
    private val stories = mutableListOf<Story>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        storyAdapter = StoryAdapter(stories)
        recyclerView.adapter = storyAdapter

        loadStories()
    }

    private fun loadStories() {
        FirebaseFirestore.getInstance().collection("stories")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                stories.clear()
                for (document in result) {
                    val caption = document.getString("caption") ?: ""
                    val imageUrl = document.getString("imageUrl")
                    val timestamp = document.getTimestamp("timestamp")
                    stories.add(Story(caption, imageUrl, timestamp))
                }
                storyAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                // Handle the error
            }
    }
}