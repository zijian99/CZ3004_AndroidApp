package com.example.ntugroup35.Bluetooth;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.example.ntugroup35.MainActivity;
import com.example.ntugroup35.R;



/**
 * This fragment controls Bluetooth to communicate with other devices.
 */
public class BluetoothFragment extends Fragment {

    /**
     * Tag for log in this fragment
     */
    private static final String TAG = "BluetoothFragment";
    /**
     * Request code for location permission
     */
    private final int LOCATION_PERMISSION_REQUEST = 101;
    // Intent request codes from device list activity
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // Layout Views
    private ListView conversationView;
    private EditText mOutEditText;
    private Button sendButton;
    private BluetoothDevice lastDevice;

    /**
     * Constructor
     */
    public BluetoothFragment(){ }
    /**
     * Name of the connected device
     */
    private String connectedDeviceName = null;

    /**
     * Array adapter for the conversation thread
     */
    private ArrayAdapter<String> conversationArrayAdapter;

    /**
     * String buffer for outgoing messages
     */
    private StringBuffer mOutStringBuffer;

    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter bluetoothAdapter = null;

    /**
     * Member object for the bluetooth services
     */
    private BluetoothService bluetoothService = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Get local Bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        FragmentActivity activity = getActivity();
        if (bluetoothAdapter == null && activity != null) {
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            activity.finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (bluetoothAdapter == null) {
            return;
        }
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else {
            setupChat();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //stop bluetooth service
        if (bluetoothService != null) {
            bluetoothService.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (bluetoothService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (bluetoothService.getState() == BluetoothService.STATE_NONE) {
                // Start the Bluetooth chat services
                bluetoothService.start();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bluetooth, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        conversationView = view.findViewById(R.id.in);
        mOutEditText = view.findViewById(R.id.edit_text_out);
        sendButton = view.findViewById(R.id.button_send);
    }

    /**
     * Set up the UI and background operations for chat.
     */
    private void setupChat() {
        Log.d(TAG, "setupChat()");

        // Initialize the array adapter for the conversation thread
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        conversationArrayAdapter = new ArrayAdapter<>(activity, R.layout.message);

        conversationView.setAdapter(conversationArrayAdapter);

        // Initialize the compose field with a listener for the return key
        mOutEditText.setOnEditorActionListener(mWriteListener);

        // Initialize the send button with a listener that for click events
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(MainActivity.getTimeStarted())
                {
                    // Reset out string buffer to zero and clear the edit text field
                    mOutStringBuffer.setLength(0);
                    mOutEditText.setText(mOutStringBuffer);
                    Toast.makeText(activity, "Task has started. Please do not send additional info.", Toast.LENGTH_SHORT).show();
                }
                else{
                    // Send a message using content of the edit text widget
                    View view = getView();
                    if (view!= null) {
                        TextView textView = view.findViewById(R.id.edit_text_out);
                        String message = textView.getText().toString();
                        sendMessage(message);
                    }
                }

            }
        });

        // Initialize the BluetoothService to perform bluetooth connections
        bluetoothService = new BluetoothService(activity, messageHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer();
    }

    /**
     * Makes this device discoverable for 300 seconds (5 minutes).
     */
    @SuppressLint("MissingPermission")
    private void ensureDiscoverable() {
        if (bluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }


    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    public void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (bluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
            try{
            Toast.makeText(getActivity(), R.string.not_connected_sendMsg,
                    Toast.LENGTH_SHORT).show();}
            catch(Exception e){
                Log.e(TAG,e.toString());
            }
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothService to write
            byte[] send = message.getBytes();
            bluetoothService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            mOutEditText.setText(mOutStringBuffer);

        }
    }

    /**
     * The action listener for the EditText widget, to listen for the return key
     */
    private TextView.OnEditorActionListener mWriteListener
            = new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message);
            }
            return true;
        }
    };

    /**
     * Updates the status on the action bar.
     *
     * @param resId a string resource ID
     */
    private void setStatus(int resId) {
        FragmentActivity activity = getActivity();
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(resId);
    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private void setStatus(CharSequence subTitle) {
        FragmentActivity activity = getActivity();
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(subTitle);
    }

////////////////////////////////////////////////////////////////////////////////
    /**
     * The Handler that gets information back from the BluetoothService
     */
    @SuppressLint("HandlerLeak")
    private final Handler messageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();
            switch (msg.what) {
                //Bluetooth state change
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            setStatus(getString(R.string.bt_connected_to, connectedDeviceName));
                            conversationArrayAdapter.clear();
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            setStatus(R.string.bt_connecting);
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            setStatus(R.string.bt_not_connected);
                            break;
                    }
                    break;
                    //send message to device
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    conversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                    //receive message from device
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    boolean messageIsCommand = false;
                    if (readMessage.split(",")[0].equals("ROBOT")){
                        String[] splitString = readMessage.split(",");
                        if (splitString.length == 4 && isInteger(splitString[1]) &&
                                isInteger(splitString[2]) && splitString[3].length() == 1){
                            if (MainActivity.setRobotPosition(Integer.parseInt(splitString[1]),
                                    Integer.parseInt(splitString[2]), splitString[3].charAt(0))){
                                messageIsCommand = true;
                            }
                        } else if (splitString.length == 2){
                            MainActivity.updateRobotStatus(splitString[1]);
                            messageIsCommand = true;
                        }
                    } else if (readMessage.split(",")[0].equals("TARGET")){
                        String[] splitString = readMessage.split(",");
                        if (splitString.length == 3 && isInteger(splitString[1]) && isInteger(splitString[2])){
                            if (MainActivity.exploreTarget(Integer.parseInt(splitString[1]), Integer.parseInt(splitString[2]))){
                                messageIsCommand = true;
                            }
                        }
                        else if(splitString.length == 4 && isInteger(splitString[1]) && isInteger(splitString[2]) && isInteger(splitString[3])){
                            if (MainActivity.exploreTargetViaCoordinates(Integer.parseInt(splitString[1]), Integer.parseInt(splitString[2]),Integer.parseInt(splitString[3]))){
                                messageIsCommand = true;
                            }
                        }
                    }
//                    else if (readMessage.equals("status,END"))
                    else if (readMessage.equals("TASK,END")){
                        MainActivity.stopTimer();
                        Toast.makeText(activity, "Task has ended.", Toast.LENGTH_SHORT).show();
                    }
                    else if (readMessage.equals("W")){
                        MainActivity.setRobotPositionEXForward();
                    }
                    else if (readMessage.equals("S")){
                        MainActivity.setRobotPositionEXBackward();
                    }
                    else if (readMessage.equals("A")){
                        MainActivity.setRobotPositionEXLeft();
                    }
                    else if (readMessage.equals("D")){
                        MainActivity.setRobotPositionEXRight();
                    }
                    else if (readMessage.equals("Z")){
                        MainActivity.setRobotPositionEXHardLeftReverse();
                    }
                    else if (readMessage.equals("C")){
                        MainActivity.setRobotPositionEXHardRightReverse();
                    }
                    else if (readMessage.equals("Q")){
                        MainActivity.setRobotPositionEXHardLeft();
                    }
                    else if (readMessage.equals("E")){
                        MainActivity.setRobotPositionEXHardRight();
                    }

                    if (!messageIsCommand){
                        conversationArrayAdapter.add(connectedDeviceName + ":  " + readMessage);
                    }
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    connectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != activity) {
                        Toast.makeText(activity, "Connected to "
                                + connectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };
    //from device list activity
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DevicesList returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DevicesList returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "Bluetooth is not enabled");
                    FragmentActivity activity = getActivity();
                    if (activity != null) {
                        Toast.makeText(activity, R.string.bt_not_enabled,
                                Toast.LENGTH_SHORT).show();
                        activity.finish();
                    }
                }
        }
    }

    /**
     * Establish connection with other device
     *

     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address from device list activity bundle
        Bundle extras = data.getExtras();
        if (extras == null) {
            return;
        }
        String address = extras.getString(BluetoothScanActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        lastDevice = device;
        // Attempt to connect to the device
        bluetoothService.connect(device, secure);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
    }
    //require location and bluetooth permission to access bluetooth

    /**
     * Check permission and start the device list activity
     */
    private void checkPermissions() {
        if(getActivity()==null)
            return;
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
        } else {
            Intent intent = new Intent(getActivity(), BluetoothScanActivity.class);
            startActivityForResult(intent, REQUEST_CONNECT_DEVICE_SECURE);
        }
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.secure_connect_scan: {
                // Launch the DevicesList to see devices and do scan
                checkPermissions();
                return true;
            }
            case R.id.discoverable: {
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;
            }
            case R.id.manual:{
                //Manual request info from arena
                sendMessage("sendArena");
                return true;
            }
            case R.id.resetTimer: {
                //Reset timer
                MainActivity.resetTimer();
                return true;
            }
            case R.id.reconnectDevice:{
                //Reconnect to prev device if available
                if(lastDevice!=null)
                    bluetoothService.connect(lastDevice, true);
                else
                    Toast.makeText(getActivity(), "There is no previously connected device", Toast.LENGTH_SHORT).show();
                return true;
            }
            case R.id.resetMap:{
                //clear map
                MainActivity.clearMap();
                return true;
            }
            case R.id.presetMap:{
                //set pre-allocated obs and robot
                MainActivity.presetMap();
                return true;
            }
            case R.id.smithaMap:{
                //set pre-allocated obs and robot
                MainActivity.smithaMap();
                return true;
            }
            case R.id.eMap:{
                //set pre-allocated obs and robot
                MainActivity.eMap();
                return true;
            }

        }
        return false;
    }

    /**
     * Check whether string is an integer
     *
     * @param input string to be checked
     * @return boolean
     */
    private boolean isInteger( String input ) {
        try {
            Integer.parseInt( input );
            return true;
        }
        catch( NumberFormatException e ) {
            return false;
        }
    }
}