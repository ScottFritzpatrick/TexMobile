package ca.alexcomeau.texmobile.blocks;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;

public class BlockT extends Block {
    public BlockT(Point start)
    {
        super(start,
              new Point[][]{
                      {new Point(0,2),new Point(1,2),new Point(2,2),new Point(1,1)},
                      {new Point(1,3),new Point(1,2),new Point(0,2),new Point(1,1)},
                      {new Point(1,2),new Point(0,1),new Point(1,1),new Point(2,1)},
                      {new Point(1,3),new Point(1,2),new Point(2,2),new Point(1,1)}
              },
              Color.CYAN);
    }

    protected BlockT(Parcel in)
    {
        super(in);
    }

    public static final Parcelable.Creator<BlockT> CREATOR = new Parcelable.Creator<BlockT>() {
        public BlockT createFromParcel(Parcel in) {
            return new BlockT(in);
        }

        public BlockT[] newArray(int size) {
            return new BlockT[size];
        }
    };
}
