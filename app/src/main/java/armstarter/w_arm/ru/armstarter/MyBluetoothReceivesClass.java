package armstarter.w_arm.ru.armstarter;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MyBluetoothReceivesClass {
    private final static int REQUEST_ENABLE_BT = 1;
    private final static String ARM_DEVICE_BT = "ARMSCORER";

    private Context MainContext;

    B_STATE Bluetooth_state;
    B_SCAN Bluetooth_scan;
    B_ACL Bluetooth_acl;
    List<String> mArrayAdapter;

    /*
    BluetoothAdapter
    отвечает за работу с установленным в телефоне Bluetooth модулем. Экземпляр этого класса есть в любой программе, использующей bluetooth.
    В состав этого класса входят методы, позволяющие производить поиск доступных устройств, запрашивать список подключенных устройств, создавать экземпляр класса
    BluetoothDevice на основании известного MAC адреса и создавать BluetoothServerSocket для ожидания запроса на соединение от других устройств.
    */
    BluetoothAdapter bluetoothAdapter;
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

                        createBluetoothAdapter();

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
    // регистрация различных bluetooth приемников
    MyBluetoothReceivesClass(Context mContext) {
        //получаем контекс главного приложения
        MainContext = mContext;

        //******************************************************************************************
        //Регистрируем приемники в контексте главного приложения

        //extra state
        Bluetooth_state = new B_STATE();
        IntentFilter filter_state = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        MainContext.registerReceiver(Bluetooth_state, filter_state);

        //extra scan
        Bluetooth_scan = new B_SCAN();
        IntentFilter filter_scan = new IntentFilter();
        filter_scan.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter_scan.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter_scan.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        MainContext.registerReceiver(Bluetooth_scan, filter_scan);

        //acl
        Bluetooth_acl = new B_ACL();
        IntentFilter filter_acl = new IntentFilter();
        filter_acl.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter_acl.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter_acl.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        MainContext.registerReceiver(Bluetooth_acl, filter_acl);

        //end
        //******************************************************************************************


    }


    protected void createBluetoothAdapter(){
        //******************************************************************************************
        //http://www.mobilab.ru/androiddev/bluetoothinandroid.html
        // создаем блютуз соединение
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Проверяем существует ли Bluetooth на устройстве
        if(bluetoothAdapter!=null)
        {
            // С Bluetooth все в порядке.



            String status;


            //проверяем вклюен ли на устройсве bluetooth
            if (bluetoothAdapter.isEnabled()) {
                // Bluetooth включен. Работаем.


                // в качестве теста выводим наименование устройства и его MAC адрес
                String mydeviceaddress = bluetoothAdapter.getAddress();
                String mydevicename = bluetoothAdapter.getName();
                status = mydevicename+" : "+ mydeviceaddress;
                Toast.makeText(MainContext, status,Toast.LENGTH_SHORT).show();



                    mArrayAdapter = new ArrayList<String>();

                    Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                    // Если список спаренных устройств не пуст
                    if( pairedDevices.size()>0 ){
                    // проходимся в цикле по этому списку
                        for(BluetoothDevice device: pairedDevices){
                            // Добавляем имена и адреса в mArrayAdapter, чтобы показать
                            mArrayAdapter.add(device.getName()+"\n"+ device.getAddress());

                            if (ARM_DEVICE_BT == device.getName()){
                                //нашли наше устройство
                                break;
                            }
                        }
                    }
            }
            else
            {
                // Bluetooth выключен. Предложим пользователю включить его.
                // Если пользователь согласился на включение адаптера, в переменную enableBtIntent
                // будет записано значение RESULT_OK. В противном случае - RESULT_CANCELED.
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                //warning, maybe memory leaks?
                ((Activity) MainContext).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

            }

        }
    }


    protected List<String> ReturnSavedBeforeDevices(){
        return mArrayAdapter;
    }

    // при закрытии приложения обязательно вызвать у созданного объекта данный метод, чтоб разрегестрировать приемники
    protected void    close(){
        MainContext.unregisterReceiver(Bluetooth_acl);
        MainContext.unregisterReceiver(Bluetooth_scan);
        MainContext.unregisterReceiver(Bluetooth_state);
        mArrayAdapter.clear();
    }


}
