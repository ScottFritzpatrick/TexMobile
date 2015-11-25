package ca.alexcomeau.texmobile.blocks;

import android.graphics.Point;
import android.util.Log;

import java.io.Serializable;

public abstract class Block implements Serializable {
    protected int rotation;
    protected Point position;
    protected int blockColor;
    // Nested arrays should be based on a 4x4 grid. Order doesn't really matter.
    protected Point[][] rotations;

    public Block(Point start, Point[][] rotate, int color)
    {
        rotation = 0;
        position = start;
        blockColor = color;
        rotations = rotate;
    }

    public void moveUp() { position.y++; }
    public void moveDown() { position.y--; }
    public void moveLeft() { position.x--; }
    public void moveRight() { position.x++; }

    // Loops around the rotations clockwise
    public void rotateRight() { if(rotation++ == rotations.length) rotation = 0; }

    // Loops around the rotations counterclockwise
    public void rotateLeft() { if(rotation-- == -1) rotation = rotations.length - 1; }

    // Adds the position to the current relative coordinates and returns that.
    public Point[] getAbsoluteCoordinates()
    {
        Point[] relative = getRelativeCoordinates();
        Point[] coords = new Point[relative.length];

        for(int i = 0; i < relative.length; i++)
            coords[i] = new Point(relative[i].x + position.x, relative[i].y + position.y);

        return coords;
    }

    public int getBlockColor() { return blockColor; }
    public Point getPosition() { return position; }
    public Point[] getRelativeCoordinates() { return rotations[rotation]; }
}
