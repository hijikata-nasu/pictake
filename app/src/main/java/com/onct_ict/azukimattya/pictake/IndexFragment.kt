package com.onct_ict.azukimattya.pictake

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.fragment_index.*
import kotlinx.android.synthetic.main.grid_items.view.*

class IndexFragment : Fragment() {
    var adapter: IndexGridAdapter? = null
    var indexList = ArrayList<IndexItems>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(container?.context).inflate(R.layout.fragment_index, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // load foods
        indexList.add(IndexItems("pict001", R.drawable.pict001))
        indexList.add(IndexItems("pict002", R.drawable.pict002))
        indexList.add(IndexItems("pict003", R.drawable.pict003))
        indexList.add(IndexItems("pict004", R.drawable.pict004))
        indexList.add(IndexItems("pict005", R.drawable.pict005))
        indexList.add(IndexItems("pict006", R.drawable.pict006))
        indexList.add(IndexItems("pict007", R.drawable.pict007))
        indexList.add(IndexItems("pict008", R.drawable.pict008))
        indexList.add(IndexItems("pict009", R.drawable.pict009))
        indexList.add(IndexItems("pict010", R.drawable.pict010))
        indexList.add(IndexItems("pict011", R.drawable.pict011))
        indexList.add(IndexItems("pict012", R.drawable.pict012))
        indexList.add(IndexItems("pict013", R.drawable.pict013))
        indexList.add(IndexItems("pict014", R.drawable.pict014))
        indexList.add(IndexItems("pict015", R.drawable.pict015))
        indexList.add(IndexItems("pict016", R.drawable.pict016))
        indexList.add(IndexItems("pict017", R.drawable.pict017))
        indexList.add(IndexItems("pict018", R.drawable.pict018))
        indexList.add(IndexItems("pict019", R.drawable.pict019))
        indexList.add(IndexItems("pict020", R.drawable.pict020))
        indexList.add(IndexItems("pict021", R.drawable.pict021))
        indexList.add(IndexItems("pict022", R.drawable.pict022))
        indexList.add(IndexItems("", R.drawable.pict022))
        indexList.add(IndexItems("", 0))
        indexList.add(IndexItems("", 0))
        indexList.add(IndexItems("", 0))
        indexList.add(IndexItems("", 0))
        indexList.add(IndexItems("", 0))
        indexList.add(IndexItems("", 0))
        indexList.add(IndexItems("", 0))
        adapter = IndexGridAdapter(activity!! , indexList)

        gridViewIndex.adapter = adapter
    }

    class IndexGridAdapter : BaseAdapter {
        var gridList = ArrayList<IndexItems>()
        var context: Context? = null

        constructor(context: Context, foodsList: ArrayList<IndexItems>) : super() {
            this.context = context
            this.gridList = foodsList
        }

        override fun getCount(): Int {
            return gridList.size
        }

        override fun getItem(position: Int): Any {
            return gridList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val gridItem = this.gridList[position]

            var inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var indexView = inflator.inflate(R.layout.grid_items, null)
            indexView.image_view.setImageResource(gridItem.image!!)
            indexView.text_view.text = gridItem.name!!

            return indexView
        }
    }
}
