package com.example.pa_mobile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.pa_mobile.databinding.DetailChapterBinding
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import androidx.recyclerview.widget.LinearLayoutManager

class ChapterFragment : Fragment(){
    private var _binding: DetailChapterBinding? = null
    private val binding get() = _binding!!

    private var currentIndex: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DetailChapterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val chapterId = arguments?.getString("chapterId")
        val komikId = arguments?.getString("komikId").toString()
        val chapters: Array<Chapter>? = arguments?.getParcelableArray("chapterList")?.map { it as Chapter }?.toTypedArray()
        val index = arguments?.getInt("index")

        println("ini KomikId" + komikId)

        binding.buttonBackToDetail.setOnClickListener {
            val action = ChapterFragmentDirections.actionChapterFragmentToDetailFragment(komikId)
            findNavController().navigate(action)
        }

        currentIndex = index?: 0
        if (chapters != null && currentIndex < chapters.size-1) {
            binding.buttonNextChapter.visibility = View.VISIBLE
        }
        else {
            binding.buttonNextChapter.visibility = View.GONE
        }

        binding.chapterTitle.text = chapters?.getOrNull(currentIndex)?.title ?: "Chapter"

        binding.buttonNextChapter.setOnClickListener{
            println("Index" + index)
            println("Curr Index " + currentIndex)
            nextChapter(chapters)
        }

        binding.buttonPrevChapter.setOnClickListener{
            println("Index" + index)
            println("Curr Index " + currentIndex)
            previousChapter(chapters)
        }
        requestChapterImage(chapterId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun requestChapterImage(chapterId: String?) {
        if (currentIndex > 0) {
            binding.buttonPrevChapter.visibility = View.VISIBLE
        }
        else {
            binding.buttonPrevChapter.visibility = View.GONE
        }

        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://mangaverse-api.p.rapidapi.com/manga/image?id=$chapterId")
            .get()
            .addHeader("X-RapidAPI-Key", Constants.RAPID_API_KEY)
            .addHeader("X-RapidAPI-Host", "mangaverse-api.p.rapidapi.com")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                Log.i("Response", "Received Response from server")
                response.use {
                    if (!response.isSuccessful){
                        Log.e("Http Error", "Something didn't load")
                    } else {
                        val body = response.body()?.string()
                        val chapterResponse = Gson().fromJson(body, ChapterFetchResponse::class.java)
                        val chapterImageList = chapterResponse.data
                        // Set adapter untuk RecyclerView
                        val verAdapter = ChapterImageAdapter(chapterImageList)
                        requireActivity().runOnUiThread {
                            binding.recyclerViewImages.layoutManager = LinearLayoutManager(requireContext())
                            binding.recyclerViewImages.adapter = verAdapter
                        }
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace();
            }
        })
    }

    private fun nextChapter(chapters: Array<Chapter>?) {
        if (chapters != null) {
            if (currentIndex < chapters.size) {
                currentIndex++
                if (currentIndex < chapters.size) {
                    binding.buttonNextChapter.visibility = View.VISIBLE
                }
                else {
                    binding.buttonNextChapter.visibility = View.GONE
                }
                val nextChapterId = chapters[currentIndex].id
                requestChapterImage(nextChapterId)
                binding.chapterTitle.text = chapters[currentIndex].title
            }
        }
    }

    private fun previousChapter(chapters: Array<Chapter>?) {
        if (chapters != null) {
            if (currentIndex > 0 && currentIndex <= chapters.size) {
                currentIndex--
                val nextChapterId = chapters[currentIndex].id
                requestChapterImage(nextChapterId)
                binding.chapterTitle.text = chapters[currentIndex].title
            }
        }
    }
}

