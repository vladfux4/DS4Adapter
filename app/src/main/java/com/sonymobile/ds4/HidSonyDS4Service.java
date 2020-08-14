package com.sonymobile.ds4;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.input.InputManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;

import com.vladfux.ds4adapter.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static android.bluetooth.BluetoothAdapter.getDefaultAdapter;
import static android.support.v4.app.NotificationCompat.PRIORITY_MIN;

public class HidSonyDS4Service
        extends Service
{
    public static final String ACTION_KEY_EVENT = "com.vladfux.ds4adapter.action.KEY_EVENT";
    public static final String ACTION_MOTION_EVENT = "com.vladfux.ds4adapter.action.MOTION_EVENT";

    private static final int BD_ADDR_LEN = 6;
    private static final Integer[] DUALSHOCK4_PIDS;

    public static final int MESSAGE_DS4_CONNECTION_STATE_CHANGED = 40;
    public static final int MESSAGE_INPUT_EVENT = 128;

    private static final String TAG = "HidSonyDS4Service";
    private static HidSonyDS4Service self = null;
    public static HidSonyDS4Service getServiceObject(){
        return self;
    }

    public MsgHandler mHandler = new MsgHandler();

    private static Map<Integer, byte[]> mInputDevMap;
    private static final byte[] mInputWaitAddr;
    private static byte[] newDeviceAddr;
    private static int mInputWaitId;
    private static final int DUALSHOCK4_VID = 1356;
    private static BluetoothAdapter mAdapter = null;
    private static int mAdapterState = 10;

    private RemoteCallbackList<IHidSonyDS4Callback> callbackList = new RemoteCallbackList();
    private InputManager mInputManager = null;

    public void broadcastDS4ConnectionState(BluetoothDevice paramBluetoothDevice, int paramInt) {
        Intent localIntent = new Intent();
        if (paramInt == 1) {
            localIntent.setAction("com.sonymobile.ds4.action.DS4_CONNECTED");
        } else {
            localIntent.setAction("com.sonymobile.ds4.action.DS4_DISCONNECTED");
        }

        localIntent.putExtra("android.bluetooth.device.extra.DEVICE", paramBluetoothDevice);
        localIntent.addFlags(268435456);
        localIntent.addFlags(67108864);
        localIntent.addFlags(16777216);
        sendBroadcast(localIntent, "android.permission.BLUETOOTH");
    }

    public static class MsgHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_DS4_CONNECTION_STATE_CHANGED) {
                if (msg.arg1 == 0) { // connected
                    synchronized (newDeviceAddr) {
                        newDeviceAddr = getBytesFromAddress(((BluetoothDevice)msg.obj).getAddress());
                    }
                } else if (msg.arg1 == 1) { // disconnected
                    byte[] addr =  getBytesFromAddress(((BluetoothDevice) msg.obj).getAddress());
                    synchronized (HidSonyDS4Service.mInputDevMap) {
                        Iterator it = HidSonyDS4Service.mInputDevMap.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry localEntry = (Map.Entry)it.next();
                            if (HidSonyDS4Service.compareAddress((byte[])localEntry.getValue(), addr)) {
                                HidSonyDS4Service.mInputDevMap.remove(localEntry.getKey());
                                break;
                            }
                        }
                    }
                }
            }

            if (MESSAGE_INPUT_EVENT == msg.what) {
                KeyEvent event = (KeyEvent)msg.obj;
                int id = msg.arg1;
                HidSonyDS4Service.getServiceObject().onTouchReport(id, event);
            }
        }
    };

    public void onDS4DeviceAdded(int id, BluetoothDevice device) {
        Log.d(TAG, "DS4 added: " + id);
        broadcastDS4ConnectionState(device, 1);
    }

    public void onDS4DeviceRemoved(int id, BluetoothDevice device) {
        Log.d(TAG, "DS deleted: " + id);
        broadcastDS4ConnectionState(device, 0);
    }

    private InputManager.InputDeviceListener mInputDevListener = new InputManager.InputDeviceListener() {
        public void onInputDeviceAdded(int id) {
            Log.d(TAG, "Device added: " + id);

            InputDevice input_dev = HidSonyDS4Service.this.mInputManager.getInputDevice(id);
            if (input_dev == null) {
                Log.e(TAG, "Input device is null");
                return;
            }

            if ((input_dev.getVendorId() == DUALSHOCK4_VID)
                    && (Arrays.asList(HidSonyDS4Service.DUALSHOCK4_PIDS).contains(Integer.valueOf(input_dev.getProductId())))) {
                Log.d(TAG, "DS4 added: " + id + ", " + getAddressStringFromByte(newDeviceAddr));

                BluetoothDevice bl_device = mAdapter.getRemoteDevice(getAddressStringFromByte(newDeviceAddr));
                onDS4DeviceAdded(id, bl_device);
                synchronized (HidSonyDS4Service.mInputDevMap) {
                    HidSonyDS4Service.mInputDevMap.put(id, newDeviceAddr);
                    newDeviceAddr = mInputWaitAddr;
                }
            }
        }

        public void onInputDeviceChanged(int paramAnonymousInt) {}

        public void onInputDeviceRemoved(int id) {
            Log.d(TAG, "Device deleted: " + id);

            Iterator localIterator = HidSonyDS4Service.mInputDevMap.entrySet().iterator();
            while (localIterator.hasNext()) {
                Map.Entry localEntry = (Map.Entry)localIterator.next();
                if (((Integer)localEntry.getKey()).intValue() == id) {
                    Log.d(TAG, "DS deleted: " + id);
                    HidSonyDS4Service.mInputDevMap.remove(localEntry.getKey());
                    BluetoothDevice bl_device = mAdapter.getRemoteDevice(
                            getAddressStringFromByte((byte[])localEntry.getValue()));
                    onDS4DeviceRemoved(id, bl_device);
                    break;
                }
            }
        }
    };

    private IHidSonyDS4Service.Stub serviceInterface = new IHidSonyDS4Service.Stub() {
        public void registerCallback(IHidSonyDS4Callback paramAnonymousIHidSonyDS4Callback)
                throws RemoteException {
            Log.d(TAG, "registerCallback");

            synchronized (HidSonyDS4Service.this.callbackList) {
                HidSonyDS4Service.this.callbackList.register(paramAnonymousIHidSonyDS4Callback);
            }
        }

        public void unregisterCallback(IHidSonyDS4Callback paramAnonymousIHidSonyDS4Callback)
                throws RemoteException {
            Log.d(TAG, "unregisterCallback");

            synchronized (HidSonyDS4Service.this.callbackList) {
                HidSonyDS4Service.this.callbackList.unregister(paramAnonymousIHidSonyDS4Callback);
            }
        }

        public BluetoothDevice getBtDeviceByDS4No(int paramAnonymousInt)
                throws RemoteException {
            Log.d(TAG, "getBtDeviceByDS4No");

            return null;
        }

        public int getDS4No(BluetoothDevice paramAnonymousBluetoothDevice)
                throws RemoteException {
            Log.d(TAG, "getDS4No");
            return 0;
        }

        public int getDS4NoByInputDeviceId(int paramAnonymousInt)
                throws RemoteException {
            Log.d(TAG, "getDS4NoByInputDeviceId");
            return -1;
        }

        public int getInputDeviceIdByDS4No(int number)
                throws RemoteException {
            Log.d(TAG, "getInputDeviceIdByDS4No: " + number);

            if (HidSonyDS4Service.mAdapterState != 12) {
                return -1;
            }

            Map.Entry<Integer, byte[]> it = mInputDevMap.entrySet().iterator().next();
            int id = it.getKey().intValue();

            Log.d(TAG, "return getInputDeviceIdByDS4No " + id);
            return id;
        }

        public List<MotionReport> getMotionReports(BluetoothDevice paramAnonymousBluetoothDevice)
                throws RemoteException {
            Log.d(TAG, "getMotionReports");
            return null;
        }

        public List<TouchReport> getTouchReports(BluetoothDevice paramAnonymousBluetoothDevice)
                throws RemoteException {
            Log.d(TAG, "getTouchReports");
            return null;
        }

        public boolean isDS4(BluetoothDevice device)
                throws RemoteException {
            Log.d(TAG, "isDS4: " + device.getAddress());
            boolean ret_val = false;

            Iterator it = HidSonyDS4Service.mInputDevMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry localEntry = (Map.Entry)it.next();
                if (HidSonyDS4Service.compareAddress((byte[])localEntry.getValue(),
                        getBytesFromAddress(device.getAddress()))) {
                    ret_val = true;
                    break;
                }
            }

            return ret_val;
        }

        public boolean isDS4ByInputDeviceID(int paramAnonymousInt)
                throws RemoteException {
            Log.d(TAG, "isDS4ByInputDeviceID");
            return false;
        }


        public boolean resetOrientation(BluetoothDevice paramAnonymousBluetoothDevice)
                throws RemoteException {
            Log.d(TAG, "resetOrientation");
            return true;
        }

        public boolean setDeadbandFilter(BluetoothDevice device, boolean state)
                throws RemoteException {
            Log.d(TAG, "setDeadbandFilter: " + state);
            return true;
        }

        public boolean setLightBar(BluetoothDevice paramAnonymousBluetoothDevice, int red, int green, int blue)
                throws RemoteException {
            Log.d(TAG, "setLightBar: " + red + " " + green + " " + blue);
            return true;
        }

        public boolean setLightBarBlink(BluetoothDevice device, int arg1, int arg2)
                throws RemoteException {
            Log.d(TAG, "setLightBarBlink: " + arg1 + " " + arg2);
            return true;
        }

        public boolean setMotionReportInterval(BluetoothDevice device, int interval)
                throws RemoteException {
            Log.d(TAG, "setMotionReportInterval: " + interval);
            return true;
        }

        public boolean setMotor(BluetoothDevice paramAnonymousBluetoothDevice, int left, int right)
                throws RemoteException {
            Log.d(TAG, "setMotor: " + left + " " + right);
            return true;
        }

        public boolean setTiltCollectionFilter(BluetoothDevice paramAnonymousBluetoothDevice, boolean state)
                throws RemoteException {
            Log.d(TAG, "setTiltCollectionFilter: " + state);
            return true;
        }
    };

    static {
        DUALSHOCK4_PIDS = new Integer[] { Integer.valueOf(1476), Integer.valueOf(2508) };
        mInputDevMap = Collections.synchronizedMap(new LinkedHashMap());
        mInputWaitId = -1;
        mInputWaitAddr = new byte[] { -1, -1, -1, -1, -1, -1 };
        newDeviceAddr = mInputWaitAddr;
    }

    private static boolean compareAddress(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) {
        for (int i = 0; i < paramArrayOfByte1.length; i++) {
            if (paramArrayOfByte1[i] != paramArrayOfByte2[i]) {
                return false;
            }
        }
        return true;
    }

    private static String getAddressStringFromByte(byte[] paramArrayOfByte) {
        if ((paramArrayOfByte != null) && (paramArrayOfByte.length == BD_ADDR_LEN)) {
            return String.format("%02X:%02X:%02X:%02X:%02X:%02X",
                    new Object[] {
                            Byte.valueOf(paramArrayOfByte[0]),
                            Byte.valueOf(paramArrayOfByte[1]),
                            Byte.valueOf(paramArrayOfByte[2]),
                            Byte.valueOf(paramArrayOfByte[3]),
                            Byte.valueOf(paramArrayOfByte[4]),
                            Byte.valueOf(paramArrayOfByte[5])
                     });
        }
        return null;
    }

    private static byte[] getBytesFromAddress(String paramString) {
        int i = 0;
        byte[] arrayOfByte = new byte[6];
        int j = 0;
        while (j < paramString.length()) {
            int k = i;
            int m = j;
            if (paramString.charAt(j) != ':') {
                arrayOfByte[i] = ((byte)(byte)Integer.parseInt(paramString.substring(j, j + 2), 16));
                k = i + 1;
                m = j + 1;
            }
            j = m + 1;
            i = k;
        }
        return arrayOfByte;
    }

    private void onMotionReport(byte[] paramArrayOfByte, float[] data, long paramLong) {
        if (BluetoothAdapter.STATE_ON != mAdapterState) {
            return;
        }

        BluetoothDevice localBluetoothDevice = this.mAdapter.getRemoteDevice(paramArrayOfByte);
        MotionReport localMotionReport = new MotionReport();

        int i = 0;
        localMotionReport.orientation.x = data[0];
        localMotionReport.orientation.y = data[1];
        localMotionReport.orientation.z = data[2];
        localMotionReport.orientation.w = data[3];
        localMotionReport.angularVelocity.x = data[4];
        localMotionReport.angularVelocity.y = data[5];
        localMotionReport.angularVelocity.z = data[6];
        localMotionReport.acceleration.x = data[7];
        localMotionReport.acceleration.y = data[8];
        localMotionReport.acceleration.z = data[9];

        localMotionReport.timestamp = paramLong;
        synchronized (this.callbackList) {
            int j = this.callbackList.beginBroadcast();
            while (i < j) {
                try {
                    ((IHidSonyDS4Callback)this.callbackList.getBroadcastItem(i)).onMotionReport(localBluetoothDevice, localMotionReport);
                } catch (RemoteException localRemoteException) {}
                i++;
            }
            this.callbackList.finishBroadcast();
        }
    }

    private void onTouchReport(int input_id, KeyEvent event) {
        if (BluetoothAdapter.STATE_ON != mAdapterState) {
            return;
        }

        byte[] addr = mInputDevMap.get(input_id);
        if (null == addr) {
            return;
        }

        BluetoothDevice device = mAdapter.getRemoteDevice(getAddressStringFromByte(addr));

        if (null == device) {
            Log.d(TAG, "ERROR: No input device");
            return;
        }

        int key_code = 0;
        if (event.getKeyCode() == KeyEvent.KEYCODE_BUTTON_A) {
            key_code = 1;
//        } else if (event.getKeyCode() == KeyEvent.KEYCODE_BUTTON_MODE) {
//            key_code = 2;
        } else {
            return;
        }

        Log.d(TAG, "KEY_EVENT: " + event.toString());

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            key_code += 127;
        }

        TouchReport report = new TouchReport();
        int i = 0;
        report.dataCount = 1;
        report.touch0.id = key_code;
        report.touch0.x = 0;
        report.touch0.y = 0;
        report.touch1.id = 0;
        report.touch1.x = 0;
        report.touch1.y = 0;
        report.timestamp = event.getEventTime();
        synchronized (this.callbackList) {
            int j = this.callbackList.beginBroadcast();
            while (i < j) {
                try {
                    Log.d(TAG, "Send touch_report");
                    ((IHidSonyDS4Callback)this.callbackList.getBroadcastItem(i)).onTouchReport(device, report);
                } catch (RemoteException localRemoteException) {}
                i++;
            }
            this.callbackList.finishBroadcast();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: " + intent.toString());

        if (mAdapterState == BluetoothAdapter.STATE_ON) {
            return this.serviceInterface;
        }

        Log.d(TAG, "onBind Error: " + intent.toString());
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");

        self = this;
        mAdapterState = getDefaultAdapter().getState();
        super.onCreate();
        this.mAdapter = getDefaultAdapter();
        this.mInputManager = ((InputManager)getSystemService("input"));
        ChangeServiceState();
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "onRebind: " + intent.toString());
        super.onRebind(intent);
    }

    private final BroadcastReceiver mAdapterStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (null == action) {
                return;
            }

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch(state) {
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "STATE_TURNING_OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "STATE_TURNING_ON");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "STATE_ON");
                        ChangeServiceState();
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "STATE_OFF");
                        ChangeServiceState();
                        break;
                }
            }
        }
    };

    private final BroadcastReceiver mDeviceConnectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = null;
            if (null == action) {
                return;
            }

            switch (action) {
                case BluetoothDevice.ACTION_ACL_CONNECTED:
                    Log.d(TAG, "ACTION_ACL_CONNECTED");
                    device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device != null) {
                        Log.d(TAG, "ACTION_ACL_CONNECTED name: " + device.getName());
                        Log.d(TAG, "ACTION_ACL_CONNECTED addr: " + device.getAddress());
                        ChangeBluetoothDeviceState(device, true);
                    }
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    Log.d(TAG, "ACTION_ACL_DISCONNECTED");
                    device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device != null) {
                        Log.d(TAG, "ACTION_ACL_DISCONNECTED name: " + device.getName());
                        Log.d(TAG, "ACTION_ACL_DISCONNECTED addr: " + device.getAddress());
                        ChangeBluetoothDeviceState(device, false);
                    }
                    break;
            }
        }
    };

    private final BroadcastReceiver mKeyEventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (null == action) {
                return;
            }

            switch (action) {
                case HidSonyDS4Service.ACTION_KEY_EVENT:
                    KeyEvent key_event = intent.getParcelableExtra("com.vladfux.ds4adapter.extra.KEY_EVENT");
                    Message msg = new Message();
                    msg.what = HidSonyDS4Service.MESSAGE_INPUT_EVENT;
                    msg.arg1 = key_event.getDevice().getId();
                    msg.obj = key_event;
                    HidSonyDS4Service.getServiceObject().mHandler.sendMessage(msg);
                    break;
                case HidSonyDS4Service.ACTION_MOTION_EVENT:
//                    MotionEvent motion_event = intent.getParcelableExtra("com.vladfux.ds4adapter.extra.MOTION_EVENT");
//                    Log.d("Main", "MOTION_EVENT: " + motion_event.toString());
                    break;
            }
        }
    };

    public void ChangeBluetoothDeviceState(BluetoothDevice device, boolean state) {
        Message connect_msg = new Message();
        connect_msg.what = HidSonyDS4Service.MESSAGE_DS4_CONNECTION_STATE_CHANGED;
        connect_msg.arg1 = state ? 0 : 1;
        connect_msg.obj = device;
        if (null != HidSonyDS4Service.getServiceObject()) {
            HidSonyDS4Service.getServiceObject().mHandler.sendMessage(connect_msg);
        }
    }

    public void ChangeServiceState() {
        Intent service_start = new Intent(this, HidSonyDS4Service.class);
        ContextCompat.startForegroundService(this, service_start );

        // Create the Foreground Service
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel(notificationManager) : "";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
        Notification notification = notificationBuilder.setOngoing(true)
                .setPriority(PRIORITY_MIN)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build();

        startForeground(101, notification);
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(NotificationManager notificationManager){
        String channelId = "com.vladfux4.ds4adapter.notification_channel";
        String channelName = "DS4 Adapter service";
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        // omitted the LED color
        channel.setImportance(NotificationManager.IMPORTANCE_NONE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(channel);
        return channelId;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: " + intent.toString());

        mAdapterState = getDefaultAdapter().getState();
        Log.d(TAG, "mAdapterState: " + mAdapterState);

        if (mAdapterState == BluetoothAdapter.STATE_ON) {
            this.mInputManager.getInputDeviceIds();
            this.mInputManager.registerInputDeviceListener(this.mInputDevListener, null);

            IntentFilter adapterStateFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mAdapterStateReceiver, adapterStateFilter);

            IntentFilter deviceConnectionFilter = new IntentFilter();
            deviceConnectionFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
            deviceConnectionFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
            registerReceiver(mDeviceConnectionReceiver, deviceConnectionFilter);

            IntentFilter keyEventFilter = new IntentFilter();
            keyEventFilter.addAction(HidSonyDS4Service.ACTION_KEY_EVENT);
            keyEventFilter.addAction(HidSonyDS4Service.ACTION_MOTION_EVENT);
            registerReceiver(mKeyEventReceiver, keyEventFilter);

        } else if (mAdapterState == BluetoothAdapter.STATE_OFF){
            this.mInputManager.unregisterInputDeviceListener(this.mInputDevListener);

            unregisterReceiver(mAdapterStateReceiver);
            unregisterReceiver(mDeviceConnectionReceiver);
            unregisterReceiver(mKeyEventReceiver);

            stopSelf();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: " + intent.toString());
        super.onUnbind(intent);
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i(TAG, "onTaskRemoved");
    }
}
