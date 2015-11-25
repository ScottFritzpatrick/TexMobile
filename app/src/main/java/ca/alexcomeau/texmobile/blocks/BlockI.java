package ca.alexcomeau.texmobile.blocks;

import android.graphics.Color;
import android.graphics.Point;

public class BlockI extends Block {
    public BlockI(Point start)
    {
        super(start,
              new Point[][]{
                      {new Point(0,2),new Point(1,2),new Point(2,2),new Point(3,2)},
                      {new Point(2,0),new Point(2,1),new Point(2,2),new Point(2,3)}
              },
              Color.RED);
    }
}
