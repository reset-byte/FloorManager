package com.github.floormanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.floormanager.databinding.ActivityWebBinding

/**
 * Web展示活动类
 * 用于显示Web内容的Activity
 */
class WebActivity: AppCompatActivity() {

    private lateinit var binding: ActivityWebBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        binding.webDescription.loadHtml("<p><img src=\"https://picsum.photos/500/300\"><br></p><p><img src=\"https://picsum.photos/500/400\"><br></p><p><img src=\"https://picsum.photos/500/350\"></p><p><img src=\"https://picsum.photos/500/450\"></p><p><br></p><p><br></p>")
    }

}