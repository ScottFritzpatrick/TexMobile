package ca.alexcomeau.texmobile.blocks;

import android.graphics.Color;
import ca.alexcomeau.texmobile.Coordinate;

public class BlockZ extends Block {
    protected static final int blockColor = Color.GREEN;
    protected static final Coordinate[][] rotations = new Coordinate[][]{
            {new Coordinate(0,2),new Coordinate(1,2),new Coordinate(1,1),new Coordinate(2,1)},
            {new Coordinate(1,1),new Coordinate(1,2),new Coordinate(2,2),new Coordinate(2,3)}
    };

    public BlockZ(Coordinate start)
    {
        super(start);
    }
}