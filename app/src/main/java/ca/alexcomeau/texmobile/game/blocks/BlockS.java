package ca.alexcomeau.texmobile.game.blocks;

import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;

public class BlockS extends Block {
    public BlockS(Point start)
    {
        super(start,
              new Point[][]{
                      {new Point(1,2),new Point(2,2),new Point(1,1),new Point(0,1)},
                      {new Point(0,3),new Point(0,2),new Point(1,2),new Point(1,1)}
              },
              Block.S);
    }

    protected BlockS(Parcel in)
    {
        super(in);
    }

    public static final Parcelable.Creator<BlockS> CREATOR = new Parcelable.Creator<BlockS>() {
        public BlockS createFromParcel(Parcel in) {
            return new BlockS(in);
        }

        public BlockS[] newArray(int size) {
            return new BlockS[size];
        }
    };
}