package ca.alexcomeau.texmobile.blocks;

import android.graphics.Color;
import ca.alexcomeau.texmobile.Coordinate;

public class BlockS extends Block {
    protected static final int blockColor = Color.MAGENTA;
    protected static final Coordinate[][] rotations = new Coordinate[][]{
            {new Coordinate(0,1),new Coordinate(1,2),new Coordinate(2,2),new Coordinate(1,1)},
            {new Coordinate(0,2),new Coordinate(0,3),new Coordinate(1,1),new Coordinate(1,2)}
    };

    public BlockS(Coordinate start)
    {
        super(start);
    }
}
