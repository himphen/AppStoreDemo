package com.hibernatev2.appstoredemo.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.hibernatev2.appstoredemo.R
import com.hibernatev2.appstoredemo.model.AppItem
import kotlinx.android.synthetic.main.list_item_top_recommendation_item.view.*
import java.util.*

class AppRecommendationAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var top10RecommendationList: List<AppItem> = ArrayList()

    private var mContext: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mContext = parent.context

        val itemView: View = LayoutInflater.from(mContext).inflate(R.layout.list_item_top_recommendation_item, parent, false)
        return TopRecommendationItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as TopRecommendationItemViewHolder
        val appItem = top10RecommendationList[position]

        viewHolder.categoryTv.text = appItem.category!![0]
        viewHolder.nameTv.text = appItem.title

        Glide.with(mContext!!)
                .load(appItem.iconUrl)
                .apply(RequestOptions()
                        .transform(RoundedCorners(14))
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(viewHolder.iconIv)
    }

    override fun getItemCount(): Int {
        return top10RecommendationList.size
    }

    override fun getItemViewType(position: Int): Int {
        return VIEW_TYPE_CONTENT
    }

    fun setData(top10RecommendationList: List<AppItem>) {
        this.top10RecommendationList = top10RecommendationList
        notifyDataSetChanged()
    }

    class TopRecommendationItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iconIv: ImageView = itemView.iconIv
        var nameTv: TextView = itemView.nameTv
        var categoryTv: TextView = itemView.categoryTv
    }

    companion object {
        private const val VIEW_TYPE_CONTENT = 0x00
    }
}
