package com.sonymobile.ds4;

import android.os.Parcel;
import android.os.Parcelable;


public class TouchReport
        implements Parcelable
{
    public static final Parcelable.Creator<TouchReport> CREATOR = new Parcelable.Creator<TouchReport>()
    {
        public TouchReport createFromParcel(Parcel paramAnonymousParcel)
        {
            return new TouchReport(paramAnonymousParcel);
        }

        public TouchReport[] newArray(int paramAnonymousInt)
        {
            return new TouchReport[paramAnonymousInt];
        }
    };
    public int dataCount;
    public long timestamp;
    public TouchData touch0 = new TouchData();
    public TouchData touch1 = new TouchData();

    public TouchReport() {}

    public TouchReport(Parcel paramParcel)
    {
        this.dataCount = paramParcel.readInt();
        this.touch0.id = paramParcel.readInt();
        this.touch0.x = paramParcel.readInt();
        this.touch0.y = paramParcel.readInt();
        this.touch1.id = paramParcel.readInt();
        this.touch1.x = paramParcel.readInt();
        this.touch1.y = paramParcel.readInt();
        this.timestamp = paramParcel.readLong();
    }

    public int describeContents()
    {
        return 0;
    }

    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
        paramParcel.writeInt(this.dataCount);
        paramParcel.writeInt(this.touch0.id);
        paramParcel.writeInt(this.touch0.x);
        paramParcel.writeInt(this.touch0.y);
        paramParcel.writeInt(this.touch1.id);
        paramParcel.writeInt(this.touch1.x);
        paramParcel.writeInt(this.touch1.y);
        paramParcel.writeLong(this.timestamp);
    }

    public class TouchData
    {
        public int id;
        public int x;
        public int y;

        public TouchData() {}
    }
}
