package ca.alexcomeau.texmobile.blocks;

import android.graphics.Color;
import ca.alexcomeau.texmobile.Coordinate;

public class BlockO extends Block {
    protected static final int blockColor = Color.YELLOW;
    protected static final Coordinate[][] rotations = new Coordinate[][]{
            {new Coordinate(1,1),new Coordinate(1,2),new Coordinate(2,1),new Coordinate(2,2)}
    };

    public BlockO(Coordinate start) { super(start); }

    // Overrides -- the O block can't rotate so these can be simplified
    @Override
    public void rotateRight() { }
    @Override
    public void rotateLeft() { }
}