package ca.alexcomeau.texmobile.blocks;

import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;

public abstract class Block implements Parcelable {
    protected int rotation;
    protected Point position;
    protected byte blockID;
    // Nested arrays should be based on a 4x4 grid. Points need to be in descending order based on y.
    protected Point[][] rotations;

    // Block identifiers
    public static byte I = 1;
    public static byte J = 2;
    public static byte L = 3;
    public static byte O = 4;
    public static byte S = 5;
    public static byte T = 6;
    public static byte Z = 7;

    public Block(Point start, Point[][] rotate, byte color)
    {
        rotation = 0;
        position = new Point(start);
        blockID = color;
        rotations = rotate;
    }

    public void moveUp() { position.y++; }
    public void moveDown() { position.y--; }
    public void moveLeft() { position.x--; }
    public void moveRight() { position.x++; }

    // Loops around the rotations clockwise
    public void rotateRight()
    {
        rotation++;
        if(rotation == rotations.length)
            rotation = 0;
    }

    // Loops around the rotations counterclockwise
    public void rotateLeft()
    {
        rotation--;
        if(rotation == -1)
            rotation = rotations.length - 1;
    }

    // Adds the position to the current relative coordinates and returns that.
    public Point[] getAbsoluteCoordinates()
    {
        Point[] relative = getRelativeCoordinates();
        Point[] coords = new Point[relative.length];

        for(int i = 0; i < relative.length; i++)
            coords[i] = new Point(relative[i].x + position.x, relative[i].y + position.y);

        return coords;
    }

    public byte getBlockID() { return blockID; }
    public Point getPosition() { return position; }
    public Point[] getRelativeCoordinates() { return rotations[rotation]; }

    // ===== Parcelable Stuff ====================================================
    protected Block(Parcel in) {
        rotation = in.readInt();
        position = (Point) in.readValue(Point.class.getClassLoader());
        blockID = in.readByte();
        int length = in.readInt();
        rotations = new Point[length][4];

        for(int i = 0; i < length; i++)
            rotations[i] = (Point[]) in.readArray(Point.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(rotation);
        dest.writeValue(position);
        dest.writeByte(blockID);
        dest.writeInt(rotations.length);
        for(Point[] p : rotations)
            dest.writeArray(p);
    }
}