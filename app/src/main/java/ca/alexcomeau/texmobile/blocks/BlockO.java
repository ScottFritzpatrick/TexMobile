package ca.alexcomeau.texmobile.blocks;

import android.graphics.Color;
import android.graphics.Point;

public class BlockO extends Block {
    public BlockO(Point start)
    {
        super(start,
                new Point[][]{
                        {new Point(1,1),new Point(1,2),new Point(2,1),new Point(2,2)}
                },
                Color.YELLOW);
    }

    // Overrides -- the O block can't rotate so these can be simplified
    @Override
    public void rotateRight() { }
    @Override
    public void rotateLeft() { }
}