package com.onct_ict.azukimattya.pictake

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_ranking_collect.*
import kotlinx.android.synthetic.main.ranking_items.view.*

class RankingStepFragment: Fragment(){
    var adapter: CollectRankingListAdapter? = null
    var collectRankingList = ArrayList<RankingItems>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_ranking_collect, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //ここでcollectRankingList にデータを入れたりする
        collectRankingList.add(RankingItems(1, "kaito", 114514))
        collectRankingList.add(RankingItems(2, "kaito", 114514))
        collectRankingList.add(RankingItems(3, "kaito", 114514))
        collectRankingList.add(RankingItems(4, "kaito", 114514))
        collectRankingList.add(RankingItems(5, "kaito", 114514))
        collectRankingList.add(RankingItems(6, "kaito", 114514))
        collectRankingList.add(RankingItems(6, "kaito", 114514))
        collectRankingList.add(RankingItems(6, "kaito", 114514))
        collectRankingList.add(RankingItems(6, "kaito", 114514))
        collectRankingList.add(RankingItems(6, "kaito", 114514))
        collectRankingList.add(RankingItems(6, "kaito", 114514))
        collectRankingList.add(RankingItems(6, "kaito", 114514))
        collectRankingList.add(RankingItems(6, "kaito", 114514))
        collectRankingList.add(RankingItems(6, "kaito", 114514))
        collectRankingList.add(RankingItems(6, "kaito", 114514))
        collectRankingList.add(RankingItems(6, "kaito", 114514))
        collectRankingList.add(RankingItems(6, "kaito", 114514))
        collectRankingList.add(RankingItems(6, "kaito", 114514))
        adapter = CollectRankingListAdapter(activity!!, collectRankingList)

        listCollectRanking.adapter = adapter
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

}