package ca.alexcomeau.texmobile.blocks;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;

public class BlockI extends Block {
    public BlockI(Point start)
    {
        super(start,
              new Point[][]{
                      {new Point(0,2),new Point(1,2),new Point(2,2),new Point(3,2)},
                      {new Point(2,3),new Point(2,2),new Point(2,1),new Point(2,0)}
              },
              Color.RED);
    }

    protected BlockI(Parcel in)
    {
        super(in);
    }

    public static final Parcelable.Creator<BlockI> CREATOR = new Parcelable.Creator<BlockI>() {
        public BlockI createFromParcel(Parcel in) {
            return new BlockI(in);
        }

        public BlockI[] newArray(int size) {
            return new BlockI[size];
        }
    };
}
