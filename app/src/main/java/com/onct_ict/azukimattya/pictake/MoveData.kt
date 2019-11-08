package com.onct_ict.azukimattya.pictake

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import com.google.android.gms.maps.model.LatLng

class MoveData : Application() {
    private val TAG = "APPLICATION"
    // 撮影した画像データ
    var obj: Bitmap? = null
    // 撮影した場所の座標
    var list: MutableList<LatLng> = mutableListOf()
    // 撮影した日時
    var str: MutableList<String> = mutableListOf()
    // 撮影した種類
    var bmpnum: MutableList<Int> = mutableListOf()

    override fun onCreate() {
        super.onCreate()
        //Application作成時
        Log.v(TAG, "--- onCreate() in ---")
    }

    override fun onTerminate() {
        super.onTerminate()
        //Application終了時
        Log.v(TAG, "--- onTerminate() in ---")
    }

    fun clearObj() {
        obj = null
    }
}
