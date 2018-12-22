package armstarter.w_arm.ru.armstarter;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MyBluetoothReceivesClass {
    private final static int REQUEST_ENABLE_BT = 1;
    private final static String ARM_DEVICE_BT = "MONSTER-PC";
    private final static String LOG_ARM_DEBUG = "arm_debug";

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
                        Log.d(LOG_ARM_DEBUG, "STATE - STATE_OFF");
                        Toast.makeText(MainContext, "STATE - STATE_OFF",Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(LOG_ARM_DEBUG, "STATE - STATE_TURNING_OFF");
                        Toast.makeText(MainContext, "STATE - STATE_TURNING_OFF",Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(LOG_ARM_DEBUG, "STATE - STATE_ON");
                        Toast.makeText(MainContext, "STATE - STATE_ON",Toast.LENGTH_SHORT).show();

                        Log.d(LOG_ARM_DEBUG, "Создаем createBluetoothAdapter()");
                        createBluetoothAdapter();

                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(LOG_ARM_DEBUG, "STATE - STATE_TURNING_ON");
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
                        Log.d(LOG_ARM_DEBUG, "SCAN - SCAN_MODE_CONNECTABLE_DISCOVERABLE");
                        Toast.makeText(MainContext, "SCAN - SCAN_MODE_CONNECTABLE_DISCOVERABLE",Toast.LENGTH_SHORT).show();

                        Log.d(LOG_ARM_DEBUG, "Создаем createBluetoothAdapter()");
                        createBluetoothAdapter();

                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(LOG_ARM_DEBUG, "SCAN - SCAN_MODE_CONNECTABLE");
                        Toast.makeText(MainContext, "SCAN - SCAN_MODE_CONNECTABLE",Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(LOG_ARM_DEBUG, "SCAN - SCAN_MODE_NONE");
                        Toast.makeText(MainContext, "SCAN - SCAN_MODE_NONE",Toast.LENGTH_SHORT).show();
                        break;
                }
            }



            if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                Log.e(LOG_ARM_DEBUG, "Обработчик сканирование работает");

            }


            if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                Log.e(LOG_ARM_DEBUG, "Сканирование завершено");
            }

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                Log.e(LOG_ARM_DEBUG, "Найдено устройство");
                // Get the BluetoothDevice object from the Intent
                 BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                Log.e(LOG_ARM_DEBUG,device.getName() + " " + device.getAddress());
               // deviceListAdapter.add(device.getName() + "\n" + device.getAddress());

                //если мы нашди то что искали, то пытаемся настроить связь с этим устройсвом
                String bName = "MONSTER-PC";
                String bMac = "00:1A:7D:DA:71:13";
                String sUID = "00001101-0000-1000-8000-00805F9B34FB";
                Log.e(LOG_ARM_DEBUG, "Тест кода");
                if (device.getName().equals(bName)  && device.getAddress().equals(bMac)){
                    bluetoothAdapter.cancelDiscovery();


                    Log.e(LOG_ARM_DEBUG, "Создаем сокет на клиенте");
                    AcceptThread newthread = new AcceptThread(device, sUID);
                    newthread.start();


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
                    Log.d(LOG_ARM_DEBUG, "ACL - ACTION_ACL_CONNECTED");
                    Toast.makeText(MainContext, "ACL - ACTION_ACL_CONNECTED",Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    Log.d(LOG_ARM_DEBUG, "ACL - ACTION_ACL_DISCONNECTED");
                    Toast.makeText(MainContext, "ACL - ACTION_ACL_DISCONNECTED",Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED:
                    Log.d(LOG_ARM_DEBUG, "ACL - ACTION_ACL_DISCONNECT_REQUESTED");
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
        filter_scan.addAction(BluetoothDevice.ACTION_FOUND);
        filter_scan.addAction(BluetoothDevice.ACTION_NAME_CHANGED);
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

/*
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_COARSE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    proceedDiscovery(); // --->
                } else {
                    //TODO re-request
                }
                break;
            }
        }


*/

    protected void createBluetoothAdapter(){
        //******************************************************************************************
        //http://www.mobilab.ru/androiddev/bluetoothinandroid.html
        // создаем блютуз
        Log.d(LOG_ARM_DEBUG, "создаем блютуз соединение");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Проверяем существует ли Bluetooth на устройстве
        if(bluetoothAdapter!=null)
        {
            // С Bluetooth все в порядке.
            Log.d(LOG_ARM_DEBUG, "С Bluetooth все в порядке");


            String status;


            //проверяем вклюен ли на устройсве bluetooth
            if (bluetoothAdapter.isEnabled()) {
                // Bluetooth включен. Работаем.
                Log.d(LOG_ARM_DEBUG, "Bluetooth включен. Работаем.");

                // в качестве теста выводим наименование устройства и его MAC адрес
                String mydeviceaddress = bluetoothAdapter.getAddress();
                String mydevicename = bluetoothAdapter.getName();
                status = mydevicename+" : "+ mydeviceaddress;
                Toast.makeText(MainContext, status,Toast.LENGTH_SHORT).show();



                    mArrayAdapter = new ArrayList<String>();

                    Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                    // Если список спаренных устройств не пуст
                    Log.d(LOG_ARM_DEBUG, "Если список спаренных устройств не пуст -  pairedDevices.size() ="+pairedDevices.size());

                    boolean ThreadIsWorked  = false; // признак найденного устройства, с которым хотим соединитья

                    if( pairedDevices.size()>0 ){
                    // проходимся в цикле по этому списку
                        for(BluetoothDevice device: pairedDevices){
                            // Добавляем имена и адреса в mArrayAdapter, чтобы показать
                            mArrayAdapter.add(device.getName()+"\n"+ device.getAddress());
                            Log.d(LOG_ARM_DEBUG, device.getName());
                            if (ARM_DEVICE_BT == device.getName()){
                                //нашли наше устройство
                                ThreadIsWorked = true;
                                AcceptThread newthread = new AcceptThread(bluetoothAdapter, "0000-0000-0000-0000");
                                newthread.start();

                                break;
                            }
                        }

                    }

                    // если искомое устройство раньше не коммутировалось, то производим поиск
                    if (!ThreadIsWorked){
                        if (bluetoothAdapter != null) {

                            if (bluetoothAdapter.isDiscovering()) {
                                bluetoothAdapter.cancelDiscovery();
                            }
                            bluetoothAdapter.startDiscovery();
                            Log.e(LOG_ARM_DEBUG, "начинаем сканирование ");

                        }
                    } else {
                        Log.e(LOG_ARM_DEBUG, "BluetoothAdapter is null");
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




    private class AcceptThread extends Thread{

        private BluetoothServerSocket mmServerSocket;
        private BluetoothSocket mmSocket;
        private final String NAME = "MyDevice";
        private UUID MY_UUID;

        public AcceptThread(BluetoothDevice device,String str_uid){


            MY_UUID = UUID.nameUUIDFromBytes(str_uid.getBytes());

            // используем вспомогательную переменную, которую в дальнейшем
            // свяжем с mmServerSocket,
            BluetoothSocket  tmp=null;



            //чтобы прослушивать ожидание на подключение, используется
            try{
                // MY_UUID это UUID нашего приложения, это же значение
                // используется в клиентском приложении

                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
                Log.e(LOG_ARM_DEBUG, "Создание сокета 50%  - createRfcommSocketToServiceRecord");

            } catch(IOException e){

            }
            mmSocket= tmp;
        }

        public AcceptThread(BluetoothAdapter mBluetoothAdapter, String str_uid){


            MY_UUID = UUID.nameUUIDFromBytes(str_uid.getBytes());

                    // используем вспомогательную переменную, которую в дальнейшем
                    // свяжем с mmServerSocket,
                BluetoothServerSocket tmp=null;



                //чтобы прослушивать ожидание на подключение, используется
                try{
                    // MY_UUID это UUID нашего приложения, это же значение
                    // используется в клиентском приложении
                    tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);//String: service name for SDP record, UUID: uuid for SDP record
                } catch(IOException e){

                }
                mmServerSocket= tmp;
        }

        public void run(){
            BluetoothSocket socket=null;
            // ждем пока не произойдет ошибка или не
            // будет возвращен сокет
            while(true){
                try{
                    socket= mmServerSocket.accept();
                } catch(IOException e){
                    break;
                }
                 // если соединение было подтверждено
                if(socket!=null){
                 // управлчем соединением (в отдельном потоке)
                    manageConnectedSocket(socket);
                    try{
                        mmServerSocket.close();
                    } catch(IOException e){


                    }
                    break;
                }
            }
        }



        public void manageConnectedSocket(BluetoothSocket bt_socket){
            ConnectedThread ConThread = new ConnectedThread(bt_socket);
            ConThread.start();
        }

    /** отмена ожидания сокета */
        public void cancel(){
            try{
                mmServerSocket.close();
            } catch(IOException e){

            }
        }
    }


    //класс для чтнеия и записи данных между установленым соединением на устройствах
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            Toast.makeText(MainContext, "Чтение и запись данных 01",Toast.LENGTH_SHORT).show();
            // Получить входящий и исходящий потоки данных
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];// буферный массив
            int bytes;// bytes returned from read()

            // Прослушиваем InputStream пока не произойдет исключение
            while (true) {
                try {// читаем из InputStream
                    bytes = mmInStream.read(buffer);
                    // посылаем прочитанные байты главной деятельности
                 /*   mHandler.obtainMessage(MESSAGE_READ, bytes,-1, buffer)
                            .sendToTarget();*/
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Вызываем этот метод из главной деятельности, чтобы отправить данные
        удаленному устройству */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
            }
        }

        /* Вызываем этот метод из главной деятельности,
        чтобы разорвать соединение */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }

    }
}
