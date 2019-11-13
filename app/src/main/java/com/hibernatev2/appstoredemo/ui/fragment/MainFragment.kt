package com.hibernatev2.appstoredemo.ui.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hibernatev2.appstoredemo.R
import com.hibernatev2.appstoredemo.event.AppItemLoadComplete
import com.hibernatev2.appstoredemo.event.EntryLoadComplete
import com.hibernatev2.appstoredemo.event.FreeAppItemLoadMoreRequest
import com.hibernatev2.appstoredemo.event.SearchRequest
import com.hibernatev2.appstoredemo.helper.UtilHelper
import com.hibernatev2.appstoredemo.model.AppItem
import com.hibernatev2.appstoredemo.model.Entry
import com.hibernatev2.appstoredemo.rest.model.BaseCallback
import com.hibernatev2.appstoredemo.rest.model.response.AppEntryResponse
import com.hibernatev2.appstoredemo.rest.model.response.AppItemResponse
import com.hibernatev2.appstoredemo.rest.service.BaseApiClient
import com.hibernatev2.appstoredemo.ui.activity.MainActivity
import com.hibernatev2.appstoredemo.ui.adapter.MainAdapter
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_main.*
import retrofit2.Call
import retrofit2.Response
import java.util.*

class MainFragment : Fragment() {

    lateinit var apiClient: BaseApiClient
    private var mContext: Activity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = activity
        apiClient = BaseApiClient(mContext!!)
    }

    private val top100FreeEntryListAll = ArrayList<Entry>()
    private val top10RecommendationEntryListAll = ArrayList<Entry>()
    private val top100FreeEntryListSearched = ArrayList<Entry>()
    private val top10RecommendationEntryListSearched = ArrayList<Entry>()
    private val top100FreeAppItemList = ArrayList<AppItem>()
    private val top10RecommendationAppItemList = ArrayList<AppItem>()

    private var adapter: MainAdapter? = null

    private var currentPage = 0
    private val pageSize = 10
    private var search = ""

    private var observer: Observer<Any>? = null
    private var searchRequestDisposable: Disposable? = null

    private var isFirstTimeOpenSearchView = true

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = MainAdapter()
        adapter!!.setData(top100FreeAppItemList, top10RecommendationAppItemList, recyclerView!!)
        adapter!!.setOnLoadMoreListener(object : MainAdapter.OnLoadMoreListener {
            override fun onLoadMore() {
                Observable.just(FreeAppItemLoadMoreRequest())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .unsubscribeOn(Schedulers.io())
                        .subscribe(observer!!)
            }
        })

        recyclerView!!.layoutManager = LinearLayoutManager(mContext)
        recyclerView!!.adapter = adapter
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.main_list, menu)
        val mSearchMenuItem = menu.findItem(R.id.action_search)
        val searchView = SearchView((mContext as MainActivity).supportActionBar!!.themedContext)
        mSearchMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW or MenuItem.SHOW_AS_ACTION_IF_ROOM)
        mSearchMenuItem.actionView = searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                if (!isFirstTimeOpenSearchView) {
                    search = query
                    resetAndFetchList()
                } else {
                    isFirstTimeOpenSearchView = false
                }
                return false
            }
        })
    }


    @SuppressLint("CheckResult")
    override fun onStart() {
        super.onStart()

        observer = object : Observer<Any> {
            override fun onSubscribe(d: Disposable) {
                UtilHelper.debug("onSubscribe")
            }

            override fun onNext(event: Any) {
                UtilHelper.debug("onNext")
                if (event is FreeAppItemLoadMoreRequest) {
                    progressBar!!.visibility = View.VISIBLE
                    val appItemIdList = ArrayList<String>()

                    var start = currentPage * pageSize
                    val end = ++currentPage * pageSize
                    val size = top100FreeEntryListSearched.size
                    while (start < end && start < size) {
                        val entry = top100FreeEntryListSearched[start]
                        appItemIdList.add(entry.id!!.attributes!!.id)
                        start++
                    }

                    if (appItemIdList.size > 0) {
                        apiClient.apiClient()
                                .lookupItem(UtilHelper.getCommonSeparatedString(appItemIdList))
                                .enqueue(object : BaseCallback<AppItemResponse>(mContext!!) {
                                    override fun onResponse(call: Call<AppItemResponse>, response: Response<AppItemResponse>) {
                                        if (mContextReference.get() == null || !isAdded) {
                                            return
                                        }

                                        if (response.isSuccessful && response.body() != null) {
                                            Observable.just(AppItemLoadComplete(response.body()!!.results!!)).subscribe(observer!!)
                                        } else {
                                            // TODO Error message
                                        }
                                    }

                                    override fun onFailure(call: Call<AppItemResponse>, t: Throwable) {
                                        if (mContextReference.get() == null || !isAdded) {
                                            return
                                        }
                                    }
                                })
                    } else {
                        progressBar!!.visibility = View.GONE
                        adapter!!.setLoadedAndNoMore()
                    }
                } else if (event is AppItemLoadComplete) {
                    val data = event.list

                    top100FreeAppItemList.addAll(data)
                    adapter!!.notifyDataSetChanged()
                    progressBar!!.visibility = View.GONE

                    adapter!!.setLoaded()
                    UtilHelper.debug("Done AppItemLoadComplete")
                    UtilHelper.debug("size top100FreeAppItemList: " + top100FreeAppItemList.size)
                } else if (event is SearchRequest) {
                    top100FreeEntryListSearched.clear()
                    top10RecommendationEntryListSearched.clear()

                    for (entry in top100FreeEntryListAll) {
                        if (entry.name!!.label!!.contains(search)
                                || entry.artist!!.label!!.contains(search)
                                || entry.category!!.attributes!!.label.contains(search)
                                || entry.summary!!.label!!.contains(search)) {
                            top100FreeEntryListSearched.add(entry)
                        }
                    }

                    for (entry in top10RecommendationEntryListAll) {
                        if (entry.name!!.label!!.contains(search)
                                || entry.artist!!.label!!.contains(search)
                                || entry.category!!.attributes!!.label.contains(search)
                                || entry.summary!!.label!!.contains(search)) {
                            top10RecommendationEntryListSearched.add(entry)
                        }
                    }

                    val appItemIdList = ArrayList<String>()

                    for (entry in top10RecommendationEntryListSearched) {
                        appItemIdList.add(entry.id!!.attributes!!.id)
                    }

                    apiClient.apiClient()
                            .lookupItem(UtilHelper.getCommonSeparatedString(appItemIdList))
                            .enqueue(object : BaseCallback<AppItemResponse>(mContext!!) {
                                override fun onResponse(call: Call<AppItemResponse>, response: Response<AppItemResponse>) {
                                    if (mContextReference.get() == null || !isAdded) {
                                        return
                                    }

                                    if (response.isSuccessful && response.body() != null) {
                                        top10RecommendationAppItemList.addAll(response.body()!!.results!!)
                                        adapter!!.notifyDataSetChanged()
                                    } else {
                                        // TODO Error message
                                    }
                                }

                                override fun onFailure(call: Call<AppItemResponse>, t: Throwable) {
                                    if (mContextReference.get() == null || !isAdded) {
                                        return
                                    }
                                }
                            })

                    Observable.just(FreeAppItemLoadMoreRequest())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .unsubscribeOn(Schedulers.io())
                            .subscribe(observer!!)
                } else if (event is EntryLoadComplete) {
                    resetAndFetchList()
                }
            }

            override fun onError(e: Throwable) {
                e.printStackTrace()
                UtilHelper.debug("onError")
            }

            override fun onComplete() {
                UtilHelper.debug("onComplete")
            }
        }

        Observable.zip(
                apiClient.apiClient().top10RecommendationEntry(),
                apiClient.apiClient().top100FreeEntry(),
                BiFunction<AppEntryResponse, AppEntryResponse, Any> { top10RecommendationResponse, top100FreeResponse ->
                    top10RecommendationEntryListAll.addAll(top10RecommendationResponse.data.entries)
                    top100FreeEntryListAll.addAll(top100FreeResponse.data.entries)
                    EntryLoadComplete()
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(observer!!)
    }

    private fun resetAndFetchList() {
        currentPage = 0
        top100FreeAppItemList.clear()
        top10RecommendationAppItemList.clear()
        adapter!!.init()

        Observable.just(SearchRequest()).subscribe(object : Observer<Any> {
            override fun onSubscribe(d: Disposable) {
                if (searchRequestDisposable != null) {
                    searchRequestDisposable!!.dispose()
                }

                searchRequestDisposable = d
                UtilHelper.debug("onSubscribe")
            }

            override fun onNext(event: Any) {
                UtilHelper.debug("onNext")
                top100FreeEntryListSearched.clear()
                top10RecommendationEntryListSearched.clear()

                for (entry in top100FreeEntryListAll) {
                    if (entry.name!!.label!!.contains(search)
                            || entry.artist!!.label!!.contains(search)
                            || entry.category!!.attributes!!.label.contains(search)
                            || entry.summary!!.label!!.contains(search)) {
                        top100FreeEntryListSearched.add(entry)
                    }
                }

                for (entry in top10RecommendationEntryListAll) {
                    if (entry.name!!.label!!.contains(search)
                            || entry.artist!!.label!!.contains(search)
                            || entry.category!!.attributes!!.label.contains(search)
                            || entry.summary!!.label!!.contains(search)) {
                        top10RecommendationEntryListSearched.add(entry)
                    }
                }

                val appItemIdList = ArrayList<String>()

                for (entry in top10RecommendationEntryListSearched) {
                    appItemIdList.add(entry.id!!.attributes!!.id)
                }

                apiClient.apiClient()
                        .lookupItem(UtilHelper.getCommonSeparatedString(appItemIdList))
                        .enqueue(object : BaseCallback<AppItemResponse>(mContext!!) {
                            override fun onResponse(call: Call<AppItemResponse>, response: Response<AppItemResponse>) {
                                if (mContextReference.get() == null || !isAdded) {
                                    return
                                }

                                if (response.isSuccessful && response.body() != null) {
                                    top10RecommendationAppItemList.addAll(response.body()!!.results!!)
                                    adapter!!.notifyDataSetChanged()
                                } else {
                                    // TODO Error message
                                }
                            }

                            override fun onFailure(call: Call<AppItemResponse>, t: Throwable) {
                                if (mContextReference.get() == null || !isAdded) {
                                    return
                                }
                            }
                        })

                Observable.just(FreeAppItemLoadMoreRequest())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .unsubscribeOn(Schedulers.io())
                        .subscribe(observer!!)
            }

            override fun onError(e: Throwable) {
                e.printStackTrace()
                UtilHelper.debug("onError")
            }

            override fun onComplete() {
                searchRequestDisposable = null
                UtilHelper.debug("onComplete")
            }
        })
    }

    companion object {


        fun newInstance(): Fragment {
            val fragment = MainFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
