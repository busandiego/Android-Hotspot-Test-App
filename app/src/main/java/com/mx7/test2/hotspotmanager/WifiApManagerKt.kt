package com.mx7.test2.hotspotmanager

import android.content.Context
import android.net.wifi.WifiConfiguration


class WifiApManagerKt(context: Context) {
    // private val mWifiManager: WifiManager
    // private val context: Context

    /**
     * Show write permission settings page to user if necessary or forced
     * @param force show settings page even when rights are already granted
     */
   /* fun showWritePermissionSettings(force: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (force || !Settings.System.canWrite(context)) {
                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                intent.data = Uri.parse("package:" + context.getPackageName())
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        }
    }

    *//**
     * Start AccessPoint mode with the specified
     * configuration. If the radio is already running in
     * AP mode, update the new configuration
     * Note that starting in access point mode disables station
     * mode operation
     *
     * @param wifiConfig SSID, security and channel details as part of WifiConfiguration
     * @return `true` if the operation succeeds, `false` otherwise
     *//*
    fun setWifiApEnabled(wifiConfig: WifiConfiguration?, enabled: Boolean): Boolean {
        return try {

            if (enabled) { // disable WiFi in any case
                mWifiManager.isWifiEnabled = false
            }
            val method: Method = mWifiManager.javaClass.getMethod(
                "setWifiApEnabled",
                WifiConfiguration::class.java,
                Boolean::class.javaPrimitiveType
            )
            method.invoke(mWifiManager, wifiConfig, enabled)
            as kotlin.Boolean
        }
            catch (e: Exception) {
            Log.e(this.javaClass.toString(), "", e)
            false
        }
    }// Fix for Android 4

    *//**
     * Gets the Wi-Fi enabled state.
     *
     * @return [WIFI_AP_STATE]
     * @see .isWifiApEnabled
     *//*
    val wifiApState: WIFI_AP_STATE
        get() = try {
            val method: Method = mWifiManager.javaClass.getMethod("getWifiApState")
            var tmp = method.invoke(mWifiManager) as Int

            // Fix for Android 4
            if (tmp >= 10) {
                tmp = tmp - 10
            }
            WIFI_AP_STATE::class.java.enumConstants[tmp]
        } catch (e: Exception) {
            Log.e(this.javaClass.toString(), "", e)
            WIFI_AP_STATE.WIFI_AP_STATE_FAILED
        }

    *//**
     * Return whether Wi-Fi AP is enabled or disabled.
     *
     * @return `true` if Wi-Fi AP is enabled
     * @hide Dont open yet
     * @see .getWifiApState
     *//*
    val isWifiApEnabled: Boolean
        get() = wifiApState === WIFI_AP_STATE.WIFI_AP_STATE_ENABLED

    *//**
     * Gets the Wi-Fi AP Configuration.
     *
     * @return AP details in [WifiConfiguration]
     *//*
    val wifiApConfiguration: WifiConfiguration?
        get() {
            return try {
                val method: Method = mWifiManager.javaClass.getMethod("getWifiApConfiguration")
                method.invoke(mWifiManager) as kotlin.Boolean

            } catch (e: Exception) {
                Log.e(this.javaClass.toString(), "", e)
                null
            }
        }

    *//**
     * Sets the Wi-Fi AP Configuration.
     *
     * @return `true` if the operation succeeded, `false` otherwise
     *//*
    fun setWifiApConfiguration(wifiConfig: WifiConfiguration?): Boolean {
        return try {
            val method: Method = mWifiManager.javaClass.getMethod(
                "setWifiApConfiguration",
                WifiConfiguration::class.java
            )
            method.invoke(mWifiManager, wifiConfig) as kotlin.Boolean
        } catch (e: Exception) {
            Log.e(this.javaClass.toString(), "", e)
            false
        }
    }

    *//**
     * Gets a list of the clients connected to the Hotspot, reachable timeout is 300
     *
     * @param onlyReachables  `false` if the list should contain unreachable (probably disconnected) clients, `true` otherwise
     * @param finishListener, Interface called when the scan method finishes
     *//*
    fun getClientList(onlyReachables: Boolean, finishListener: FinishScanListener) {
        getClientList(onlyReachables, 300, finishListener)
    }

    *//**
     * Gets a list of the clients connected to the Hotspot
     *
     * @param onlyReachables   `false` if the list should contain unreachable (probably disconnected) clients, `true` otherwise
     * @param reachableTimeout Reachable Timout in miliseconds
     * @param finishListener,  Interface called when the scan method finishes
     *//*
    fun getClientList(
        onlyReachables: Boolean,
        reachableTimeout: Int,
        finishListener: FinishScanListener
    ) {
        val runnable: Runnable = object : Runnable {
            override fun run() {
                var br: BufferedReader? = null
                val result = ArrayList<ClientScanResult?>()
                try {
                    br = BufferedReader(FileReader("/proc/net/arp"))
                    var line: String
                    while (br.readLine().also { line = it } != null) {
                        val splitted: Array<String> = line.split(" +").toTypedArray()
                        if (splitted != null && splitted.size >= 4) {
                            // Basic sanity check
                            val mac = splitted[3]
                            if (mac.matches("..:..:..:..:..:..")) {
                                val isReachable: Boolean =
                                    InetAddress.getByName(splitted[0]).isReachable(reachableTimeout)
                                if (!onlyReachables || isReachable) {
                                    result.add(
                                        ClientScanResult(
                                            splitted[0],
                                            splitted[3], splitted[5], isReachable
                                        )
                                    )
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(this.javaClass.toString(), e.toString())
                } finally {
                    try {
                        if (br != null) {
                            br.close()
                        }
                    } catch (e: IOException) {
                        Log.e(this.javaClass.toString(), "${e.message}")
                    }
                }

                // Get a handler that can be used to post to the main thread
                val mainHandler = Handler(context.getMainLooper())
                val myRunnable: Runnable = Runnable { finishListener.onFinishScan(result) }
                mainHandler.post(myRunnable)
            }
        }
        val mythread = Thread(runnable)
        mythread.start()
    }

    init {
        this.context = context
        mWifiManager = this.context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }*/
}