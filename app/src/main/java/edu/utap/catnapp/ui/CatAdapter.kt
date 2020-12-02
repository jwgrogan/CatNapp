package edu.utap.catnapp.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListAdapter
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.utap.catnapp.R
import edu.utap.catnapp.api.CatPost
import edu.utap.catnapp.firebase.CatPhoto
import edu.utap.catnapp.firebase.Storage

class CatAdapter(private val viewModel: MainViewModel)
    : RecyclerView.Adapter<CatAdapter.VH>() {

    // ViewHolder pattern minimizes calls to findViewById
    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        // XXX Write me.
        // NB: This one-liner will exit the current fragment
        // (itemView.context as FragmentActivity).supportFragmentManager.popBackStack()
        private var gridPic = view.findViewById<ImageView>(R.id.gridImage)
        private var gridDetails = view.findViewById<TextView>(R.id.gridDetailsTV)
        private var fav = view.findViewById<ImageView>(R.id.gridFav)
//        val subRowDetails = itemView.findViewById<TextView>(R.id.subRowDetails)

        init {
            // TODO: this?
        }


        private fun initSave(item: CatPost) {
            // Send message button
            val catPhoto = CatPhoto().apply {
                val cUser = MainViewModel.currentUser
                if(cUser == null) {
                    username = "unknown"
                    userId = "unknown"
                    Log.d("HomeFragment", "XXX, currentUser null!")
                } else {
                    username = cUser.displayName
                    userId = cUser.uid
                }

                pictureURL = item.url
            }
            viewModel.savePhoto(catPhoto)
        }

        fun bind(item: CatPost){
//            if (item.url != null) {
//                Glide.glideFetch(item.iconURL, item.iconURL, subRowPic)
//            }

            val imageURL = item.url
            Glide.with(itemView).load(imageURL).into(gridPic)

//            itemView.setOnClickListener {
//                itemView.setBackgroundResource(R.drawable.image_layout)
//            }

            itemView.setOnClickListener{
                MainViewModel.doOneCat(itemView.context, item)
            }

            // set fav heart
            // TODO: this only works in the same session - fix
            if (viewModel.isFav(item)) {
                fav.setImageResource(R.drawable.ic_favorite_black_24dp)
            } else {
                fav.setImageResource(R.drawable.ic_favorite_border_black_24dp)
            }

            // TODO: move this into function above
            fav.setOnClickListener{
                if (viewModel.isFav(item)) {
                    fav.setImageResource(R.drawable.ic_favorite_border_black_24dp)
                    viewModel.removeFav(item)
                    Storage.deleteImage(item.url)
                } else {
                    fav.setImageResource(R.drawable.ic_favorite_black_24dp)
                    viewModel.addFav(item)
                    initSave(item)
                }
            }

//            subRowHeading.text = item.displayName
//            subRowDetails.text = item.publicDescription
//            subRowHeading.setOnClickListener{
////                viewModel.setTitle(item.displayName.toString())
//                viewModel.setSubreddit(item.displayName.toString())
//                viewModel.setTitleToSubreddit()
//                viewModel.netPosts()
//                (itemView.context as FragmentActivity).supportFragmentManager.popBackStack()

//            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.cat_image, parent, false)
        return VH(itemView)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(viewModel.observeCats().value!![position])
    }

    override fun getItemCount(): Int {
        return viewModel.observeCats().value?.count() ?:0
    }

//    fun addAll(redditPost : List<RedditPost>) {
//        subreddits.addAll(redditPost)
//    }
}