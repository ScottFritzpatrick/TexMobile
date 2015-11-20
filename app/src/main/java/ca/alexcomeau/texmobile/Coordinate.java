package ca.alexcomeau.texmobile;

import android.os.Parcel;
import android.os.Parcelable;

// Data class
public class Coordinate implements Parcelable {
    public int x;
    public int y;

    public Coordinate(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public Coordinate add(Coordinate other) { return new Coordinate(x + other.x, y + other.y); }
    public boolean equals(Coordinate other) { return (x == other.x && y == other.y); }

    // ===== Parcelable stuff ========================
    protected Coordinate(Parcel in) {
        x = in.readInt();
        y = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(x);
        dest.writeInt(y);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Coordinate> CREATOR = new Parcelable.Creator<Coordinate>() {
        @Override
        public Coordinate createFromParcel(Parcel in) {
            return new Coordinate(in);
        }

        @Override
        public Coordinate[] newArray(int size) {
            return new Coordinate[size];
        }
    };
}