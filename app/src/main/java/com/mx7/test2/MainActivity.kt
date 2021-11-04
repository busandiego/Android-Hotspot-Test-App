package com.mx7.test2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.net.wifi.WifiManager
import android.net.wifi.WifiManager.LocalOnlyHotspotCallback
import android.net.wifi.WifiManager.LocalOnlyHotspotReservation
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mx7.test2.hotspotmanager.ClientScanResult
import com.mx7.test2.hotspotmanager.FinishScanListener
import com.mx7.test2.hotspotmanager.WifiApManager
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.net.URI
import java.util.*
import kotlin.collections.ArrayList

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mainAdapter: MainAdapter
    var list: MutableList<String> = mutableListOf()
    private lateinit var mutableList: ArrayList<ClientScanResult>
    var mReservation: LocalOnlyHotspotReservation? = null

    private lateinit var wam: WifiApManager

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getClientList()

        Handler(Looper.getMainLooper()).postDelayed(
            {
                Log.d(TAG, "onCreate: >>> ${list}")
                val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
                mainAdapter = MainAdapter(list, this)
                recyclerView.adapter = mainAdapter
                recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                Log.d(TAG, "onFinishScan: >>>>>>>>> ${mainAdapter.getItemCount()}")

            }, 4000
        )

        // turnOffHotspot()
        // app 아이피 구하기
        // getWifiApIpAddress()

        var on: Button = findViewById(R.id.on_btn)
        var off: Button = findViewById(R.id.off_btn)
        var clientBtn: Button = findViewById(R.id.client_btn)

        on.setOnClickListener(this)
        off.setOnClickListener(this)
        clientBtn.setOnClickListener(this)

    }


    fun getClientList(){
        wam = WifiApManager(this)
        wam.getClientList(true, object : FinishScanListener {
            override fun onFinishScan(clients: ArrayList<ClientScanResult?>?) {
                Log.d(TAG, "onFinishScan: >>>>> ${clients}")

                if (clients != null) {
                    for (clientScanResult in clients) {
                        if (clientScanResult != null) {
                            list.add(clientScanResult.ipAddr)
                        }

                        Log.d(TAG, "list: ${list}")
                        Log.d(TAG, "onFinishScan: ${clientScanResult?.isReachable}")
                        Log.d(TAG, "onFinishScan: ${clientScanResult?.ipAddr}")
                        Log.d(TAG, "onFinishScan: ${clientScanResult?.device}")
                        Log.d(TAG, "onFinishScan: ${clientScanResult?.hWAddr}")
                    }
                }
            }
        })
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    fun turnOnHotspot() {

        val manager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        if (
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.startLocalOnlyHotspot(
                @RequiresApi(Build.VERSION_CODES.O)
                object : LocalOnlyHotspotCallback() {
                    override fun onStarted(reservation: LocalOnlyHotspotReservation) {
                        super.onStarted(reservation)
                        Log.d("TAG", "Wifi Hotspot is on now")
                        mReservation = reservation
                    }

                    override fun onStopped() {
                        super.onStopped()
                        Log.d("TAG", "onStopped: ")
                    }

                    override fun onFailed(reason: Int) {
                        super.onFailed(reason)
                        Log.d("TAG", "onFailed: $reason")
                    }
                }, Handler()
            )


        }


    }

    fun turnOffHotspot() {
        if (mReservation != null) {
            Log.d("TAG", "Wifi Hotspot off")
            mReservation!!.close()
        }
    }


    fun getWifiApIpAddress(): String? {
        try {
            val en: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
            while (en
                    .hasMoreElements()
            ) {
                val intf: NetworkInterface = en.nextElement()
                if (intf.getName().contains("wlan")) {
                    val enumIpAddr: Enumeration<InetAddress> = intf.getInetAddresses()
                    while (enumIpAddr
                            .hasMoreElements()
                    ) {
                        val inetAddress: InetAddress = enumIpAddr.nextElement()

                        Log.d(TAG, "getWifiApIpAddress: >>> $inetAddress ")
                        if (!inetAddress.isLoopbackAddress()
                            && inetAddress.address.size == 4
                        ) {
                            Log.d("size", inetAddress.getHostAddress())
                            return inetAddress.getHostAddress()
                        }
                    }
                }
            }
        } catch (ex: SocketException) {
            Log.e("SocketException", ex.toString())
        }
        return null
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View) {
        when(v.id){
            R.id.on_btn -> {
                turnOnHotspot()
            }

            R.id.off_btn -> {
                // hotspot 끄기
                turnOffHotspot()
            }

            R.id.client_btn -> {
                getClientList()
                mainAdapter.notifyDataSetChanged()
            }
        }
    }

}