package ca.alexcomeau.texmobile;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import ca.alexcomeau.texmobile.blocks.Block;


public class Board implements Parcelable {
    private int[][] stack;

    public Board()
    {
        // [Y][X] -- makes things a lot easier
        stack = new int[22][10];

        // Initialize all the colors to be black
        for(int i = 0; i < 22; i++)
            for(int j = 0; j < 10; j++)
                stack[i][j] = Color.BLACK;
    }

    // Checks whether the block is currently in a legal position.
    public boolean checkBlock(Block block)
    {
        Point[] coords = block.getAbsoluteCoordinates();

        for(Point c : coords)
        {
            if(c.y < 0 || c.x < 0 || c.x >= stack[0].length)
                return false;
            if (stack[c.y][c.x] != Color.BLACK)
                return false;
        }
        return true;
    }

    // Returns whether there is room for the given piece to move left
    public boolean checkLeft(Block block)
    {
        // Check if it would fit to the left, then move it back.
        block.moveLeft();
        boolean result = checkBlock(block);
        block.moveRight();
        return result;
    }

    // Returns whether there is room for the given piece to move right
    public boolean checkRight(Block block)
    {
        // Check if it would fit to the right, then move it back.
        block.moveRight();
        boolean result = checkBlock(block);
        block.moveLeft();
        return result;
    }

    // Returns whether there is room for the given piece to move down
    public boolean checkDown(Block block)
    {
        // Check if it would fit below, then move it back.
        block.moveDown();
        boolean result = checkBlock(block);
        block.moveUp();
        return result;
    }

    // Returns whether there is room for the given piece to move down
    public boolean checkRotateRight(Block block)
    {
        // Check if it would fit rotated, then put it back.
        block.rotateRight();
        boolean result = checkBlock(block);
        block.rotateLeft();
        return result;
    }

    // Returns whether there is room for the given piece to move down
    public boolean checkRotateLeft(Block block)
    {
        // Check if it would fit rotated, then put it back.
        block.rotateLeft();
        boolean result = checkBlock(block);
        block.rotateRight();
        return result;
    }

    // Returns whether the specified line is filled, and thus can be cleared.
    public boolean checkLine(int line)
    {
        Log.d("erzz", "checking a line!");
        for(int i : stack[line])
        {
            // Check each cell in the specified row. If black is found, that means there is an empty cell.
            if (i == Color.BLACK)
                return false;
        }
        return true;
    }

    // Clears all the blocks from the specified line and up for a number of lines equal to num.
    public void clearLines(int line, int num)
    {
        Log.d("erzz", "clearing a line!");
        // Set the cells to black.
        for(int i = 0; i < num; i++)
            for(int j = 0; j < 10; j++)
                stack[line + i][j] = Color.BLACK;

        // Lower all the other lines by copying the lines above them
        //System.arraycopy(stack, line + 1, stack, line, stack.length - line - 1);

        // The top row will always be all black, blocks cannot be placed there
    }

    // Clear a single line
    public void clearLine(int line) { clearLines(line, 1); }

    // Saves a block to the stack.
    public void lockBlock(Block block)
    {
        Log.d("erzz", "locking a block!");
        // Color in the places the block is over
        for(Point c : block.getAbsoluteCoordinates())
            stack[c.y][c.x] = block.getBlockColor();
    }

    public int[][] getStack() { return stack; }

    public boolean equals(Board other) { return stack.equals(other.getStack()); }

    // ===== Parcelable Stuff ============================
    protected Board(Parcel in) {
        stack = (int[][]) in.readSerializable();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(stack);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Board> CREATOR = new Parcelable.Creator<Board>() {
        @Override
        public Board createFromParcel(Parcel in) {
            return new Board(in);
        }

        @Override
        public Board[] newArray(int size) {
            return new Board[size];
        }
    };
}
