package com.example.pa_mobile

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class KatalogAdapter(context: Context, mangas: List<Komik>) :
    ArrayAdapter<Komik>(context, 0, mangas) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItemView = convertView
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(R.layout.list_item_manga, parent, false)
        }

        val currentManga = getItem(position)

        val titleTextView = listItemView!!.findViewById<TextView>(R.id.titleTextView)
        val chaptersTextView = listItemView!!.findViewById<TextView>(R.id.chaptersTextView)
        val thumImageView = listItemView!!.findViewById<ImageView>(R.id.imageThum)
        titleTextView.text = currentManga?.title
        chaptersTextView.text = "Chapter " + currentManga?.totalChapter.toString()

        Glide.with(context)
            .load(currentManga?.thumb)
            .placeholder(R.drawable.wp6682518_kato_megumi_wallpapers2) // Placeholder sementara gambar dimuat
            .error(R.drawable.email) // Gambar yang akan ditampilkan jika ada kesalahan
            .into(thumImageView)

        listOf(titleTextView, thumImageView).forEach { view ->
            view.setOnClickListener {
                val id = currentManga?.id.toString()
                val action = KatalogFragmentDirections.actionKatalogFragmentToDetailFragment(id)
                val navController = Navigation.findNavController(listItemView)
                navController.navigate(action)
            }
        }

        return listItemView

    }
}

class ChapterAdapter(context: Context, private val chapters: List<Chapter>) :
    ArrayAdapter<Chapter>(context, 0, chapters) {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItemView = convertView
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(R.layout.list_item_chapter, parent, false)
        }

        val currentChapter = getItem(position)
        val currPoss = position

        println("CURRR pos " + currPoss)
        println(position)

        val chapterItem = listItemView!!.findViewById<TextView>(R.id.chapterItem)

        chapterItem.text = currentChapter?.title
        chapterItem.setOnClickListener{
            val chapterId = currentChapter?.id.toString()
            val komikId = currentChapter?.manga.toString()
            val chapterArray = chapters.toTypedArray()
            val action = DetailFragmentDirections.actionDetailFragmentToChapterFragment(chapterId, komikId, chapterArray, currPoss)
            val navController = Navigation.findNavController(listItemView)
            navController.navigate(action)

        }
        return listItemView

    }
}

class ChapterImageAdapter(private val chapterImages: List<ChapterFetch>) :
    RecyclerView.Adapter<ChapterImageAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chapterImage: ImageView = itemView.findViewById(R.id.chapterImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_chapter_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentChapter = chapterImages[position]
        Glide.with(holder.itemView.context)
            .load(currentChapter.link)
            .error(R.drawable.email)
            .fitCenter()
            .into(holder.chapterImage)
    }

    override fun getItemCount(): Int {
        return chapterImages.size
    }
}

class FavoriteAdapter(private var favorites: List<Favorite>, private val favoriteViewModel: FavoriteViewModel) :
    RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val thumbImageView: ImageView = itemView.findViewById(R.id.thumbImageView)
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val totalChaptersTextView: TextView = itemView.findViewById(R.id.totalChaptersTextView)
        val deleteButton: Button = itemView.findViewById(R.id.buttonDeleteFavorite)
    }

    fun updateFavorites(newFavorites: List<Favorite>) {
        favorites = newFavorites
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.favorite_item, parent, false)
        return FavoriteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val currentFavorite = favorites[position]
        Glide.with(holder.itemView.context).load(currentFavorite.thumbLink).into(holder.thumbImageView)
        holder.titleTextView.text = currentFavorite.title
        holder.totalChaptersTextView.text = "Total Chapters: ${currentFavorite.totalChapter}"

        holder.deleteButton.setOnClickListener {
            val komikId = currentFavorite.id
            favoriteViewModel.deleteByKomikId(komikId)
        }
    }

    override fun getItemCount() = favorites.size
}

//class HorizontalAdapter(private val context: Context, private val mangas: List<Manga>) :
//    RecyclerView.Adapter<HorizontalAdapter.MangaViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MangaViewHolder {
//        val view = LayoutInflater.from(context).inflate(R.layout.list_item_manga, parent, false)
//        return MangaViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: MangaViewHolder, position: Int) {
//        val manga = mangas[position]
//        holder.bind(manga)
//    }
//
//    override fun getItemCount(): Int {
//        return mangas.size
//    }
//
//    inner class MangaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
//        private val chapterTextView: TextView = itemView.findViewById(R.id.chaptersTextView)
//        private val mangaImageView: ImageView = itemView.findViewById(R.id.imageThum)
//
//        fun bind(manga: Manga) {
//            titleTextView.text = manga.title
//            chapterTextView.text = manga.totalChapter.toString()
//            Glide.with(itemView)
//                .load(manga.thumb) // Manga.gambar adalah URL gambar
//                .placeholder(R.drawable.manga_bleach) // Placeholder sementara gambar dimuat
//                .error(R.drawable.email) // Gambar yang akan ditampilkan jika ada kesalahan
//                .into(mangaImageView)
//        }
//
//    }
//}


//class ChapterImageAdapter(context: Context, chapterImages: List<ChapterFetch>) :
//    ArrayAdapter<ChapterFetch>(context, 0, chapterImages) {
//
//    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//        var listItemView = convertView
//        if (listItemView == null) {
//            listItemView = LayoutInflater.from(context).inflate(R.layout.list_chapter_image, parent, false)
//        }
//
//        val currentChapter = getItem(position)
//
//        val chapterImage = listItemView!!.findViewById<ImageView>(R.id.chapterImage)
//
//        Glide.with(context)
//            .load(currentChapter?.link) // Manga.gambar adalah URL gambar
//            //.placeholder(R.drawable.manga_bleach) // Placeholder sementara gambar dimuat
//            .error(R.drawable.email) // Gambar yang akan ditampilkan jika ada kesalahan
//            .fitCenter()
//            .into(chapterImage)
//
//        return listItemView
//
//    }
//}



