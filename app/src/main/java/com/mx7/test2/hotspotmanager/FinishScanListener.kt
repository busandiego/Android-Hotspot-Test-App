package com.mx7.test2.hotspotmanager

import com.mx7.test2.hotspotmanager.ClientScanResult

interface FinishScanListener {
    /**
     * Interface called when the scan method finishes. Network operations should not execute on UI thread
     * @param clients
     */
    fun onFinishScan(clients: ArrayList<ClientScanResult?>?)
}