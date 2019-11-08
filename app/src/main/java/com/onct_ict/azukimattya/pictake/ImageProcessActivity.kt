package com.onct_ict.azukimattya.pictake

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Rect
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import java.text.SimpleDateFormat
import java.util.*
import com.google.android.gms.maps.model.LatLng

class ImageProcessActivity : AppCompatActivity(), LocationListener {

    var imageView: ImageView? = null
    var button: Button? = null
    var text: TextView? = null
    val PICT_NUM = 22
    val EVALUATION_VALUE =  0.5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_process)
        OpenCVLoader.initDebug()
        LoaderCallback(this).onManagerConnected(LoaderCallbackInterface.SUCCESS)

        val data = this.application as MoveData

        findViews()
        setListeners()
        if (data.obj != null) {
            recognition(data)
        }
    }

    // 時間取得
    private fun getToday(): String {
        val date = Date()
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return format.format(date)
    }

    // UIの設定
    private fun findViews() {
        imageView = this.findViewById(R.id.imageView) as ImageView
        button = this.findViewById(R.id.back_button) as Button
        text = this.findViewById(R.id.textView) as TextView
    }

    // ボタンのイベントとか
    private fun setListeners() {
        // ボタンを押したときなどの動作を書く
        button?.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    // 画像比較
    private fun recognition(data: MoveData) {
        // 撮影したデータを代入
        val capturedImage = data.obj

        data.clearObj()
        // opencvで使える形式に（行列）
        val rImg = Mat()
        Utils.bitmapToMat(capturedImage, rImg)

        // サイズ変更
        val resizeWidth = 750.0
        val resizeHeight = 1000.0
        val resize = Mat()
        Imgproc.resize(rImg, resize, Size(resizeWidth, resizeHeight))

        // 画像の切り取り
        val cutsize = 550
        val cutImgSize = Rect(100, 150, cutsize, cutsize)
        val trimImg = Mat(resize, cutImgSize)

        // グレースケール変換
        Imgproc.cvtColor(trimImg, trimImg, Imgproc.COLOR_RGB2GRAY)

        // 画像の二値化
        Imgproc.threshold(trimImg, trimImg, 0.0, 255.0, Imgproc.THRESH_OTSU)

        // ここまで撮った写真の処理

        val pictArray = resources.obtainTypedArray(R.array.pict_list)
        val pictName = resources.getStringArray(R.array.picttext)
        var sub = 0
        val changeImg = Mat()
        // 評価値の最大値（一番似ている画像）
        var max = -1.0
        // 似ている画像の添え字
        var maxsub = -1

        while (sub < PICT_NUM) {

            val res = pictArray.getResourceId(sub, -1)
            // 比較用画像
            val bmp = BitmapFactory.decodeResource(resources, res)
            // openCVで扱える形式に変更
            val img = Mat()
            Utils.bitmapToMat(bmp, img)

            // サイズ変更
            Imgproc.resize(img, changeImg, Size(cutsize.toDouble(), cutsize.toDouble()))

            // グレースケール
            Imgproc.cvtColor(changeImg, changeImg, Imgproc.COLOR_RGB2GRAY)

            // 二値化
            Imgproc.threshold(changeImg, changeImg, 128.0, 255.0, Imgproc.THRESH_BINARY)

            // ぼかし（ジャギーを無くす）
            Imgproc.GaussianBlur(trimImg, trimImg, Size(13.0, 13.0), 0.0, 0.0)

            val result = Mat()

            Imgproc.matchTemplate(trimImg, changeImg, result, Imgproc.TM_CCOEFF_NORMED)

            var mmr = Core.minMaxLoc(result)

            if (mmr.maxVal > EVALUATION_VALUE) {
                if (max < mmr.maxVal) {
                    max = mmr.maxVal
                    maxsub = sub
                }
            }

            // 白黒反転させて比較
            Core.bitwise_not(trimImg, trimImg)
            Imgproc.matchTemplate(trimImg, changeImg, result, Imgproc.TM_CCOEFF_NORMED)

            mmr = Core.minMaxLoc(result)

            if (mmr.maxVal > EVALUATION_VALUE) {
                if (max < mmr.maxVal) {
                    max = mmr.maxVal
                    maxsub = sub
                }
            }

            sub += 1
            Log.d("fuck", "" + max)
        }

        // 認識成功時
        if (maxsub != -1) {
            val res = pictArray.getResourceId(maxsub, -1)
            val bmp = BitmapFactory.decodeResource(resources, res)
            data.bmp.add(bmp)
            getLocation()
            // 画像をセット
            imageView?.setImageBitmap(bmp)
            text?.setTextSize(20.0f)
            text?.setText(pictName[maxsub])
            text?.setGravity(Gravity.CENTER_VERTICAL)
        }

        if (maxsub == -1) {
            text?.setText("Recognition Failure")
            text?.setTextSize(36.0f)
            text?.setTypeface(Typeface.SANS_SERIF)
        }
        pictArray.recycle()
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

        // 位置情報を管理している LocationManager のインスタンスを生成する
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val locationProvider: String?

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
            val data = this.application as MoveData
            data.str.add(getToday())
            data.list.add(LatLng(location.latitude, location.longitude))
            Log.d("kaisei", "" + location.latitude + "," + location.longitude)
            Log.d("oshiri", ""+ data.list.count())
        }
    }

    //位置情報が通知されるたびにコールバックされるメソッド
    override fun onLocationChanged(location: android.location.Location) {
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

class LoaderCallback(activity: AppCompatActivity) : BaseLoaderCallback(activity)
