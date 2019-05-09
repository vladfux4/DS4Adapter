package com.sonymobile.ds4;


import android.bluetooth.BluetoothDevice;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IHidSonyDS4Callback
        extends IInterface
{
    public abstract void onMotionReport(BluetoothDevice paramBluetoothDevice, MotionReport paramMotionReport)
            throws RemoteException;

    public abstract void onTouchReport(BluetoothDevice paramBluetoothDevice, TouchReport paramTouchReport)
            throws RemoteException;

    public static abstract class Stub
            extends Binder
            implements IHidSonyDS4Callback
    {
        private static final String DESCRIPTOR = "com.sonymobile.ds4.IHidSonyDS4Callback";
        static final int TRANSACTION_onMotionReport = 1;
        static final int TRANSACTION_onTouchReport = 2;

        public Stub()
        {
            attachInterface(this, DESCRIPTOR);
        }

        public static IHidSonyDS4Callback asInterface(IBinder paramIBinder)
        {
            if (paramIBinder == null) {
                return null;
            }
            IInterface localIInterface = paramIBinder.queryLocalInterface(DESCRIPTOR);
            if ((localIInterface != null) && ((localIInterface instanceof IHidSonyDS4Callback))) {
                return (IHidSonyDS4Callback)localIInterface;
            }
            return new Proxy(paramIBinder);
        }

        public IBinder asBinder()
        {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags)
                throws RemoteException
        {
            if (code != INTERFACE_TRANSACTION)
            {
                TouchReport touchReport = null;
                MotionReport motionReport = null;
                BluetoothDevice localBluetoothDevice = null;
                switch (code)
                {
                    default:
                        return super.onTransact(code, data, reply, flags);
                    case TRANSACTION_onTouchReport: {
                        data.enforceInterface(DESCRIPTOR);

                        if (data.readInt() != 0) {
                            localBluetoothDevice = BluetoothDevice.CREATOR.createFromParcel(data);
                        }

                        if (data.readInt() != 0) {
                            touchReport = TouchReport.CREATOR.createFromParcel(data);
                        }

                        onTouchReport(localBluetoothDevice, touchReport);
                        reply.writeNoException();
                        return true;
                    }
                    case TRANSACTION_onMotionReport: {
                        data.enforceInterface(DESCRIPTOR);
                        if (data.readInt() != 0) {
                            localBluetoothDevice = BluetoothDevice.CREATOR.createFromParcel(data);
                        }

                        if (data.readInt() != 0) {
                            motionReport = MotionReport.CREATOR.createFromParcel(data);
                        }

                        onMotionReport(localBluetoothDevice, motionReport);
                        reply.writeNoException();
                        return true;
                    }
                }
            }

            reply.writeString(DESCRIPTOR);
            return true;
        }

        private static class Proxy
                implements IHidSonyDS4Callback
        {
            private IBinder mRemote;

            Proxy(IBinder paramIBinder)
            {
                this.mRemote = paramIBinder;
            }

            public IBinder asBinder()
            {
                return this.mRemote;
            }

            public String getInterfaceDescriptor()
            {
                return DESCRIPTOR;
            }

            public void onMotionReport(BluetoothDevice paramBluetoothDevice, MotionReport paramMotionReport)
                    throws RemoteException
            {
                Parcel localParcel1 = Parcel.obtain();
                Parcel localParcel2 = Parcel.obtain();
                try
                {
                    localParcel1.writeInterfaceToken(DESCRIPTOR);
                    if (paramBluetoothDevice != null)
                    {
                        localParcel1.writeInt(1);
                        paramBluetoothDevice.writeToParcel(localParcel1, 0);
                    }
                    else
                    {
                        localParcel1.writeInt(0);
                    }
                    if (paramMotionReport != null)
                    {
                        localParcel1.writeInt(1);
                        paramMotionReport.writeToParcel(localParcel1, 0);
                    }
                    else
                    {
                        localParcel1.writeInt(0);
                    }
                    this.mRemote.transact(1, localParcel1, localParcel2, 0);
                    localParcel2.readException();
                    return;
                }
                finally
                {
                    localParcel2.recycle();
                    localParcel1.recycle();
                }
            }

            public void onTouchReport(BluetoothDevice paramBluetoothDevice, TouchReport paramTouchReport)
                    throws RemoteException
            {
                Parcel localParcel1 = Parcel.obtain();
                Parcel localParcel2 = Parcel.obtain();
                try
                {
                    localParcel1.writeInterfaceToken(DESCRIPTOR);
                    if (paramBluetoothDevice != null)
                    {
                        localParcel1.writeInt(1);
                        paramBluetoothDevice.writeToParcel(localParcel1, 0);
                    }
                    else
                    {
                        localParcel1.writeInt(0);
                    }
                    if (paramTouchReport != null)
                    {
                        localParcel1.writeInt(1);
                        paramTouchReport.writeToParcel(localParcel1, 0);
                    }
                    else
                    {
                        localParcel1.writeInt(0);
                    }
                    this.mRemote.transact(2, localParcel1, localParcel2, 0);
                    localParcel2.readException();
                    return;
                }
                finally
                {
                    localParcel2.recycle();
                    localParcel1.recycle();
                }
            }
        }
    }
}
