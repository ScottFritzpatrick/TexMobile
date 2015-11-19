package ca.alexcomeau.texmobile;

import android.graphics.Color;
import ca.alexcomeau.texmobile.blocks.Block;

public class Board {
    private int[][] stack;

    public Board()
    {
        // [Y][X] -- makes things a lot easier
        stack = new int[22][10];

        // Initialize all the colors to be black
        for(int[] c : stack)
        {
            for(int i : c)
                i = Color.BLACK;
        }
    }

    // Checks whether the block is currently in a legal position.
    public boolean checkBlock(Block block)
    {
        for(Coordinate c : block.getAbsoluteCoordinates())
        {
            if(c.y == -1 || c.x == -1 || c.x == stack[0].length)
                return false;
            if (stack[c.y][c.x] != Color.BLACK)
                return false;
        }

        return true;
    }

    // Returns whether there is room for the given piece to move left
    public boolean checkLeft(Block block)
    {
        // We can manipulate the block here without affecting the original
        block.moveLeft();
        return checkBlock(block);
    }

    // Returns whether there is room for the given piece to move right
    public boolean checkRight(Block block)
    {
        // We can manipulate the block here without affecting the original
        block.moveRight();
        return checkBlock(block);
    }

    // Returns whether there is room for the given piece to move down
    public boolean checkDown(Block block)
    {
        // We can manipulate the block here without affecting the original
        block.moveDown();
        return checkBlock(block);
    }

    // Returns whether there is room for the given piece to move down
    public boolean checkRotateRight(Block block)
    {
        // We can manipulate the block here without affecting the original
        block.rotateRight();
        return checkBlock(block);
    }

    // Returns whether there is room for the given piece to move down
    public boolean checkRotateLeft(Block block)
    {
        // We can manipulate the block here without affecting the original
        block.rotateLeft();
        return checkBlock(block);
    }

    // Returns whether the specified line is filled, and thus can be cleared.
    public boolean checkLine(int line)
    {
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
        // Set the cells to black.
        for(int i = 0; i < num; i++)
            for(int j : stack[line + i])
                j = Color.BLACK;

        // Lower all the other lines by copying the lines above them
        System.arraycopy(stack, line + 1, stack, line, stack.length - line - 1);

        // The top row will always be all black, blocks cannot be placed there
    }

    // Clear a single line
    public void clearLine(int line) { clearLines(line, 1); }

    // Saves a block to the stack.
    public void lockBlock(Block block)
    {
        // Color in the places the block is over
        for(Coordinate c : block.getAbsoluteCoordinates())
            stack[c.y][c.x] = block.getBlockColor();
    }

    public int[][] getStack() { return stack; }

    public boolean equals(Board other) { return stack.equals(other.getStack()); }
}
