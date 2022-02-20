package com.mx7.test2.hotspotmanager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import com.mx7.test2.hotspotmanager.ClientScanResult;
import com.mx7.test2.hotspotmanager.FinishScanListener;
import com.mx7.test2.hotspotmanager.WIFI_AP_STATE;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;

public class WifiApManager {
    private final WifiManager mWifiManager;
    private Context context;
    private static final String TAG = "WifiApManager";

    public WifiApManager(Context context) {
        this.context = context;
        mWifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
    }

    /**
     * Show write permission settings page to user if necessary or forced
     * @param force show settings page even when rights are already granted
     */
    public void showWritePermissionSettings(boolean force) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (force || !Settings.System.canWrite(this.context)) {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + this.context.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.context.startActivity(intent);
            }
        }
    }

    /**
     * Start AccessPoint mode with the specified
     * configuration. If the radio is already running in
     * AP mode, update the new configuration
     * Note that starting in access point mode disables station
     * mode operation
     *
     * @param wifiConfig SSID, security and channel details as part of WifiConfiguration
     * @return {@code true} if the operation succeeds, {@code false} otherwise
     */
    public boolean setWifiApEnabled(WifiConfiguration wifiConfig, boolean enabled) {
        try {
            if (enabled) { // disable WiFi in any case
                mWifiManager.setWifiEnabled(false);
            }

            Method method = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            return (Boolean) method.invoke(mWifiManager, wifiConfig, enabled);
        } catch (Exception e) {
            Log.e(this.getClass().toString(), "", e);
            return false;
        }
    }

    /**
     * Gets the Wi-Fi enabled state.
     *
     * @return {@link WIFI_AP_STATE}
     * @see #isWifiApEnabled()
     */
    public WIFI_AP_STATE getWifiApState() {
        try {
            Method method = mWifiManager.getClass().getMethod("getWifiApState");

            int tmp = ((Integer) method.invoke(mWifiManager));

            // Fix for Android 4
            if (tmp >= 10) {
                tmp = tmp - 10;
            }

            return WIFI_AP_STATE.class.getEnumConstants()[tmp];
        } catch (Exception e) {
            Log.e(this.getClass().toString(), "", e);
            return WIFI_AP_STATE.WIFI_AP_STATE_FAILED;
        }
    }

    /**
     * Return whether Wi-Fi AP is enabled or disabled.
     *
     * @return {@code true} if Wi-Fi AP is enabled
     * @hide Dont open yet
     * @see #getWifiApState()
     */
    public boolean isWifiApEnabled() {
        return getWifiApState() == WIFI_AP_STATE.WIFI_AP_STATE_ENABLED;
    }

    /**
     * Gets the Wi-Fi AP Configuration.
     *
     * @return AP details in {@link WifiConfiguration}
     */
    public WifiConfiguration getWifiApConfiguration() {
        try {
            Method method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
            return (WifiConfiguration) method.invoke(mWifiManager);
        } catch (Exception e) {
            Log.e(this.getClass().toString(), "", e);
            return null;
        }
    }

    /**
     * Sets the Wi-Fi AP Configuration.
     *
     * @return {@code true} if the operation succeeded, {@code false} otherwise
     */
    public boolean setWifiApConfiguration(WifiConfiguration wifiConfig) {
        try {
            Method method = mWifiManager.getClass().getMethod("setWifiApConfiguration", WifiConfiguration.class);
            return (Boolean) method.invoke(mWifiManager, wifiConfig);
        } catch (Exception e) {
            Log.e(this.getClass().toString(), "", e);
            return false;
        }
    }

    /**
     * Gets a list of the clients connected to the Hotspot, reachable timeout is 300
     *
     * @param onlyReachables  {@code false} if the list should contain unreachable (probably disconnected) clients, {@code true} otherwise
     * @param finishListener, Interface called when the scan method finishes
     */
    public void getClientList(boolean onlyReachables, FinishScanListener finishListener) {
        getClientList(onlyReachables, 300, finishListener);
    }

    /**
     * Gets a list of the clients connected to the Hotspot
     *
     * @param onlyReachables   {@code false} if the list should contain unreachable (probably disconnected) clients, {@code true} otherwise
     * @param reachableTimeout Reachable Timout in miliseconds
     * @param finishListener,  Interface called when the scan method finishes
     */
    public void getClientList(final boolean onlyReachables, final int reachableTimeout, final FinishScanListener finishListener) {
        Runnable runnable = new Runnable() {
            public void run() {

                BufferedReader br = null;
                final ArrayList<ClientScanResult> result = new ArrayList<ClientScanResult>();


                // if API 29(Q) over, can not access "/proc/net/arp"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    try {
                        Process ipProc = Runtime.getRuntime().exec("ip neigh show");
                        ipProc.waitFor();
                        if (ipProc.exitValue() != 0) {
                            throw new Exception("Unable to access ARP entries");
                        }

                        br = new BufferedReader(new InputStreamReader(ipProc.getInputStream(), "UTF-8"));
                        String line;
                        while ((line = br.readLine()) != null) {
                            String[] neighborLine = line.split("\\s+");
                            if (neighborLine.length <= 4) {
                                continue;
                            }
                            String ip = neighborLine[0];
                            final String hwAddr = neighborLine[4];

                            InetAddress addr = InetAddress.getByName(ip);
                            if (addr.isLinkLocalAddress() || addr.isLoopbackAddress()) {
                                continue;
                            }
                            String macAddress = neighborLine[4];
                            String state = neighborLine[neighborLine.length - 1];
                            String device = neighborLine[1];

                            Log.d(TAG, "macAddress>>> " + macAddress);
                            Log.d(TAG, "state >>> " + state);
                            Log.d(TAG, "hwAddr >>> " + hwAddr);
                            Log.d(TAG, "addr >>> " + addr);
                            Log.d(TAG, "ip >>> " + ip);

                            // additional code
                            boolean isReachable = InetAddress.getByName(ip).isReachable(reachableTimeout);

                            if (!onlyReachables || isReachable) {
                                result.add(new ClientScanResult(ip, hwAddr, device, isReachable));
                            }

                            /*if (!NEIGHBOR_FAILED.equals(state) && !NEIGHBOR_INCOMPLETE.equals(state)) {
                                boolean isReachable = false;
                                try {
                                    isReachable = InetAddress.getByName(ip).isReachable(5000);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (isReachable) {
                                    result.add(new WifiClient(ip, hwAddr));
                                }
                            }*/
                        }
                    } catch(Exception e) {
                        Log.e(this.getClass().toString(), e.getMessage());
                    }

                // below 29(Q) api
                } else {
                    try {
                        br = new BufferedReader(new FileReader("/proc/net/arp"));
                        String line;
                        while ((line = br.readLine()) != null) {
                            String[] splitted = line.split(" +");

                            if ((splitted != null) && (splitted.length >= 4)) {
                                // Basic sanity check
                                String mac = splitted[3];

                                if (mac.matches("..:..:..:..:..:..")) {
                                    boolean isReachable = InetAddress.getByName(splitted[0]).isReachable(reachableTimeout);

                                    if (!onlyReachables || isReachable) {
                                        result.add(new ClientScanResult(splitted[0], splitted[3], splitted[5], isReachable));
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e(this.getClass().toString(), e.toString());
                    } finally {
                        try {
                            br.close();
                        } catch (IOException e) {
                            Log.e(this.getClass().toString(), e.getMessage());
                        }
                    }
                }

                // Get a handler that can be used to post to the main thread
                Handler mainHandler = new Handler(context.getMainLooper());
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        finishListener.onFinishScan(result);
                    }
                };
                mainHandler.post(myRunnable);
            }
        };
        Thread mythread = new Thread(runnable);
        mythread.start();
    }
}