package com.onct_ict.azukimattya.pictake

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Surface
import android.view.TextureView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader

class CameraActivity : AppCompatActivity() {

    private val textureView: TextureView by lazy {
        findViewById<TextureView>(R.id.texture_view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        OpenCVLoader.initDebug()
        LoaderCallback(this).onManagerConnected(LoaderCallbackInterface.SUCCESS)

        getSupportActionBar()!!.setTitle("Take Pictgram")

    }

    override fun onResume() {
        super.onResume()
        if (textureView.isAvailable) {
            openCamera()
        } else {
            textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                override fun onSurfaceTextureAvailable(texture: SurfaceTexture?, p1: Int, p2: Int) {
                    openCamera()
                }

                override fun onSurfaceTextureSizeChanged(texture: SurfaceTexture?, p1: Int, p2: Int) {}
                override fun onSurfaceTextureUpdated(texture: SurfaceTexture?) {}
                override fun onSurfaceTextureDestroyed(texture: SurfaceTexture?): Boolean = true
            }
        }
        takePicture()
    }

    private var cameraDevice: CameraDevice? = null

    private val cameraManager: CameraManager by lazy {
        getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    private fun openCamera() {
        // カメラの権限確認
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            val permissions = arrayOf(Manifest.permission.CAMERA)
            ActivityCompat.requestPermissions(this, permissions, 0)
            return
        }
        // ここまで

        cameraManager.openCamera("0", object: CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                cameraDevice = camera
                createCameraPreviewSession()
            }

            override fun onDisconnected(camera: CameraDevice) {
                cameraDevice?.close()
                cameraDevice = null
            }

            override fun onError(camera: CameraDevice, p1: Int) {
                cameraDevice?.close()
                cameraDevice = null
            }
        }, null)
    }

    private var captureSession: CameraCaptureSession? = null

    // カメラの画面を表示
    private fun createCameraPreviewSession() {
        if (cameraDevice == null) {
            return
        }

        // 画面サイズの取得
        val ds = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(ds)

        val texture = textureView.surfaceTexture
        texture.setDefaultBufferSize(960, 1280)
        val surface = Surface(texture)

        val previewRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        previewRequestBuilder.addTarget(surface)

        cameraDevice?.createCaptureSession(listOf(surface), object : CameraCaptureSession.StateCallback() {
            override fun onConfigured(session: CameraCaptureSession) {
                captureSession = session
                captureSession?.setRepeatingRequest(previewRequestBuilder.build(), null, null)
            }

            override fun onConfigureFailed(session: CameraCaptureSession) {}
        }, null)
        takePicture()
    }

    // カメラ撮影＆＆画像取得
    private fun takePicture() {
        val button: Button? = findViewById(R.id.photo_button)

        // ボタン押したとき
        button?.setOnClickListener {
            // カメラプレビュー停止
            captureSession?.stopRepeating()

            if (textureView.isAvailable) {
                // 画像の取得
                val bmp = textureView.bitmap
                // 画像をクラスにいったん保存
                val data = this.application as MoveData
                data.obj = bmp

                val pict = Intent(application, ImageProcessActivity::class.java)
                startActivity(pict)
            }
            // プレビュー再開
            createCameraPreviewSession()
        }
    }
}
