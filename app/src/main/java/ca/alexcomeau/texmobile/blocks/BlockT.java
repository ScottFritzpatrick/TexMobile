package ca.alexcomeau.texmobile.blocks;

import android.graphics.Color;
import android.graphics.Point;

public class BlockT extends Block {
    public BlockT(Point start)
    {
        super(start,
              new Point[][]{
                      {new Point(0,2),new Point(1,2),new Point(1,1),new Point(2,2)},
                      {new Point(1,1),new Point(1,2),new Point(1,3),new Point(0,2)},
                      {new Point(0,1),new Point(1,1),new Point(1,2),new Point(2,1)},
                      {new Point(1,1),new Point(1,2),new Point(1,3),new Point(2,2)}
              },
              Color.CYAN);
    }
}
