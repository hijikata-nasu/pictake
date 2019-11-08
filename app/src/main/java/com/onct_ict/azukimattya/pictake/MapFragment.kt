package com.onct_ict.azukimattya.pictake

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.*
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.ImageButton
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.*
import com.google.android.gms.maps.GoogleMap
import androidx.appcompat.app.AlertDialog
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.SupportMapFragment





class MapFragment : Fragment(), OnMapReadyCallback, LocationListener {
    private lateinit var mMap: GoogleMap
    private var coordinates: LatLng? = null

    companion object{
        var mapFragment: SupportMapFragment? = null
        fun newInstance() = MapFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d("MapFragment", "onCreateView")
        super.onCreateView(inflater, container, savedInstanceState)


        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    // 位置情報を管理している LocationManager のインスタンスを生成する
    private val locationManager: LocationManager
        get() = this.activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var locationProvider: String? = null

    private fun getLocation() {
        Log.d("MapFragment", "getLocation")
        // Fine か Coarseのいずれかのパーミッションが得られているかチェックする
        // 本来なら、Android6.0以上かそうでないかで実装を分ける必要がある

        // fixme 出来ればここの関数内のパーミッション要求は消したい(onCreateで書いてるため)
        if (ActivityCompat.checkSelfPermission(
                this.activity!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this.activity!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            /** fine location のリクエストコード（値は他のパーミッションと被らなければ、なんでも良い） */
            val requestCode = 1

            // いずれも得られていない場合はパーミッションのリクエストを要求する
            ActivityCompat.requestPermissions(
                this.activity!!,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                requestCode
            )
            return
        }
        // GPSが利用可能になっているかどうかをチェック
        locationProvider = when {
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
            else -> {
                val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(settingsIntent)
                return
            }
        }
        // いずれも利用可能でない場合は、GPSを設定する画面に遷移する
        // GPSプロバイダーが有効になっていない場合は基地局情報が利用可能になっているかをチェック

        /** 位置情報の通知するための最小時間間隔（ミリ秒）  */
        val minTime: Long = 200
        /** 位置情報を通知するための最小距離間隔（メートル） */
        val minDistance: Long = 1

        // 利用可能なロケーションプロバイダによる位置情報の取得の開始
        // FIXME 本来であれば、リスナが複数回登録されないようにチェックする必要がある
        locationManager.requestLocationUpdates(locationProvider!!, minTime, minDistance.toFloat(), this)
        // 最新の位置情報
        val location = locationManager.getLastKnownLocation(locationProvider!!)

        if (location != null) {
            coordinates = LatLng(location.latitude, location.longitude)
            Log.d("MapFragment", "" + location.latitude + "," + location.longitude)
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        Log.d("MapFragment", "onMapReady")
        mMap = googleMap
        val data = activity!!.application as MoveData
        // ピクトグラムの説明文（名前）
        val pictName = resources.getStringArray(R.array.picttext)


        if (coordinates != null) {
            (data.list).forEach { position ->
                var cnt = 0
                mMap.addMarker(MarkerOptions().position(position).title(data.str[cnt]))
                Log.d("MapFragment", data.str[cnt])
                cnt += 1
            }
            val cUpdate = CameraUpdateFactory.newLatLngZoom(coordinates, 19f)

            // マーカーをクリックしたときの動作
            mMap.setOnMarkerClickListener { marker ->
                val sub = marker.id.replace("m", "").toInt()
                val iv = ImageView(this.activity)
                iv.setImageBitmap(BitmapFactory.decodeResource(resources, data.bmpnum[sub]))
                //iv.scaleType = ImageView.ScaleType.FIT_XY
                iv.adjustViewBounds = true

                val name = pictName[sub]

                Log.d("MapFragment", sub.toString())

                // ダイアログタイトルの編集
                val title = TextView(context)

                title.setText(name)
                title.setTextSize(30.0f)
                title.setGravity( Gravity.CENTER_HORIZONTAL )

                AlertDialog.Builder(this.activity!!).setCustomTitle(title).setView(iv).setPositiveButton("close") { _, _ ->

                }.show()
                false

            }
            mMap.moveCamera(cUpdate)
            mMap.isMyLocationEnabled = true
        }
    }

    override fun onLocationChanged(location: Location?) {
        Log.d("MapFragment", "onLocationChanged")
        coordinates = LatLng(location!!.latitude, location.longitude)
        Log.d("kaisei", coordinates.toString())
    }
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        Log.d("MapFragment", "onStatusChanged")
        // TODO Auto-generated method stub
    }

    override fun onProviderEnabled(provider: String) {
        Log.d("MapFragment", "onProviderEnabled")
        // TODO Auto-generated method stub
    }

    override fun onProviderDisabled(provider: String) {
        Log.d("MapFragment", "onProviderDisabled")
        // TODO Auto-generated method stub
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("MapFragment", "onViewCreated")
        super.onViewCreated(view, savedInstanceState)

        getLocation()

        mapFragment = childFragmentManager.findFragmentById(R.id.googleMap) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }
}