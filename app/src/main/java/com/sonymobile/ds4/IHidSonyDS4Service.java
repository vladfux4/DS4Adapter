package com.sonymobile.ds4;

import android.bluetooth.BluetoothDevice;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

import java.util.List;

public abstract interface IHidSonyDS4Service
        extends IInterface
{
    public abstract BluetoothDevice getBtDeviceByDS4No(int paramInt)
            throws RemoteException;

    public abstract int getDS4No(BluetoothDevice paramBluetoothDevice)
            throws RemoteException;

    public abstract int getDS4NoByInputDeviceId(int paramInt)
            throws RemoteException;

    public abstract int getInputDeviceIdByDS4No(int paramInt)
            throws RemoteException;

    public abstract List<MotionReport> getMotionReports(BluetoothDevice paramBluetoothDevice)
            throws RemoteException;

    public abstract List<TouchReport> getTouchReports(BluetoothDevice paramBluetoothDevice)
            throws RemoteException;

    public abstract boolean isDS4(BluetoothDevice paramBluetoothDevice)
            throws RemoteException;

    public abstract boolean isDS4ByInputDeviceID(int paramInt)
            throws RemoteException;

    public abstract void registerCallback(IHidSonyDS4Callback paramIHidSonyDS4Callback)
            throws RemoteException;

    public abstract boolean resetOrientation(BluetoothDevice paramBluetoothDevice)
            throws RemoteException;

    public abstract boolean setDeadbandFilter(BluetoothDevice paramBluetoothDevice, boolean paramBoolean)
            throws RemoteException;

    public abstract boolean setLightBar(BluetoothDevice paramBluetoothDevice, int paramInt1, int paramInt2, int paramInt3)
            throws RemoteException;

    public abstract boolean setLightBarBlink(BluetoothDevice paramBluetoothDevice, int paramInt1, int paramInt2)
            throws RemoteException;

    public abstract boolean setMotionReportInterval(BluetoothDevice paramBluetoothDevice, int paramInt)
            throws RemoteException;

    public abstract boolean setMotor(BluetoothDevice paramBluetoothDevice, int paramInt1, int paramInt2)
            throws RemoteException;

    public abstract boolean setTiltCollectionFilter(BluetoothDevice paramBluetoothDevice, boolean paramBoolean)
            throws RemoteException;

    public abstract void unregisterCallback(IHidSonyDS4Callback paramIHidSonyDS4Callback)
            throws RemoteException;

    public static abstract class Stub
            extends Binder
            implements IHidSonyDS4Service
    {
        private static final String DESCRIPTOR = "com.sonymobile.ds4.IHidSonyDS4Service";
        static final int TRANSACTION_registerCallback = 1;
        static final int TRANSACTION_unregisterCallback = 2;
        static final int TRANSACTION_setMotor = 3;
        static final int TRANSACTION_setLightBar = 4;
        static final int TRANSACTION_setLightBarBlink = 5;
        static final int TRANSACTION_resetOrientation = 6;
        static final int TRANSACTION_setTiltCollectionFilter = 7;
        static final int TRANSACTION_setDeadbandFilter = 8;
        static final int TRANSACTION_getMotionReports = 9;
        static final int TRANSACTION_getTouchReports = 10;
        static final int TRANSACTION_setMotionReportInterval = 11;
        static final int TRANSACTION_isDS4 = 12;
        static final int TRANSACTION_isDS4ByInputDeviceID = 13;
        static final int TRANSACTION_getDS4No = 14;
        static final int TRANSACTION_getDS4NoByInputDeviceId = 15;
        static final int TRANSACTION_getBtDeviceByDS4No = 16;
        static final int TRANSACTION_getInputDeviceIdByDS4No = 17;

        public Stub()
        {
            attachInterface(this, DESCRIPTOR);
        }

        public static IHidSonyDS4Service asInterface(IBinder paramIBinder)
        {
            if (paramIBinder == null) {
                return null;
            }
            IInterface localIInterface = paramIBinder.queryLocalInterface(DESCRIPTOR);
            if ((localIInterface != null) && ((localIInterface instanceof IHidSonyDS4Service))) {
                return (IHidSonyDS4Service)localIInterface;
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
                BluetoothDevice device = null;
                boolean state = false;
                boolean result = false;
                int integer_result = 0;

                switch (code)
                {
                    default:
                        return super.onTransact(code, data, reply, flags);
                    case TRANSACTION_getInputDeviceIdByDS4No:
                        data.enforceInterface(DESCRIPTOR);
                        integer_result = getInputDeviceIdByDS4No(data.readInt());
                        reply.writeNoException();
                        reply.writeInt(integer_result);
                        return true;
                    case TRANSACTION_getBtDeviceByDS4No:
                        data.enforceInterface(DESCRIPTOR);
                        BluetoothDevice ret_device = null;
                        ret_device = getBtDeviceByDS4No(data.readInt());
                        reply.writeNoException();
                        if (ret_device != null)
                        {
                            reply.writeInt(1);
                            ret_device.writeToParcel(reply, 1);
                        }
                        else
                        {
                            reply.writeInt(0);
                        }
                        return true;
                    case TRANSACTION_getDS4NoByInputDeviceId:
                        data.enforceInterface(DESCRIPTOR);
                        integer_result = getDS4NoByInputDeviceId(data.readInt());
                        reply.writeNoException();
                        reply.writeInt(integer_result);
                        return true;
                    case TRANSACTION_getDS4No:
                        data.enforceInterface(DESCRIPTOR);
                        if (data.readInt() != 0) {
                            device = BluetoothDevice.CREATOR.createFromParcel(data);
                        }

                        integer_result = getDS4No(device);
                        reply.writeNoException();
                        reply.writeInt(integer_result);
                        return true;
                    case TRANSACTION_isDS4ByInputDeviceID:
                        data.enforceInterface(DESCRIPTOR);
                        result = isDS4ByInputDeviceID(data.readInt());
                        reply.writeNoException();
                        reply.writeInt(result ? 1 : 0);
                        return true;
                    case TRANSACTION_isDS4:
                        data.enforceInterface(DESCRIPTOR);
                        if (data.readInt() != 0) {
                            device = BluetoothDevice.CREATOR.createFromParcel(data);
                        }

                        result = isDS4(device);
                        reply.writeNoException();
                        reply.writeInt(result ? 1 : 0);
                        return true;
                    case TRANSACTION_setMotionReportInterval:
                        data.enforceInterface(DESCRIPTOR);
                        if (data.readInt() != 0) {
                            device = BluetoothDevice.CREATOR.createFromParcel(data);
                        }

                        result = setMotionReportInterval(device, data.readInt());
                        reply.writeNoException();
                        reply.writeInt(result ? 1 : 0);
                        return true;
                    case TRANSACTION_getTouchReports:
                        data.enforceInterface(DESCRIPTOR);
                        if (data.readInt() != 0) {
                            device = BluetoothDevice.CREATOR.createFromParcel(data);
                        }

                        List<TouchReport> touch_list = null;
                        touch_list = getTouchReports(device);
                        reply.writeNoException();
                        reply.writeTypedList(touch_list);
                        return true;
                    case TRANSACTION_getMotionReports:
                        data.enforceInterface(DESCRIPTOR);
                        if (data.readInt() != 0) {
                            device = BluetoothDevice.CREATOR.createFromParcel(data);
                        }

                        List<MotionReport> report_list = null;
                        report_list = getMotionReports(device);
                        reply.writeNoException();
                        reply.writeTypedList(report_list);
                        return true;
                    case TRANSACTION_setDeadbandFilter:
                        data.enforceInterface(DESCRIPTOR);
                        if (data.readInt() != 0) {
                            device = BluetoothDevice.CREATOR.createFromParcel(data);
                        }

                        if (data.readInt() != 0) {
                            state = true;
                        }

                        result = setDeadbandFilter(device, state);
                        reply.writeNoException();
                        reply.writeInt(result ? 1 : 0);
                        return true;
                    case TRANSACTION_setTiltCollectionFilter:
                        data.enforceInterface(DESCRIPTOR);
                        if (data.readInt() != 0) {
                            device = BluetoothDevice.CREATOR.createFromParcel(data);
                        }

                        if (data.readInt() != 0) {
                            state = true;
                        }

                        result = setTiltCollectionFilter(device, state);
                        reply.writeNoException();
                        reply.writeInt(result ? 1 : 0);
                        return true;
                    case TRANSACTION_resetOrientation:
                        data.enforceInterface(DESCRIPTOR);
                        if (data.readInt() != 0) {
                            device = BluetoothDevice.CREATOR.createFromParcel(data);
                        }

                        result = resetOrientation(device);
                        reply.writeNoException();
                        reply.writeInt(result ? 1 : 0);
                        return true;
                    case TRANSACTION_setLightBarBlink:
                        data.enforceInterface(DESCRIPTOR);
                        if (data.readInt() != 0) {
                            device = BluetoothDevice.CREATOR.createFromParcel(data);
                        }
                        result = setLightBarBlink(device, data.readInt(), data.readInt());
                        reply.writeNoException();
                        reply.writeInt(result ? 1 : 0);
                        return true;
                    case TRANSACTION_setLightBar:
                        data.enforceInterface(DESCRIPTOR);
                        if (data.readInt() != 0) {
                            device = BluetoothDevice.CREATOR.createFromParcel(data);
                        }

                        result = setLightBar(device, data.readInt(), data.readInt(), data.readInt());
                        reply.writeNoException();
                        reply.writeInt(result ? 1 : 0);
                        return true;
                    case TRANSACTION_setMotor: {
                        data.enforceInterface(DESCRIPTOR);
                        if (data.readInt() != 0) {
                            device = BluetoothDevice.CREATOR.createFromParcel(data);
                        }

                        result = setMotor(device, data.readInt(), data.readInt());
                        reply.writeNoException();
                        reply.writeInt(result ? 1 : 0);
                        return true;
                    }
                    case TRANSACTION_unregisterCallback:
                        data.enforceInterface(DESCRIPTOR);
                        unregisterCallback(IHidSonyDS4Callback.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        return true;

                    case TRANSACTION_registerCallback:
                        data.enforceInterface(DESCRIPTOR);
                        registerCallback(IHidSonyDS4Callback.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        return true;
                }

            }

            reply.writeString(DESCRIPTOR);
            return true;
        }

        private static class Proxy
                implements IHidSonyDS4Service
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

            public BluetoothDevice getBtDeviceByDS4No(int paramInt)
                    throws RemoteException
            {
                Parcel localParcel1 = Parcel.obtain();
                Parcel localParcel2 = Parcel.obtain();
                try
                {
                    localParcel1.writeInterfaceToken(DESCRIPTOR);
                    localParcel1.writeInt(paramInt);
                    this.mRemote.transact(TRANSACTION_getBtDeviceByDS4No, localParcel1, localParcel2, 0);
                    localParcel2.readException();
                    BluetoothDevice localBluetoothDevice;
                    if (localParcel2.readInt() != 0) {
                        localBluetoothDevice = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(localParcel2);
                    } else {
                        localBluetoothDevice = null;
                    }
                    return localBluetoothDevice;
                }
                finally
                {
                    localParcel2.recycle();
                    localParcel1.recycle();
                }
            }

            public int getDS4No(BluetoothDevice paramBluetoothDevice)
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
                    this.mRemote.transact(TRANSACTION_getDS4No, localParcel1, localParcel2, 0);
                    localParcel2.readException();
                    int i = localParcel2.readInt();
                    return i;
                }
                finally
                {
                    localParcel2.recycle();
                    localParcel1.recycle();
                }
            }

            public int getDS4NoByInputDeviceId(int paramInt)
                    throws RemoteException
            {
                Parcel localParcel1 = Parcel.obtain();
                Parcel localParcel2 = Parcel.obtain();
                try
                {
                    localParcel1.writeInterfaceToken(DESCRIPTOR);
                    localParcel1.writeInt(paramInt);
                    this.mRemote.transact(TRANSACTION_getDS4NoByInputDeviceId, localParcel1, localParcel2, 0);
                    localParcel2.readException();
                    paramInt = localParcel2.readInt();
                    return paramInt;
                }
                finally
                {
                    localParcel2.recycle();
                    localParcel1.recycle();
                }
            }

            public int getInputDeviceIdByDS4No(int paramInt)
                    throws RemoteException
            {
                Parcel localParcel1 = Parcel.obtain();
                Parcel localParcel2 = Parcel.obtain();
                try
                {
                    localParcel1.writeInterfaceToken(DESCRIPTOR);
                    localParcel1.writeInt(paramInt);
                    this.mRemote.transact(TRANSACTION_getInputDeviceIdByDS4No, localParcel1, localParcel2, 0);
                    localParcel2.readException();
                    paramInt = localParcel2.readInt();
                    return paramInt;
                }
                finally
                {
                    localParcel2.recycle();
                    localParcel1.recycle();
                }
            }

            public String getInterfaceDescriptor()
            {
                return DESCRIPTOR;
            }

            public List<MotionReport> getMotionReports(BluetoothDevice paramBluetoothDevice)
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

                    this.mRemote.transact(TRANSACTION_getMotionReports, localParcel1, localParcel2, 0);
                    localParcel2.readException();

                    List<MotionReport> retList = localParcel2.createTypedArrayList(MotionReport.CREATOR);
                    return retList;
                }
                finally
                {
                    localParcel2.recycle();
                    localParcel1.recycle();
                }
            }

            public List<TouchReport> getTouchReports(BluetoothDevice paramBluetoothDevice)
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
                    this.mRemote.transact(TRANSACTION_getTouchReports, localParcel1, localParcel2, 0);
                    localParcel2.readException();
                    List<TouchReport> retList = localParcel2.createTypedArrayList(TouchReport.CREATOR);
                    return retList;
                }
                finally
                {
                    localParcel2.recycle();
                    localParcel1.recycle();
                }
            }

            public boolean isDS4(BluetoothDevice paramBluetoothDevice)
                    throws RemoteException
            {
                Parcel localParcel1 = Parcel.obtain();
                Parcel localParcel2 = Parcel.obtain();
                try
                {
                    localParcel1.writeInterfaceToken(DESCRIPTOR);
                    boolean bool = true;
                    if (paramBluetoothDevice != null)
                    {
                        localParcel1.writeInt(1);
                        paramBluetoothDevice.writeToParcel(localParcel1, 0);
                    }
                    else
                    {
                        localParcel1.writeInt(0);
                    }
                    this.mRemote.transact(TRANSACTION_isDS4, localParcel1, localParcel2, 0);
                    localParcel2.readException();
                    int i = localParcel2.readInt();
                    if (i == 0) {
                        bool = false;
                    }
                    return bool;
                }
                finally
                {
                    localParcel2.recycle();
                    localParcel1.recycle();
                }
            }

            public boolean isDS4ByInputDeviceID(int paramInt)
                    throws RemoteException
            {
                Parcel localParcel1 = Parcel.obtain();
                Parcel localParcel2 = Parcel.obtain();
                try
                {
                    localParcel1.writeInterfaceToken(DESCRIPTOR);
                    localParcel1.writeInt(paramInt);
                    IBinder localIBinder = this.mRemote;
                    boolean bool = false;
                    localIBinder.transact(TRANSACTION_isDS4ByInputDeviceID, localParcel1, localParcel2, 0);
                    localParcel2.readException();
                    paramInt = localParcel2.readInt();
                    if (paramInt != 0) {
                        bool = true;
                    }
                    return bool;
                }
                finally
                {
                    localParcel2.recycle();
                    localParcel1.recycle();
                }
            }

            public void registerCallback(IHidSonyDS4Callback paramIHidSonyDS4Callback)
                    throws RemoteException
            {
                Parcel localParcel1 = Parcel.obtain();
                Parcel localParcel2 = Parcel.obtain();
                try
                {
                    IBinder binder = null;
                    localParcel1.writeInterfaceToken(DESCRIPTOR);
                    if (paramIHidSonyDS4Callback != null) {
                        binder = paramIHidSonyDS4Callback.asBinder();
                    }

                    localParcel1.writeStrongBinder(binder);
                    this.mRemote.transact(TRANSACTION_registerCallback, localParcel1, localParcel2, 0);
                    localParcel2.readException();
                    return;
                }
                finally
                {
                    localParcel2.recycle();
                    localParcel1.recycle();
                }
            }

            public boolean resetOrientation(BluetoothDevice paramBluetoothDevice)
                    throws RemoteException
            {
                Parcel localParcel1 = Parcel.obtain();
                Parcel localParcel2 = Parcel.obtain();
                try
                {
                    localParcel1.writeInterfaceToken(DESCRIPTOR);
                    boolean bool = true;
                    if (paramBluetoothDevice != null)
                    {
                        localParcel1.writeInt(1);
                        paramBluetoothDevice.writeToParcel(localParcel1, 0);
                    }
                    else
                    {
                        localParcel1.writeInt(0);
                    }
                    this.mRemote.transact(TRANSACTION_resetOrientation, localParcel1, localParcel2, 0);
                    localParcel2.readException();
                    int i = localParcel2.readInt();
                    if (i == 0) {
                        bool = false;
                    }
                    return bool;
                }
                finally
                {
                    localParcel2.recycle();
                    localParcel1.recycle();
                }
            }

            public boolean setDeadbandFilter(BluetoothDevice paramBluetoothDevice, boolean paramBoolean)
                    throws RemoteException
            {
                Parcel localParcel1 = Parcel.obtain();
                Parcel localParcel2 = Parcel.obtain();
                try
                {
                    localParcel1.writeInterfaceToken(DESCRIPTOR);
                    boolean bool = true;
                    if (paramBluetoothDevice != null)
                    {
                        localParcel1.writeInt(1);
                        paramBluetoothDevice.writeToParcel(localParcel1, 0);
                    }
                    else
                    {
                        localParcel1.writeInt(0);
                    }

                    localParcel1.writeInt(paramBoolean ? 1 : 0);
                    this.mRemote.transact(TRANSACTION_setDeadbandFilter, localParcel1, localParcel2, 0);
                    localParcel2.readException();
                    paramBoolean = (localParcel2.readInt() != 0);
                    if (!paramBoolean) {
                        bool = false;
                    }
                    return bool;
                }
                finally
                {
                    localParcel2.recycle();
                    localParcel1.recycle();
                }
            }

            public boolean setLightBar(BluetoothDevice paramBluetoothDevice, int paramInt1, int paramInt2, int paramInt3)
                    throws RemoteException
            {
                Parcel localParcel1 = Parcel.obtain();
                Parcel localParcel2 = Parcel.obtain();
                try
                {
                    localParcel1.writeInterfaceToken(DESCRIPTOR);
                    boolean bool = true;
                    if (paramBluetoothDevice != null)
                    {
                        localParcel1.writeInt(1);
                        paramBluetoothDevice.writeToParcel(localParcel1, 0);
                    }
                    else
                    {
                        localParcel1.writeInt(0);
                    }
                    localParcel1.writeInt(paramInt1);
                    localParcel1.writeInt(paramInt2);
                    localParcel1.writeInt(paramInt3);
                    this.mRemote.transact(TRANSACTION_setLightBar, localParcel1, localParcel2, 0);
                    localParcel2.readException();
                    paramInt1 = localParcel2.readInt();
                    if (paramInt1 == 0) {
                        bool = false;
                    }
                    return bool;
                }
                finally
                {
                    localParcel2.recycle();
                    localParcel1.recycle();
                }
            }

            public boolean setLightBarBlink(BluetoothDevice paramBluetoothDevice, int paramInt1, int paramInt2)
                    throws RemoteException
            {
                Parcel localParcel1 = Parcel.obtain();
                Parcel localParcel2 = Parcel.obtain();
                try
                {
                    localParcel1.writeInterfaceToken(DESCRIPTOR);
                    boolean bool = true;
                    if (paramBluetoothDevice != null)
                    {
                        localParcel1.writeInt(1);
                        paramBluetoothDevice.writeToParcel(localParcel1, 0);
                    }
                    else
                    {
                        localParcel1.writeInt(0);
                    }
                    localParcel1.writeInt(paramInt1);
                    localParcel1.writeInt(paramInt2);
                    this.mRemote.transact(TRANSACTION_setLightBarBlink, localParcel1, localParcel2, 0);
                    localParcel2.readException();
                    paramInt1 = localParcel2.readInt();
                    if (paramInt1 == 0) {
                        bool = false;
                    }
                    return bool;
                }
                finally
                {
                    localParcel2.recycle();
                    localParcel1.recycle();
                }
            }

            public boolean setMotionReportInterval(BluetoothDevice paramBluetoothDevice, int paramInt)
                    throws RemoteException
            {
                Parcel localParcel1 = Parcel.obtain();
                Parcel localParcel2 = Parcel.obtain();
                try
                {
                    localParcel1.writeInterfaceToken(DESCRIPTOR);
                    boolean bool = true;
                    if (paramBluetoothDevice != null)
                    {
                        localParcel1.writeInt(1);
                        paramBluetoothDevice.writeToParcel(localParcel1, 0);
                    }
                    else
                    {
                        localParcel1.writeInt(0);
                    }
                    localParcel1.writeInt(paramInt);
                    this.mRemote.transact(TRANSACTION_setMotionReportInterval, localParcel1, localParcel2, 0);
                    localParcel2.readException();
                    paramInt = localParcel2.readInt();
                    if (paramInt == 0) {
                        bool = false;
                    }
                    return bool;
                }
                finally
                {
                    localParcel2.recycle();
                    localParcel1.recycle();
                }
            }

            public boolean setMotor(BluetoothDevice paramBluetoothDevice, int paramInt1, int paramInt2)
                    throws RemoteException
            {
                Parcel localParcel1 = Parcel.obtain();
                Parcel localParcel2 = Parcel.obtain();
                try
                {
                    localParcel1.writeInterfaceToken(DESCRIPTOR);
                    boolean bool = true;
                    if (paramBluetoothDevice != null)
                    {
                        localParcel1.writeInt(1);
                        paramBluetoothDevice.writeToParcel(localParcel1, 0);
                    }
                    else
                    {
                        localParcel1.writeInt(0);
                    }
                    localParcel1.writeInt(paramInt1);
                    localParcel1.writeInt(paramInt2);
                    this.mRemote.transact(TRANSACTION_setMotor, localParcel1, localParcel2, 0);
                    localParcel2.readException();
                    paramInt1 = localParcel2.readInt();
                    if (paramInt1 == 0) {
                        bool = false;
                    }
                    return bool;
                }
                finally
                {
                    localParcel2.recycle();
                    localParcel1.recycle();
                }
            }

            public boolean setTiltCollectionFilter(BluetoothDevice paramBluetoothDevice, boolean paramBoolean)
                    throws RemoteException
            {
                Parcel localParcel1 = Parcel.obtain();
                Parcel localParcel2 = Parcel.obtain();
                try
                {
                    localParcel1.writeInterfaceToken(DESCRIPTOR);
                    boolean bool = true;
                    if (paramBluetoothDevice != null)
                    {
                        localParcel1.writeInt(1);
                        paramBluetoothDevice.writeToParcel(localParcel1, 0);
                    }
                    else
                    {
                        localParcel1.writeInt(0);
                    }
                    localParcel1.writeInt(paramBoolean ? 1 : 0);
                    this.mRemote.transact(TRANSACTION_setTiltCollectionFilter, localParcel1, localParcel2, 0);
                    localParcel2.readException();
                    paramBoolean = (localParcel2.readInt() != 0);
                    if (!paramBoolean) {
                        bool = false;
                    }
                    return bool;
                }
                finally
                {
                    localParcel2.recycle();
                    localParcel1.recycle();
                }
            }

            public void unregisterCallback(IHidSonyDS4Callback paramIHidSonyDS4Callback)
                    throws RemoteException
            {
                Parcel localParcel1 = Parcel.obtain();
                Parcel localParcel2 = Parcel.obtain();
                try
                {
                    IBinder binder = null;
                    localParcel1.writeInterfaceToken(DESCRIPTOR);
                    if (paramIHidSonyDS4Callback != null) {
                        binder = paramIHidSonyDS4Callback.asBinder();
                    }

                    localParcel1.writeStrongBinder(binder);
                    this.mRemote.transact(TRANSACTION_unregisterCallback, localParcel1, localParcel2, 0);
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
