package com.onct_ict.azukimattya.pictake

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.location.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.*
import com.google.android.gms.maps.GoogleMap
import androidx.appcompat.app.AlertDialog
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.SupportMapFragment





class MapFragment : SupportMapFragment(), OnMapReadyCallback, LocationListener {
    private lateinit var mMap: GoogleMap
    private var coordinates: LatLng? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d("hoge", "onCreateView")
        super.onCreateView(inflater, container, savedInstanceState)
        getLocation()
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    // 位置情報を管理している LocationManager のインスタンスを生成する
    private val locationManager: LocationManager
        get() = this.activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var locationProvider: String? = null

    private fun getLocation() {
        Log.d("hoge", "getLocation")
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
            Log.d("kaisei", "" + location.latitude + "," + location.longitude)
        }
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

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d("hoge", "onMapReady")
        this.mMap = googleMap
        val data = activity!!.application as MoveData

        // Add a marker in Sydney and move the camera
        if (coordinates != null) {
            (data.list).forEach { position ->
                var cnt = 0
                mMap.addMarker(MarkerOptions().position(position).title(data.str[cnt]))
                Log.d("hoge", data.str[cnt])
                cnt += 1
            }
            val cUpdate = CameraUpdateFactory.newLatLngZoom(coordinates, 19f)

            // マーカーをクリックしたときの動作
            mMap.setOnMarkerClickListener { marker ->
                val sub = marker.id.replace("m", "").toInt()
                val iv = ImageView(this.activity)
                iv.setImageBitmap(data.bmp[sub])
                iv.scaleType = ImageView.ScaleType.FIT_XY
                iv.adjustViewBounds = true

                Log.d("bantyo", sub.toString())
                val show = AlertDialog.Builder(this.activity!!).setView(iv).setPositiveButton("close") { _, _ -> }.show()
                false
            }
            mMap.moveCamera(cUpdate)
            mMap.isMyLocationEnabled = true
        }
    }

    override fun onLocationChanged(location: Location?) {
        Log.d("hoge", "onLocationChanged")
        coordinates = LatLng(location!!.latitude, location.longitude)
        Log.d("kaisei", coordinates.toString())
    }
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        Log.d("hoge", "onStatusChanged")
        // TODO Auto-generated method stub
    }

    override fun onProviderEnabled(provider: String) {
        Log.d("hoge", "onProviderEnabled")
        // TODO Auto-generated method stub
    }

    override fun onProviderDisabled(provider: String) {
        Log.d("hoge", "onProviderDisabled")
        // TODO Auto-generated method stub
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("hoge", "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        getMapAsync(this)
    }
}