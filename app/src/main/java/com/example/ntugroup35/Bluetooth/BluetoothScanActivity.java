package com.example.ntugroup35.Bluetooth;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ntugroup35.R;

import java.util.Set;

/**
 * This Activity appears as a dialog.
 *
 * It lists any paired devices and
 * devices detected in the area after discovery. When a device is clicked
 * ,the MAC address of the device is sent back to the parent
 * Activity in the result Intent.
 */
public class BluetoothScanActivity extends Activity {

    /**
     * Tag for Log
     */
    private static final String TAG = "BTScanActivity";

    /**
     * Return Intent extra
     */
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    /**
     * Member fields
     */
    private BluetoothAdapter bluetoothAdapter;

    /**
     * Newly discovered devices
     */
    private ArrayAdapter<String> newDeviceArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup the window
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_bluetooth_scan);

        // Set result CANCELED in case the user backs out
        setResult(Activity.RESULT_CANCELED);

        init();

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(newDeviceReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(newDeviceReceiver, filter);

    }

    /**
     * Initialize variables
     */
    @SuppressLint("MissingPermission")
    private void init(){
        // Initialize the button to perform device discovery
        Button scanButton = findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                doDiscovery();
            }
        });

        // Initialize array adapters. One for already paired devices and one for newly discovered devices
        ArrayAdapter<String> pairedDevicesArrayAdapter =
                new ArrayAdapter<String>(this, R.layout.device_name);
        newDeviceArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

        // Find and set up the ListView for paired devices and set up on click
        ListView pairedDeviceList = findViewById(R.id.paired_devices);
        pairedDeviceList.setAdapter(pairedDevicesArrayAdapter);
        pairedDeviceList.setOnItemClickListener(mDeviceClickListener);

        // Find and set up the ListView for newly discovered devices and set up on click
        ListView newDeviceList = findViewById(R.id.new_devices);
        newDeviceList.setAdapter(newDeviceArrayAdapter);
        newDeviceList.setOnItemClickListener(mDeviceClickListener);

        // Get the local Bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                pairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            String noDevices = getResources().getText(R.string.none_paired).toString();
            pairedDevicesArrayAdapter.add(noDevices);
        }

    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Make sure we're not doing discovery anymore
        if (bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        this.unregisterReceiver(newDeviceReceiver);
    }

    /**
     * Start device discover with the BluetoothAdapter
     */
    @SuppressLint("MissingPermission")
    private void doDiscovery() {
        Log.d(TAG, "doDiscovery()");

        // Indicate scanning in the title and the loading icon
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scanning);

        // Turn on sub-title for new devices
        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        // If we're already discovering, stop it
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        bluetoothAdapter.startDiscovery();
    }

    /**
     * The on-click listener for all devices in the ListViews
     */
    private AdapterView.OnItemClickListener mDeviceClickListener
            = new AdapterView.OnItemClickListener() {
        @SuppressLint("MissingPermission")
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery as we will end the activity soon
            bluetoothAdapter.cancelDiscovery();

            // Get the clicked device MAC address
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            // Create the result Intent and include the MAC address to bring back
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

            // Set result and finish this Activity
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

    /**
     * The BroadcastReceiver that listens for discovered devices and changes the title when
     * discovery is finished
     */
    private final BroadcastReceiver newDeviceReceiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Make sure the device is not a paired devices as paired device has been shown and
                // Make sure the device is not null
                if (device.getBondState() != BluetoothDevice.BOND_BONDED && device.getName()!=null) {
                    //try to remove the device to prevent same devices added to the list
                    newDeviceArrayAdapter.remove(device.getName() + "\n" + device.getAddress());
                    newDeviceArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                    Log.e("Available Device",device.getName() + "\n" + device.getAddress());
                    //Must notify to show the new device added to the list
                    newDeviceArrayAdapter.notifyDataSetChanged();
                }
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //Stop the loading icon
                setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.select_device);
                if (newDeviceArrayAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.none_found).toString();
                    newDeviceArrayAdapter.add(noDevices);
                    newDeviceArrayAdapter.notifyDataSetChanged();
                }
            }
        }
    };

}