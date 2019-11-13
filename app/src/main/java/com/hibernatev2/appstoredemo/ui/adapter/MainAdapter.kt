package com.hibernatev2.appstoredemo.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.hibernatev2.appstoredemo.R
import com.hibernatev2.appstoredemo.model.AppItem
import kotlinx.android.synthetic.main.list_item_top_free_item.view.*
import kotlinx.android.synthetic.main.list_item_top_recommendation_row.view.*
import java.util.*

/**
 * Created by Himphen on 4/12/2017.
 */
class MainAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var top100FreeList: List<AppItem> = ArrayList()
    private var top10RecommendationList: List<AppItem> = ArrayList()

    private var isLoading = false
    private var noMore = false
    // The minimum amount of items to have below your current scroll position
    // before isLoading more.
    private val visibleThreshold = 3
    private var lastVisibleItem: Int = 0
    private var totalItemCount: Int = 0
    private var onLoadMoreListener: OnLoadMoreListener? = null
    private lateinit var mContext: Context

    interface OnLoadMoreListener {
        fun onLoadMore()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mContext = parent.context

        val itemView: View

        when (viewType) {
            VIEW_TYPE_TITLE -> {
                itemView = LayoutInflater.from(mContext).inflate(R.layout.list_item_title, parent, false)
                return TitleViewHolder(itemView)
            }

            VIEW_TYPE_HR -> {
                itemView = LayoutInflater.from(mContext).inflate(R.layout.list_item_hr, parent, false)
                return HRViewHolder(itemView)
            }

            VIEW_TYPE_TOP_RECOMMENDATION_ROW -> {
                itemView = LayoutInflater.from(mContext).inflate(R.layout.list_item_top_recommendation_row, parent, false)
                return TopRecommendationRowViewHolder(itemView)
            }


            VIEW_TYPE_TOP_FREE_ITEM -> {
                itemView = LayoutInflater.from(mContext).inflate(R.layout.list_item_top_free_item, parent, false)
                return TopFreeItemViewHolder(itemView)
            }
            else -> {
                itemView = LayoutInflater.from(mContext).inflate(R.layout.list_item_top_free_item, parent, false)
                return TopFreeItemViewHolder(itemView)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TopFreeItemViewHolder) {

            val appItem = top100FreeList[position - 3]

            holder.categoryTv.text = appItem.category!![0]
            holder.nameTv.text = appItem.title
            holder.countTv.text = (position - 2).toString()

            if (position % 2 == 1) {
                Glide.with(mContext)
                        .load(appItem.iconUrl)
                        .apply(RequestOptions()
                                .transform(RoundedCorners(14))
                                .diskCacheStrategy(DiskCacheStrategy.ALL))
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(holder.iconIv)

            } else {
                Glide.with(mContext)
                        .load(appItem.iconUrl)
                        .apply(RequestOptions()
                                .circleCrop()
                                .diskCacheStrategy(DiskCacheStrategy.ALL))
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(holder.iconIv)
            }

            if (appItem.rating != null) {
                Glide.with(mContext).load(if (appItem.rating >= 2) R.drawable.star else R.drawable.star_empty).into(holder.ratingStar2Iv)
                Glide.with(mContext).load(if (appItem.rating >= 3) R.drawable.star else R.drawable.star_empty).into(holder.ratingStar3Iv)
                Glide.with(mContext).load(if (appItem.rating >= 4) R.drawable.star else R.drawable.star_empty).into(holder.ratingStar4Iv)
                Glide.with(mContext).load(if (appItem.rating >= 5) R.drawable.star else R.drawable.star_empty).into(holder.ratingStar5Iv)
            }

            if (appItem.ratingCount != null) {
                holder.ratingCountTv.text = "(" + appItem.ratingCount + ")"
            }
        } else if (holder is TopRecommendationRowViewHolder) {
            (holder.recyclerView.adapter as AppRecommendationAdapter).setData(top10RecommendationList)
        }
    }

    override fun getItemCount(): Int {
        return 3 + top100FreeList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> VIEW_TYPE_TITLE
            1 -> VIEW_TYPE_TOP_RECOMMENDATION_ROW
            2 -> VIEW_TYPE_HR
            else -> VIEW_TYPE_TOP_FREE_ITEM
        }
    }

    fun setData(top100FreeList: List<AppItem>, top10RecommendationList: List<AppItem>, recyclerView: RecyclerView) {
        this.top100FreeList = top100FreeList
        this.top10RecommendationList = top10RecommendationList

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView,
                                    dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                totalItemCount = recyclerView.layoutManager!!.itemCount
                lastVisibleItem = (recyclerView
                        .layoutManager as LinearLayoutManager).findLastVisibleItemPosition()

                if (!noMore && !isLoading && totalItemCount <= lastVisibleItem + visibleThreshold) {
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener!!.onLoadMore()
                    }
                    isLoading = true
                    recyclerView.post { this@MainAdapter.notifyDataSetChanged() }
                }
            }
        })

        notifyDataSetChanged()
    }

    fun setLoaded() {
        isLoading = false
        notifyDataSetChanged()
    }

    fun setLoadedAndNoMore() {
        noMore = true
        setLoaded()
    }

    fun init() {
        noMore = false
        isLoading = false
        notifyDataSetChanged()
    }

    fun setOnLoadMoreListener(onLoadMoreListener: OnLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener
    }

    internal class TopRecommendationRowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var recyclerView: RecyclerView = itemView.recyclerView

        private var layoutManager: LinearLayoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)

        init {
            val adapter = AppRecommendationAdapter()
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter
        }
    }

    class TitleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class HRViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class TopFreeItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var countTv: TextView = itemView.countTv
        var iconIv: ImageView = itemView.iconIv
        var nameTv: TextView = itemView.nameTv
        var categoryTv: TextView = itemView.categoryTv
        var ratingStar2Iv: ImageView = itemView.ratingStar2Iv
        var ratingStar3Iv: ImageView = itemView.ratingStar3Iv
        var ratingStar4Iv: ImageView = itemView.ratingStar4Iv
        var ratingStar5Iv: ImageView = itemView.ratingStar5Iv
        var ratingCountTv: TextView = itemView.ratingCountTv
    }

    companion object {
        private const val VIEW_TYPE_TITLE = 11
        private const val VIEW_TYPE_HR = 12
        private const val VIEW_TYPE_TOP_RECOMMENDATION_ROW = 0
        private const val VIEW_TYPE_TOP_FREE_ITEM = 1
    }
}
