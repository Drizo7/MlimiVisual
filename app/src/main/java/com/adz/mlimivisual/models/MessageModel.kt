package com.adz.mlimivisual.models


import android.net.Uri
import android.widget.ImageView
import android.widget.VideoView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

class MessageModel constructor(
    var senderId: String = "",
    var receiverId: String = "",
    var message: String = "",
    var date: String = "",
    var type: String = ""

) {
    companion object {

        @JvmStatic
        @BindingAdapter("imageMessage")
        fun loadImage(imageView: ImageView, image: String?) {
            if (image != null) {
                Glide.with(imageView.context).load(image).into(imageView)
            }
        }
        fun loadMedia(view: VideoView, media: String?) {
            if (media != null && media.isNotEmpty()) {
                val videoUri = Uri.parse(media)
                view.setVideoURI(videoUri)
                view.setOnPreparedListener { mediaPlayer ->
                    mediaPlayer.setVolume(0f, 0f)
                    mediaPlayer.isLooping = true
                    view.start()
                }
            }
        }
    }
}