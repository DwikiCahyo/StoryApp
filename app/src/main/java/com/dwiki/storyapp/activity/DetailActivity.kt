package com.dwiki.storyapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.dwiki.storyapp.api.response.ListStoryItem
import com.dwiki.storyapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding:ActivityDetailBinding
    private lateinit var detailStory: ListStoryItem
    private lateinit var name:String
    private lateinit var create:String
    private lateinit var photo:String
    private lateinit var desc:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        detail()
    }

    private fun detail() {
        detailStory = intent.getParcelableExtra(EXTRA_STORY)!!


        name =detailStory.name
        create =detailStory.createdAt
        photo =detailStory.photoUrl
        desc =detailStory.description

        binding.apply {
            tvName.text = name
            tvDescription.text = desc
            supportActionBar?.title = name

            Glide.with(this@DetailActivity)
                .load(photo)
                .into(imageDetailStory)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }


    companion object{
        const val EXTRA_STORY = "extra_story"
    }
}