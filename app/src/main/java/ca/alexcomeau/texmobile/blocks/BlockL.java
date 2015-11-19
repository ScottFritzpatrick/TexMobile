package ca.alexcomeau.texmobile.blocks;

import android.graphics.Color;
import ca.alexcomeau.texmobile.Coordinate;

public class BlockL extends Block {
    protected static final int blockColor = Color.rgb(255,165,0); //orange
    protected static final Coordinate[][] rotations = new Coordinate[][]{
            {new Coordinate(0,2),new Coordinate(1,2),new Coordinate(0,1),new Coordinate(2,2)},
            {new Coordinate(1,1),new Coordinate(1,2),new Coordinate(1,3),new Coordinate(0,3)},
            {new Coordinate(0,1),new Coordinate(1,1),new Coordinate(2,2),new Coordinate(2,1)},
            {new Coordinate(1,1),new Coordinate(1,2),new Coordinate(1,3),new Coordinate(2,1)}
    };

    public BlockL(Coordinate start)
    {
        super(start);
    }
}
