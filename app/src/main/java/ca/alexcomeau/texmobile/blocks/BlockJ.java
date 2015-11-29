package ca.alexcomeau.texmobile.blocks;

import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;

public class BlockJ extends Block {
    public BlockJ(Point start)
    {
        super(start,
              new Point[][]{
                      {new Point(0,2),new Point(1,2),new Point(2,2),new Point(2,1)},
                      {new Point(1,3),new Point(1,2),new Point(1,1),new Point(0,1)},
                      {new Point(0,2),new Point(0,1),new Point(1,1),new Point(2,1)},
                      {new Point(1,3),new Point(2,3),new Point(1,2),new Point(1,1)}
              },
              Block.J);
    }

    protected BlockJ(Parcel in)
    {
        super(in);
    }

    public static final Parcelable.Creator<BlockJ> CREATOR = new Parcelable.Creator<BlockJ>() {
        public BlockJ createFromParcel(Parcel in) {
            return new BlockJ(in);
        }

        public BlockJ[] newArray(int size) {
            return new BlockJ[size];
        }
    };
}
