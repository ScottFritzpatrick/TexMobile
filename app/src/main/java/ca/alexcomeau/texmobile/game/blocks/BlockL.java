package ca.alexcomeau.texmobile.game.blocks;

import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;

public class BlockL extends Block {
    public BlockL(Point start)
    {
        super(start,
              new Point[][]{
                      {new Point(0,2),new Point(1,2),new Point(2,2),new Point(0,1)},
                      {new Point(1,3),new Point(0,3),new Point(1,2),new Point(1,1)},
                      {new Point(2,2),new Point(0,1),new Point(1,1),new Point(2,1)},
                      {new Point(1,3),new Point(1,2),new Point(1,1),new Point(2,1)}
              },
              Block.L);
    }

    protected BlockL(Parcel in) {
        super(in);
    }

    public static final Parcelable.Creator<BlockL> CREATOR = new Parcelable.Creator<BlockL>() {
        public BlockL createFromParcel(Parcel in) {
            return new BlockL(in);
        }

        public BlockL[] newArray(int size) {
            return new BlockL[size];
        }
    };
}
