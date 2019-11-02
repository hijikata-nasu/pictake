package com.onct_ict.azukimattya.pictake

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : SupportMapFragment(), OnMapReadyCallback, LocationListener{

    private lateinit var mMap: GoogleMap
    var Coordinates: LatLng? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return  inflater.inflate(R.layout.fragment_map, container, false)
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    /** fixme
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val data = activity!!.application as MoveData

        // Add a marker in Sydney and move the camera
        if (Coordinates != null) {
            var cnt = 0
            for (position in data.list) {
                mMap.addMarker(MarkerOptions().position(position).title(data.str[cnt]))
                cnt++
            }
            val cUpdate = CameraUpdateFactory.newLatLngZoom(
                Coordinates, 19f
            )

            // マーカーをクリックしたときの動作
            mMap.setOnMarkerClickListener { marker ->
                val sub = marker.getId().replace("m", "").toInt()
                val iv = ImageView(this)
                iv.setImageBitmap(data.bmp[sub])
                iv.setScaleType(ImageView.ScaleType.FIT_XY)
                iv.setAdjustViewBounds(true)

                Log.d("bantyo", sub.toString())
                AlertDialog.Builder(this)
                    .setView(iv)
                    .setPositiveButton("close"){ dialog, which ->
                    }.show()
                false
            }
            mMap.moveCamera(cUpdate)
            mMap.setMyLocationEnabled(true)
        }
    }

    /** 位置情報関連の関数  **/
    // 位置情報を取得する関数
    private fun getLocation() {
        // Fine か Coarseのいずれかのパーミッションが得られているかチェックする
        // 本来なら、Android6.0以上かそうでないかで実装を分ける必要がある
        // fixme 出来ればここの関数内のパーミッション要求は消したい(onCreateで書いてるため)
        if (ActivityCompat.checkSelfPermission(
                application,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                application, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            /** fine location のリクエストコード（値は他のパーミッションと被らなければ、なんでも良い） */
            val requestCode = 1

            // いずれも得られていない場合はパーミッションのリクエストを要求する
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                requestCode
            )
            return
        }
        */ //fixme

        // 位置情報を管理している LocationManager のインスタンスを生成する
        private val locationManager: LocationManager = getSystemService(activity!!.applicationContext.LOCATION_SERVICE) as LocationManager
        private val locationProvider: String? = null

        // GPSが利用可能になっているかどうかをチェック
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationProvider = LocationManager.GPS_PROVIDER
        } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationProvider = LocationManager.NETWORK_PROVIDER
        } else {
            val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(settingsIntent)
            return
        }// いずれも利用可能でない場合は、GPSを設定する画面に遷移する
        // GPSプロバイダーが有効になっていない場合は基地局情報が利用可能になっているかをチェック

        /** 位置情報の通知するための最小時間間隔（ミリ秒）  */
        val minTime: Long = 200
        /** 位置情報を通知するための最小距離間隔（メートル） */
        val minDistance: Long = 1

        // 利用可能なロケーションプロバイダによる位置情報の取得の開始
        // FIXME 本来であれば、リスナが複数回登録されないようにチェックする必要がある
        locationManager.requestLocationUpdates(
            locationProvider,
            minTime,
            minDistance.toFloat(),
            this
        )
        // 最新の位置情報
        val location = locationManager.getLastKnownLocation(locationProvider)

        if (location != null) {
            Coordinates = LatLng(location.latitude, location.longitude)
            Log.d("kaisei", "" + location.latitude + "," + location.longitude)
        }
    }

    //位置情報が通知されるたびにコールバックされるメソッド
    override fun onLocationChanged(location: android.location.Location) {
        Coordinates = LatLng(location.latitude, location.longitude)
        Log.d("kaisei", "" + location.latitude + "," + location.longitude)
    }

    //ロケーションプロバイダが利用不可能になるとコールバックされるメソッド
    override fun onProviderDisabled(provider: String) {
        //ロケーションプロバイダーが使われなくなったらリムーブする必要がある
    }

    //ロケーションプロバイダが利用可能になるとコールバックされるメソッド
    override fun onProviderEnabled(provider: String) {
        //プロバイダが利用可能になったら呼ばれる
    }

    //ロケーションステータスが変わるとコールバックされるメソッド
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        // 利用可能なプロバイダの利用状態が変化したときに呼ばれる
    }
    /**　ここまで位置情報関連の関数　**/
}
