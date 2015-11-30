package ca.alexcomeau.texmobile.game.blocks;

import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;

public class BlockZ extends Block {
    public BlockZ(Point start)
    {
        super(start,
              new Point[][]{
                      {new Point(0,2),new Point(1,2),new Point(1,1),new Point(2,1)},
                      {new Point(2,3),new Point(1,2),new Point(2,2),new Point(1,1)}
              },
              Block.Z);
    }

    protected BlockZ(Parcel in)
    {
        super(in);
    }

    public static final Parcelable.Creator<BlockZ> CREATOR = new Parcelable.Creator<BlockZ>() {
        public BlockZ createFromParcel(Parcel in) {
            return new BlockZ(in);
        }

        public BlockZ[] newArray(int size) {
            return new BlockZ[size];
        }
    };
}