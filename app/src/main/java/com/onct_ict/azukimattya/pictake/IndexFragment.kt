package com.onct_ict.azukimattya.pictake

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView

class IndexFragment : Fragment() {

    //表示する画像の名前(拡張子は無し)
    private val members = arrayOf(
        "gide",
        "pict002",
        "pict003",
        "pict004",
        "pict005",
        "pict006",
        "pict007",
        "pict008",
        "pict009",
        "pict010",
        "pict011",
        "pict012",
        "pict013",
        "pict014",
        "pict015",
        "pict016",
        "pict017",
        "pict018",
        "pict019",
        "pict020",
        "pict021",
        "pict022"
    )

    // Resource IDを格納するarray
    private val imgList = ArrayList<Int>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d("デバック", "IndexFragmentのonCreateViewが始まった") // fixme デバッグ用
        super.onCreateView(inflater, container, savedInstanceState)
        val view: View = inflater.inflate(R.layout.fragment_index, container, false)

        // for-each member名をR.drawable.名前としてintに変換してarrayに登録
        for (member in members) {
            val imageId = resources.getIdentifier( member, "drawable", ".onct_ict.azukimattya.pictake" )
            imgList.add(imageId)
        }

        // GridViewのインスタンスを生成
        val gridViewIndex: GridView = view.findViewById<GridView>(R.id.gridViewIndex)
        // BaseAdapter を継承したGridAdapterのインスタンスを生成
        // 子要素のレイアウトファイル grid_items.xml を
        // activity_main.xml に inflate するためにGridAdapterに引数として渡す
        val customAdapter = IndexGridAdapter(activity!!.applicationContext, R.layout.grid_items, imgList, members)

        // gridViewにadapterをセット
        gridViewIndex.adapter = customAdapter
        Log.d("デバック", "アダプターはたぶんセットされてる")// fixme デバッグ用
        return  inflater.inflate(R.layout.fragment_index, container, false)
    }
}
