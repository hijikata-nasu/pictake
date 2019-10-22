package com.onct_ict.azukimattya.pictake

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import java.util.ArrayList

class IndexGridAdapter// 引数がMainActivityからの設定と合わせる
internal constructor(
    context: Context,
    private val layoutId: Int,
    iList: ArrayList<Int>,
    private val names: Array<String>
) : BaseAdapter() {

    private var imageList = ArrayList<Int>()
    private val inflater: LayoutInflater

    internal inner class ViewHolder {
        var imageView: ImageView? = null
        var textView: TextView? = null
    }

    init {
        this.inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        imageList = iList
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView

        val holder: ViewHolder
        if (convertView == null) {
            // main.xml の <GridView .../> に grid_items.xml を inflate して convertView とする
            convertView = inflater.inflate(layoutId, parent, false)
            // ViewHolder を生成
            holder = ViewHolder()

            holder.imageView = convertView!!.findViewById(R.id.image_view)
            holder.textView = convertView.findViewById(R.id.text_view)

            convertView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }

        holder.imageView!!.setImageResource(imageList[position])
        holder.textView!!.text = names[position]

        return convertView
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
