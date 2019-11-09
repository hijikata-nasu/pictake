package com.onct_ict.azukimattya.pictake

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_index_details.*

class IndexDetailsActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index_details)

        val bundle = intent.extras

        imgFoodDetails.setImageResource(bundle!!.getInt("image"))
        tvName.text = bundle.getString("name")
    }
}