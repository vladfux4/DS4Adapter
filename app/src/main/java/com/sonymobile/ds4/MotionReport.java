package com.sonymobile.ds4;

import android.os.Parcel;
import android.os.Parcelable;

public class MotionReport
        implements Parcelable
{
    public static final Parcelable.Creator<MotionReport> CREATOR = new Parcelable.Creator<MotionReport>()
    {
        public MotionReport createFromParcel(Parcel paramAnonymousParcel)
        {
            return new MotionReport(paramAnonymousParcel);
        }

        public MotionReport[] newArray(int paramAnonymousInt)
        {
            return new MotionReport[paramAnonymousInt];
        }
    };


    public sceFVector3 acceleration = new sceFVector3();
    public sceFVector3 angularVelocity = new sceFVector3();
    public sceFQuaternion orientation = new sceFQuaternion();
    public long timestamp;

    public MotionReport() {}

    public MotionReport(Parcel paramParcel)
    {
        this.orientation.x = paramParcel.readFloat();
        this.orientation.y = paramParcel.readFloat();
        this.orientation.z = paramParcel.readFloat();
        this.orientation.w = paramParcel.readFloat();
        this.angularVelocity.x = paramParcel.readFloat();
        this.angularVelocity.y = paramParcel.readFloat();
        this.angularVelocity.z = paramParcel.readFloat();
        this.acceleration.x = paramParcel.readFloat();
        this.acceleration.y = paramParcel.readFloat();
        this.acceleration.z = paramParcel.readFloat();
        this.timestamp = paramParcel.readLong();
    }

    public int describeContents()
    {
        return 0;
    }

    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
        paramParcel.writeFloat(this.orientation.x);
        paramParcel.writeFloat(this.orientation.y);
        paramParcel.writeFloat(this.orientation.z);
        paramParcel.writeFloat(this.orientation.w);
        paramParcel.writeFloat(this.angularVelocity.x);
        paramParcel.writeFloat(this.angularVelocity.y);
        paramParcel.writeFloat(this.angularVelocity.z);
        paramParcel.writeFloat(this.acceleration.x);
        paramParcel.writeFloat(this.acceleration.y);
        paramParcel.writeFloat(this.acceleration.z);
        paramParcel.writeLong(this.timestamp);
    }

    public class sceFQuaternion
    {
        public float w;
        public float x;
        public float y;
        public float z;

        public sceFQuaternion() {}
    }

    public class sceFVector3
    {
        public float x;
        public float y;
        public float z;

        public sceFVector3() {}
    }
}
