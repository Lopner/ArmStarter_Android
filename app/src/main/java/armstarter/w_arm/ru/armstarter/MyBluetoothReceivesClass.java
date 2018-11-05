package armstarter.w_arm.ru.armstarter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyBluetoothReceivesClass {
    private Context MainContext;
    B_STATE Bluetooth_state;
    B_SCAN Bluetooth_scan;
    B_ACL Bluetooth_acl;

    //**********************************************************************************************
    // вложенные классы
    //**********************************************************************************************
    // Пояснение:
    // Used as an int extra field in ACTION_STATE_CHANGED intents to request the current power state.
    // Possible values are: STATE_OFF, STATE_TURNING_ON, STATE_ON, STATE_TURNING_OFF
    public class B_STATE extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Toast.makeText(MainContext, "STATE - STATE_OFF",Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Toast.makeText(MainContext, "STATE - STATE_TURNING_OFF",Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Toast.makeText(MainContext, "STATE - STATE_ON",Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Toast.makeText(MainContext, "STATE - STATE_TURNING_ON",Toast.LENGTH_SHORT).show();
                        break;
                }

            }

        }

    }

    // Пояснение:
    //  Used as an int extra field in ACTION_SCAN_MODE_CHANGED intents to request the current scan mode.
    //  Possible values are: SCAN_MODE_NONE, SCAN_MODE_CONNECTABLE, SCAN_MODE_CONNECTABLE_DISCOVERABLE
    public class B_SCAN extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {

                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Toast.makeText(MainContext, "SCAN - SCAN_MODE_CONNECTABLE_DISCOVERABLE",Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Toast.makeText(MainContext, "SCAN - SCAN_MODE_CONNECTABLE",Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Toast.makeText(MainContext, "SCAN - SCAN_MODE_NONE",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }

    }
    // Пояснение: -----
    //        Broadcast Action: Indicates a low level (ACL) connection has been established with a remote device.
    //        Always contains the extra field EXTRA_DEVICE.
    //        ACL connections are managed automatically by the Android Bluetooth stack.
    //        Requires Manifest.permission.BLUETOOTH to receive.
    //        Constant Value: "android.bluetooth.device.action.ACL_CONNECTED"
    public class B_ACL extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            switch (action){
                case BluetoothDevice.ACTION_ACL_CONNECTED:
                    Toast.makeText(MainContext, "ACL - ACTION_ACL_CONNECTED",Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    Toast.makeText(MainContext, "ACL - ACTION_ACL_DISCONNECTED",Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED:
                    Toast.makeText(MainContext, "ACL - ACTION_ACL_DISCONNECT_REQUESTED",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }


    //**********************************************************************************************
    // end: вложенные классы
    //**********************************************************************************************

    //конструктор вызываемого класса в программе
    MyBluetoothReceivesClass(Context mContext) {
        MainContext = mContext;
        Bluetooth_state = new B_STATE();
        Bluetooth_scan = new B_SCAN();
        Bluetooth_acl = new B_ACL();
    }




}
