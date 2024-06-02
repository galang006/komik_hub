package com.example.pa_mobile

import android.Manifest
import android.content.pm.PackageManager
import android.location.LocationManager
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.example.pa_mobile.databinding.KatalogBinding
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class KatalogFragment : Fragment() {

    private var _binding: KatalogBinding? = null
    private val binding get() = _binding!!
    private lateinit var locationManager: LocationManager
    private val requestCode = 123

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = KatalogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideSystemUI()
        getLocation()
    }

    private fun getLocation() {
        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isGpsEnabled && !isNetworkEnabled) {
            Toast.makeText(requireContext(), "Aktifkan GPS untuk menggunakan fitur ini", Toast.LENGTH_SHORT).show()
        } else {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), requestCode)
            } else {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    5000,
                    0f,
                    locationListener
                )
            }
        }
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val latitude = location.latitude
            val longitude = location.longitude

            println("Latitude: ${location.latitude}, Longitude: ${location.longitude}")
            // Cek apakah lokasi berada di Indonesia
            if (isLocationInIndonesia(latitude, longitude)) {
                // Lokasi berada di Indonesia, lanjutkan pemanggilan API
                callApi()
            } else {
                // Lokasi tidak berada di Indonesia
                Toast.makeText(requireContext(), "Maaf, fitur ini hanya tersedia di Indonesia", Toast.LENGTH_SHORT).show()
            }
            // Hentikan pemantauan lokasi setelah mendapatkan lokasi
            locationManager.removeUpdates(this)
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    }

    private fun isLocationInIndonesia(latitude: Double, longitude: Double): Boolean {
        // Cek koordinat yang masuk ke dalam wilayah Indonesia
        //return latitude in -11.0..40.0 && longitude in -195.0..141.0
        return latitude in -11.0..6.0 && longitude in 95.0..141.0
    }
    private fun callApi() {
        val client = OkHttpClient()
        val page = 1 + (arguments?.getInt("page") ?: 0) // Ambil nilai argument "page", default ke 1 jika null

        println(page)
        if (page >= 2) {
            binding.buttonPrev.visibility = View.VISIBLE
        } else {
            binding.buttonPrev.visibility = View.GONE
        }
        
        binding.buttonToFavorite.setOnClickListener{
            val action = KatalogFragmentDirections.actionKatalogFragmentToFavoriteFragment()
            findNavController().navigate(action)
        }

        binding.buttonPrev.setOnClickListener {
            val action = KatalogFragmentDirections.actionKatalogFragmentSelf(page-2)
            findNavController().navigate(action)
        }

        binding.buttonNext.setOnClickListener {
            val action = KatalogFragmentDirections.actionKatalogFragmentSelf(page)
            findNavController().navigate(action)
        }

        val request = Request.Builder()
            .url("https://mangaverse-api.p.rapidapi.com/manga/latest?page=$page&nsfw=false&type=all")
            .get()
            .addHeader("X-RapidAPI-Key", Constants.RAPID_API_KEY)
            .addHeader("X-RapidAPI-Host", "mangaverse-api.p.rapidapi.com")
            .build()

        client.newCall(request).enqueue(object : Callback{
            override fun onResponse(call: Call, response: Response) {
                Log.i("Response", "Received Response from server")
                response.use {
                    if (!response.isSuccessful){
                        Log.e("Http Error", "someting didnt load")
                    }else{
                        val body = response.body()?.string()
                        val mangaResponse = Gson().fromJson(body, KomikResponse::class.java)
                        val mangaList = mangaResponse.data
//                        val adapter = HorizontalAdapter(requireContext(), mangaList)
                        val verAdapter = KatalogAdapter(requireContext(), mangaList)

                        requireActivity().runOnUiThread {
                            binding.verticalView.adapter = verAdapter
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

    private fun hideSystemUI() {
        activity?.window?.decorView?.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                windowInsetsController?.hide(android.view.WindowInsets.Type.statusBars() or android.view.WindowInsets.Type.navigationBars())
                windowInsetsController?.systemBarsBehavior =
                    android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            } else {
                @Suppress("DEPRECATION")
                systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Implementasi aksi setelah izin lokasi diberikan
    }

}
