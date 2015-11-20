package ca.alexcomeau.texmobile.blocks;

import android.graphics.Color;
import ca.alexcomeau.texmobile.Coordinate;

public class BlockJ extends Block {
    public BlockJ(Coordinate start)
    {
        super(start,
              new Coordinate[][]{
                      {new Coordinate(0,2),new Coordinate(1,2),new Coordinate(2,1),new Coordinate(2,2)},
                      {new Coordinate(1,1),new Coordinate(1,2),new Coordinate(1,3),new Coordinate(0,1)},
                      {new Coordinate(0,1),new Coordinate(1,1),new Coordinate(0,2),new Coordinate(2,1)},
                      {new Coordinate(1,1),new Coordinate(1,2),new Coordinate(1,3),new Coordinate(2,3)}
              },
              Color.BLUE);
    }
}
