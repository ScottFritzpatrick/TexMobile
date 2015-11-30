package ca.alexcomeau.texmobile.game.blocks;

import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;

public class BlockO extends Block {
    public BlockO(Point start)
    {
        super(start,
                new Point[][]{
                        {new Point(1,2),new Point(2,2),new Point(1,1),new Point(2,1)}
                },
                Block.O);
    }

    // Overrides -- the O block can't rotate so these can be simplified
    @Override
    public void rotateRight() { }
    @Override
    public void rotateLeft() { }

    protected BlockO(Parcel in)
    {
        super(in);
    }

    public static final Parcelable.Creator<BlockO> CREATOR = new Parcelable.Creator<BlockO>() {
        public BlockO createFromParcel(Parcel in) {
            return new BlockO(in);
        }

        public BlockO[] newArray(int size) {
            return new BlockO[size];
        }
    };
}