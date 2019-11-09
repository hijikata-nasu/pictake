package com.onct_ict.azukimattya.pictake

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import com.github.kittinunf.result.Result
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.android.synthetic.main.fragment_index.*
import kotlinx.android.synthetic.main.fragment_ranking_collect.*
import kotlinx.android.synthetic.main.grid_items.view.*
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Math.pow

data class Indexer (
    val address: String,
    val flag: Int
)

class IndexFragment : Fragment() {
    var adapter: IndexGridAdapter? = null
    var indexList = ArrayList<IndexItems>()

    val indexData = arrayListOf(
        IndexItems("pict001", R.drawable.pict001, false),
        IndexItems("pict002", R.drawable.pict002, false),
        IndexItems("pict003", R.drawable.pict003, false),
        IndexItems("pict004", R.drawable.pict004, false),
        IndexItems("pict005", R.drawable.pict005, false),
        IndexItems("pict006", R.drawable.pict006, false),
        IndexItems("pict007", R.drawable.pict007, false),
        IndexItems("pict008", R.drawable.pict008, false),
        IndexItems("pict009", R.drawable.pict009, false),
        IndexItems("pict010", R.drawable.pict010, false),
        IndexItems("pict011", R.drawable.pict011, false),
        IndexItems("pict012", R.drawable.pict012, false),
        IndexItems("pict013", R.drawable.pict013, false),
        IndexItems("pict014", R.drawable.pict014, false),
        IndexItems("pict015", R.drawable.pict015, false),
        IndexItems("pict016", R.drawable.pict016, false),
        IndexItems("pict017", R.drawable.pict017, false),
        IndexItems("pict018", R.drawable.pict018, false),
        IndexItems("pict019", R.drawable.pict019, false),
        IndexItems("pict020", R.drawable.pict020, false),
        IndexItems("pict021", R.drawable.pict021, false),
        IndexItems("pict022", R.drawable.pict022, false))

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(container?.context).inflate(R.layout.fragment_index, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
            setIndex()
        }
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

            val inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val indexView = inflator.inflate(R.layout.grid_items, null)
            if (gridItem.getFlag!!) {
                indexView.image_view.setOnClickListener {
                    val intent = Intent(context, IndexDetailsActivity::class.java)
                    intent.putExtra("name", gridItem.name!!)
                    intent.putExtra("image", gridItem.image!!)
                    context!!.startActivity(intent)
                }
            } else {
                indexView.image_view.alpha = 0.3F
            }
            indexView.image_view.setImageResource(gridItem.image!!)
            indexView.text_view.text = gridItem.name!!

            return indexView
        }
    }

    private suspend fun setIndex() {
        val address = "taro@gmail.com" //TODO ログインのときのにする
        val (_, _, result) = Fuel.get("/indexer?address="+address).awaitStringResponseResult()
        Log.d("huga", result.toString())
        update(result)
    }

    private fun <T : Any> update(result: Result<T, FuelError>) {
        result.fold(success = {
            val jsonAdapter: JsonAdapter<Indexer> = Moshi.Builder().add(KotlinJsonAdapterFactory()).build().adapter(
                Types.newParameterizedType(Indexer::class.java))
            val flag = jsonAdapter.fromJson(it.toString())
            for (i in 0 until indexData.count()) {
                val check = pow(2.toDouble(), i.toDouble()).toInt()
                if ((flag!!.flag and check) == check) {
                    indexList.add(IndexItems(indexData[i].name!!, indexData[i].image!!, true))
                } else {
                    indexList.add(IndexItems("not collected!", indexData[i].image!!, false))
                }
            }
            adapter = IndexGridAdapter(activity!! , indexList)
            gridViewIndex.adapter = adapter

        }, failure = {
            Log.d(MainActivity::class.java.simpleName, String(it.errorData))
        })
    }
}
