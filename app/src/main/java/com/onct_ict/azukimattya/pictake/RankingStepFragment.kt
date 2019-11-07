package com.onct_ict.azukimattya.pictake

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import com.github.kittinunf.result.Result
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.android.synthetic.main.fragment_ranking_collect.*
import kotlinx.android.synthetic.main.ranking_items.view.*
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RankingStepFragment: Fragment(){
    var adapter: CollectRankingListAdapter? = null
    var stepRankingList = ArrayList<RankingItems>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_ranking_collect, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FuelManager.instance.apply {
            basePath = "http://192.168.1.11:8080"
            baseHeaders = mapOf("Device" to "Android")
            baseParams = listOf("key" to "value")
        }

        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
            getStep()
        }
    }

    class CollectRankingListAdapter: BaseAdapter {
        var listViewList = ArrayList<RankingItems>()
        var context: Context? = null

        constructor(context: Context, rankingList: ArrayList<RankingItems>): super() {
            this.context = context
            this.listViewList = rankingList
        }

        override fun getCount(): Int {
            return listViewList.size
        }

        override fun getItem(position: Int): Any {
            return listViewList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val listItem = this.listViewList[position]

            var inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var rankingView = inflater.inflate(R.layout.ranking_items, null)

            rankingView.rankValue.text = listItem.rankValue.toString()
            rankingView.userName.text = listItem.userName
            rankingView.scoreValue.text = listItem.scoreValue.toString() + " Steps"

            return rankingView
        }
    }
    private suspend fun getStep() {
        val (_, _, result) = Fuel.get("/ranking/steps").awaitStringResponseResult()
        Log.d("huga", result.toString())
        update(result)
    }
    private fun <T : Any> update(result: Result<T, FuelError>) {
        result.fold(success = {
            val jsonAdapter: JsonAdapter<List<RankingItems>> = Moshi.Builder().add(
                KotlinJsonAdapterFactory()
            ).build().adapter(
                Types.newParameterizedType(List::class.java, RankingItems::class.java))
            val top = jsonAdapter.fromJson(it.toString())
            top!!.forEach { element ->
                stepRankingList.add(RankingItems(element.rankValue!!, element.userName!!, element.scoreValue!!))
            }
            adapter = CollectRankingListAdapter(activity!!, stepRankingList)
            listCollectRanking.adapter = adapter
        }, failure = {
            Log.d(MainActivity::class.java.simpleName, String(it.errorData))
        })
    }
}