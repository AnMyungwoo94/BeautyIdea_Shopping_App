package com.myungwoo.shoppingmall_app.ui.tipList

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.myungwoo.shoppingmall_app.R
import com.myungwoo.shoppingmall_app.data.ContentModel

class BookmarkRvAdapter(
    val context: Context,
    private val items: ArrayList<ContentModel>,
    private val keyList: ArrayList<String>,
    val bookmarkIdList: MutableList<String>
) : RecyclerView.Adapter<BookmarkRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.content_rv_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bindItems(items[position], keyList[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(item: ContentModel, key: String) {
            itemView.setOnClickListener {
                val intent = Intent(context, ContentShowActivity::class.java)
                intent.putExtra("uri", item.webUrl)
                itemView.context.startActivity(intent)
            }

            val contentTitle = itemView.findViewById<TextView>(R.id.textArea)
            val imageViewArea = itemView.findViewById<ImageView>(R.id.imageArea)
            val bookmarkArea = itemView.findViewById<ImageView>(R.id.bookmarkArea)
            if (bookmarkIdList.contains(key)) {
                bookmarkArea.setImageResource(R.drawable.bookmark_color)
            } else {
                bookmarkArea.setImageResource(R.drawable.bookmark_white)
            }
            contentTitle.text = item.title
            Glide.with(context).load(item.imageUrl).into(imageViewArea)
        }
    }
}