package ca.alexcomeau.texmobile.blocks;

import android.graphics.Color;
import ca.alexcomeau.texmobile.Coordinate;

public class BlockI extends Block {
    public BlockI(Coordinate start)
    {
        super(start,
              new Coordinate[][]{
                      {new Coordinate(0,2),new Coordinate(1,2),new Coordinate(2,2),new Coordinate(3,2)},
                      {new Coordinate(2,0),new Coordinate(2,1),new Coordinate(2,2),new Coordinate(2,3)}
              },
              Color.RED);
    }
}
