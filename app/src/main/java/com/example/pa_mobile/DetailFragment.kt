package com.example.pa_mobile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import com.example.pa_mobile.databinding.DetailKomikBinding
import com.example.pa_mobile.KatalogFragment
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class DetailFragment : Fragment() {
    private var _binding: DetailKomikBinding? = null
    private val binding get() = _binding!!
    private lateinit var favoriteViewModel: FavoriteViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DetailKomikBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoriteViewModel = ViewModelProvider(this).get(FavoriteViewModel::class.java)


        binding.buttonBackToKatalog.setOnClickListener {
            val action = DetailFragmentDirections.actionDetailFragmentToKatalogFragment(0)
            findNavController().navigate(action)
        }
        val komikId = arguments?.getString("komikId")
        val client = OkHttpClient()

        val requestDetail = Request.Builder()
            .url("https://mangaverse-api.p.rapidapi.com/manga?id=$komikId")
            .get()
            .addHeader("X-RapidAPI-Key", Constants.RAPID_API_KEY)
            .addHeader("X-RapidAPI-Host", "mangaverse-api.p.rapidapi.com")
            .build()

        client.newCall(requestDetail).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                Log.i("Response", "Received Response from server")
                response.use {
                    if (!response.isSuccessful){
                        Log.e("Http Error", "someting didnt load")
                    }else{
                        val body = response.body()?.string()
                        val komikResponse = Gson().fromJson(body, DetailResponse::class.java)
                        val komikDetail = komikResponse.data
                        requireActivity().runOnUiThread {
                            binding.judulKomik.text = komikDetail.title
                            binding.statusKomik.text = komikDetail.status
                            binding.summaryKomik.text = komikDetail.summary
                            binding.authorsKomik.text = komikDetail?.authors?.joinToString(", ") ?: ""
                            binding.genresKomik.text = komikDetail?.genres?.joinToString(", ") ?: ""
                            binding.totalChapter.text = komikDetail.totalChapter.toString()

                            val totalChapter = komikDetail.totalChapter
                            val thumbLink = komikDetail.thumb
                            val title = komikDetail.title

                            binding.buttonFavorite.setOnClickListener {
                                val favorite = Favorite(komikId.toString(), title, totalChapter, thumbLink)
                                GlobalScope.launch(Dispatchers.IO) {
                                    favoriteViewModel.insert(favorite)
                                }
                                Snackbar.make(binding.root, "Komik ditambahkan ke favorit", Snackbar.LENGTH_LONG).show()
                            }

                            Glide.with(requireContext())
                                .load(komikDetail.thumb) // URL gambar
                                .placeholder(R.drawable.wp6682518_kato_megumi_wallpapers2) // Placeholder sementara
                                .error(R.drawable.email) // Gambar yang akan ditampilkan jika terjadi kesalahan
                                .into(binding.thumbKomik) // ImageView target
                        }
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace();
            }
        })

        val request = Request.Builder()
            .url("https://mangaverse-api.p.rapidapi.com/manga/chapter?id=$komikId")
            .get()
            .addHeader("X-RapidAPI-Key", Constants.RAPID_API_KEY)
            .addHeader("X-RapidAPI-Host", "mangaverse-api.p.rapidapi.com")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                Log.i("Response", "Received Response from server")
                response.use {
                    if (!response.isSuccessful){
                        Log.e("Http Error", "someting didnt load")
                    }else{
                        val body = response.body()?.string()
                        val chapterResponse = Gson().fromJson(body, ChapterResponse::class.java)
                        val chapterList = chapterResponse.data
//                        for (chapter in chapterList) {
//                            //binding.textView.text = manga.title
//                            println(chapter.id)
//                        }
                        val verAdapter = ChapterAdapter(requireContext(), chapterList)
                        requireActivity().runOnUiThread {
                            binding.listChapter.adapter = verAdapter
                        }
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace();
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}




