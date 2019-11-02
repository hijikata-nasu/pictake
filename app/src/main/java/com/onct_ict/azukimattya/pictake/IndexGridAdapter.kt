package com.onct_ict.azukimattya.pictake

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import java.util.ArrayList

class IndexGridAdapter// 引数がMainActivityからの設定と合わせる
internal constructor( context: Context, private val layoutId: Int, iList: ArrayList<Int>, private val names: Array<String> ) : BaseAdapter() {

    var context = context
    var imageList = iList
    var name = names

    internal inner class ViewHolder {
        var imageView: ImageView? = null
        var textView: TextView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var indexView = convertView
        var holder: ViewHolder

        if (indexView == null) {

            Log.d("デバック", "IndexGridAfaptertのindexViewはnull") // fixme デバッグ用

            val indexInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            // main.xml の <GridView .../> に grid_items.xml を inflate して convertView とする
            indexView = indexInflater.inflate(layoutId, parent, false)
            // ViewHolder を生成
            holder = ViewHolder()

            holder.imageView = indexView!!.findViewById<ImageView>(R.id.image_view) as ImageView
            holder.textView = indexView!!.findViewById<TextView>(R.id.text_view) as TextView

            indexView.tag = holder
        } else {
            holder = indexView.tag as ViewHolder
        }

        holder.imageView!!.setImageResource(imageList[position])
        holder.textView!!.text = name[position]

        return indexView
    }

    override fun getCount(): Int {
        // List<String> imgList の全要素数を返す
        return imageList.size
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }
}
