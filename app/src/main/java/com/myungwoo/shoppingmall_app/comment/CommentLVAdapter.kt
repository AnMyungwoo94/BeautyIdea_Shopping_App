package com.myungwoo.shoppingmall_app.comment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.myungwoo.shoppingmall_app.R

class CommentLVAdapter(private val commentList: MutableList<CommentModel>) : BaseAdapter() {
    override fun getCount(): Int {
        return commentList.size
    }

    override fun getItem(position: Int): Any {
        return commentList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(parent!!.context).inflate(R.layout.comment_list_item, parent, false)
        }
        val title = view!!.findViewById<TextView>(R.id.titleArea)
        val time = view.findViewById<TextView>(R.id.timeArea)

        title!!.text = commentList[position].commentTitle
        time!!.text = commentList[position].commentCreatedTime

        return view
    }
}