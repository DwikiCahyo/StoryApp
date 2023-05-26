package com.dwiki.storyapp.adapter


import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dwiki.storyapp.activity.DetailActivity
import com.dwiki.storyapp.activity.DetailActivity.Companion.EXTRA_STORY
import com.dwiki.storyapp.api.response.ListStoryItem
import com.dwiki.storyapp.databinding.ListStoryBinding

class ListStoryAdapter: PagingDataAdapter<ListStoryItem,ListStoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    class ViewHolder (private var binding:ListStoryBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(storyList: ListStoryItem) {
           binding.apply {
                Glide.with(imageStory)
                    .load(storyList.photoUrl)
                    .into(imageStory)
               titleStory.text = storyList.name
               uploadStory.text = storyList.description

               cardViewStory.setOnClickListener{
                   val intent = Intent(it.context,DetailActivity::class.java)
                   intent.putExtra(EXTRA_STORY,storyList)

                   val optionsCompat: ActivityOptionsCompat =
                       ActivityOptionsCompat.makeSceneTransitionAnimation(
                           itemView.context as Activity,
                           Pair(imageStory, "photo"),
                           Pair(titleStory, "name"),
                           Pair(uploadStory, "description"),
                       )
                   itemView.context.startActivity(intent,optionsCompat.toBundle())
               }
           }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListStoryBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null){
            holder.bind(data)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

}