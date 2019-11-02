package com.onct_ict.azukimattya.pictake

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment

class RankingCollectFragment: Fragment(){

    private val collectItem = arrayOf("a", "b", "c", "c", "d", "e") // fixme 実験用の適当なデータ

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_ranking_collect, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val listView = activity!!.findViewById(R.id.listCollectRanking) as ListView
        val arrayAdapter = ArrayAdapter(activity!!.applicationContext, R.layout.item_ranking_listview, collectItem)
        listView.setAdapter(arrayAdapter)
    }

}
