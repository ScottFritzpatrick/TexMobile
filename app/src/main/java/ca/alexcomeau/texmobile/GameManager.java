package ca.alexcomeau.texmobile;

import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.LinkedList;
import ca.alexcomeau.texmobile.blocks.*;

public class GameManager implements Parcelable {
    private Block currentBlock;
    private Block nextBlock;
    private Block lastBlock;
    private Board gameBoard;
    private int level;
    private int score;
    private int lockWait;
    private int droppedLines;
    private int combo;
    private int spawnWait;
    private int fallWait;
    private int autoShiftWait;
    private int lineClearWait;
    private int elapsedFrames;
    private int soundEffectToPlay;
    private String lastInput;
    private LinkedList<Integer> history;
    private boolean grandmasterValid;
    private boolean check1;
    private boolean check2;
    private boolean check3;
    private boolean pieceRedraw;
    private boolean stackRedraw;
    private Boolean gameOver;

    // Pieces drop every gravity frames, if gravity > 0.
    private int gravity;
    // Pieces drop superGravity rows per frame, if gravity == 0.
    private int superGravity;
    // When the game ends
    private int maxLevel;

    // Blocks lock in place LOCK_DELAY frames after touching the stack.
    private final int LOCK_DELAY = 15;
    // Where pieces spawn
    private final Point START = new Point(3,17);
    // Pieces are spawned SPAWN_DELAY frames after a piece is locked.
    private final int SPAWN_DELAY = 15;
    // Frames to wait before allowing consecutive duplicate inputs
    private final int AUTO_SHIFT_DELAY = 7;
    // Frames to wait after a line is cleared before doing anything else
    private final int LINE_CLEAR_DELAY = 21;

    public GameManager(){ }

    // Start the game
    public void start(int levelStart, int levelEnd)
    {
        // Initialize
        gameBoard = new Board();
        score = 0;
        level = 0;
        maxLevel = levelEnd;
        addLevel(levelStart);
        combo = 1;
        gameOver = null;
        pieceRedraw = true;
        stackRedraw = false;
        elapsedFrames = 0;
        spawnWait = 0;
        lockWait = LOCK_DELAY;
        autoShiftWait = 0;
        fallWait = 0;
        lineClearWait = LINE_CLEAR_DELAY;
        lastInput = "";

        // Start the history full of Zs.
        history = new LinkedList<>();
        history.add(6);
        history.add(6);
        history.add(6);
        history.add(6);

        // Don't generate an O, S, or Z as the first piece
        currentBlock = generateNewBlock((int)(Math.random() * 4));
        nextBlock = generateNewBlock();

        // If they're doing a full game they can attain grandmaster rank
        grandmasterValid = (maxLevel == 999 && levelStart == 0);
        check1 = grandmasterValid;
        check2 = grandmasterValid;
        check3 = grandmasterValid;
    }

    // Move ahead a frame
    public void advanceFrame(String input)
    {
        pieceRedraw = false;
        stackRedraw = false;
        soundEffectToPlay = -1;
        elapsedFrames++;

        if(lineClearWait < LINE_CLEAR_DELAY)
            lineClearWait ++;
        else
        {
            // If a block was placed last frame, swap it for the next one and generate a new next.
            if(currentBlock == null)
            {
                if(spawnWait++ >= SPAWN_DELAY)
                {
                    currentBlock = nextBlock;
                    nextBlock = generateNewBlock();
                    pieceRedraw = true;

                    // If the new block isn't in a valid location, the game is lost
                    if(!gameBoard.checkBlock(currentBlock))
                        gameOver = false;

                    spawnWait = 0;
                    fallWait = 0;

                    // A new block appearing increases the level by one, unless the level ends in 99 or is the second last
                    if(!((level + 1) % 100 == 0 || level == maxLevel - 1))
                        addLevel(1);
                }
            }
            else
            {
                droppedLines = 1;

                // Check if the received input is the same as last frame
                if(input.equals(lastInput))
                {
                    // If so, wait some frames before accepting it again to avoid unintentional doubled inputs
                    if(autoShiftWait < AUTO_SHIFT_DELAY && !input.equals(""))
                    {
                        autoShiftWait++;
                        input = "";
                    }
                }
                else
                {
                    lastInput = input;
                    autoShiftWait = 0;
                }

                switch(input)
                {
                    case "drop":
                        drop();
                        break;
                    case "left":
                        moveLeft();
                        break;
                    case "right":
                        moveRight();
                        break;
                    case "rotateLeft":
                        rotateLeft();
                        break;
                    case "rotateRight":
                        rotateRight();
                        break;
                    case "down":
                        fallWait = gravity;
                        break;
                    default:
                        break;
                }

                // Check if the block is currently in a state of falling
                if(gameBoard.checkDown(currentBlock))
                {
                    lockWait = 0;
                    // Move the block down if enough time has passed
                    if(gravity > 0)
                    {
                        if(fallWait++ >= gravity)
                        {
                            fallWait = 0;
                            currentBlock.moveDown();
                            pieceRedraw = true;
                        }
                    }
                    else
                        for(int i = 0; i < superGravity; i++)
                            if(gameBoard.checkDown(currentBlock))
                            {
                                currentBlock.moveDown();
                                pieceRedraw = true;
                            }
                }
                else
                {
                    // Check if the block needs to be locked
                    if(lockWait++ >= LOCK_DELAY)
                    {
                        gameBoard.lockBlock(currentBlock);
                        lockWait = 0;
                        soundEffectToPlay = 0;
                        // Check if locking that piece caused any lines to be cleared
                        checkClears();
                        lastBlock = currentBlock;
                        currentBlock = null;
                    }
                }
            }
        }
    }

    // ===== Input handling methods ==========================================
    private void drop()
    {
        // Count the number of lines the piece falls
        while(gameBoard.checkDown(currentBlock))
        {
            currentBlock.moveDown();
            droppedLines++;
            pieceRedraw = true;
        }

        lockWait = LOCK_DELAY;
    }

    private void moveLeft()
    {
        if(gameBoard.checkLeft(currentBlock))
        {
            currentBlock.moveLeft();
            pieceRedraw = true;
        }
    }

    private void moveRight() {
        if (gameBoard.checkRight(currentBlock))
        {
            currentBlock.moveRight();
            pieceRedraw = true;
        }
    }

    private void rotateLeft()
    {
        if(gameBoard.checkRotateLeft(currentBlock))
        {
            currentBlock.rotateLeft();
            pieceRedraw = true;
        }
        else
        {
            // See if the rotation would be valid if the block was tapped to the side (wall kick)
            currentBlock.moveRight();

            if(gameBoard.checkRotateLeft(currentBlock))
            {
                currentBlock.rotateLeft();
                pieceRedraw = true;
            }

            // Undo the move right if it still didn't work
            else
                currentBlock.moveLeft();
        }
    }

    private void rotateRight()
    {
        if(gameBoard.checkRotateRight(currentBlock))
        {
            currentBlock.rotateRight();
            pieceRedraw = true;
        }
        else
        {
            // See if the rotation would be valid if the block was tapped to the side (wall kick)
            currentBlock.moveLeft();

            if(gameBoard.checkRotateRight(currentBlock))
            {
                currentBlock.rotateRight();
                pieceRedraw = true;
            }
                // Undo the move left if it still didn't work
            else
                currentBlock.moveRight();
        }
    }
    // ===== End input handling =============================================

    // Checks for clears, clears them if so, and adds to the score and level
    private void checkClears()
    {
        int linesCleared = 0;

        // Get all the unique rows the block is spanning
        ArrayList<Integer> toCheck = new ArrayList<>();
        for(Point c : currentBlock.getAbsoluteCoordinates())
            if (!toCheck.contains(c.y))
                toCheck.add(c.y);

        // Check each of those rows
        for(Integer i : toCheck)
            if (gameBoard.checkLine(i))
            {
                gameBoard.clearLine(i);
                linesCleared++;
            }

        if(linesCleared > 0)
        {
            combo += (linesCleared * 2) - 2;

            // Bonus for clearing the whole screen
            int bravo = gameBoard.equals(new Board()) ? 4 : 1;

            // Tetris: The Grand Master scoring method
            score += (Math.ceil((level + linesCleared) / 4) + droppedLines)
                    * linesCleared * ((linesCleared * 2) - 1)
                    * combo * bravo;

            addLevel(linesCleared);

            // The game ends once the max level is reached.
            if(level >= maxLevel)
            {
                level = maxLevel;
                if(check3)
                {
                    // Final check. Score >= 126000, Time <= 13m30s. 1 frame per 34 ms.
                    if(score < 126000 && elapsedFrames * 34 > 810000)
                        grandmasterValid = false;
                    check3 = false;
                }
                gameOver = true;
            }

            stackRedraw = true;
            soundEffectToPlay = 1;
            lineClearWait = 0;
        }
        else
            combo = 1;
    }

    // Generates a block of a random type.
    private Block generateNewBlock(int i)
    {
        // Take out the oldest element of history and add in this one
        history.remove();
        history.add(i);

        switch(i)
        {
            case 0:
                return new BlockI(START);
            case 1:
                return new BlockJ(START);
            case 2:
                return new BlockL(START);
            case 3:
                return new BlockT(START);
            case 4:
                return new BlockS(START);
            case 5:
                return new BlockO(START);
            default:
                return new BlockZ(START);
        }
    }

    private Block generateNewBlock()
    {
        int i = (int)(Math.random() * 7);
        int j = 1;

        // Generate a new number until there's one that's not in the history, or 4 attempts
        while(history.contains(i) && j < 5)
        {
            i = (int)(Math.random() * 7);
            j++;
        }

        return generateNewBlock(i);
    }

    private void addLevel(int toAdd)
    {
        level += toAdd;

        // Gravity changes depending on level
        if(level < 30)
            gravity = 32;
        else if(level < 35)
            gravity = 21;
        else if(level < 40)
            gravity = 16;
        else if(level < 50)
            gravity = 13;
        else if(level < 60)
            gravity = 10;
        else if(level < 70)
            gravity = 8;
        else if(level < 80)
            gravity = 4;
        else if(level < 140)
            gravity = 3;
        else if(level < 170)
            gravity = 2;
        else if(level < 200)
            gravity = 32;
        else if(level < 220)
            gravity = 4;
        else if(level < 230)
            gravity = 2;
        else if(level < 300)
            gravity = 1;
        else if(level < 330)
        {
            // Here, the pieces start dropping multiple rows per frame.
            gravity = 0;
            superGravity = 2;

            // This is one of the checkpoints for grandmaster rank. Score >= 12000, Time <= 4m15s
            if(check1)
            {
                if (score < 12000 || elapsedFrames * 34 > 255000)
                {
                    grandmasterValid = false;
                    // No need to do the other checks if one fails.
                    check2 = false;
                    check3 = false;
                }
                check1 = false;
            }
        }
        else if(level < 360)
            superGravity = 3;
        else if(level < 400)
            superGravity = 4;
        else if(level < 420)
            superGravity = 5;
        else if(level < 450)
            superGravity = 4;
        else if(level < 500)
            superGravity = 3;
        else if(level < 999)
        {
            superGravity = 20;

            // Another checkpoint. Score >= 40000, Time <= 7m30s
            if(check2)
            {
                if (score < 40000 || elapsedFrames * 34 > 450000)
                {
                    grandmasterValid = false;
                    // No need to do the other checks if one fails.
                    check3 = false;
                }
                check2 = false;
            }
        }
    }

    public String getGrade()
    {
        if(score < 400)
            return "9";
        else if(score < 800)
            return "8";
        else if(score < 1400)
            return "7";
        else if(score < 2000)
            return "6";
        else if(score < 3500)
            return "5";
        else if(score < 5500)
            return "4";
        else if(score < 8000)
            return "3";
        else if(score < 12000)
            return "2";
        else if(score < 16000)
            return "1";
        else if(score < 22000)
            return "S1";
        else if(score < 30000)
            return "S2";
        else if(score < 40000)
            return "S3";
        else if(score < 52000)
            return "S4";
        else if(score < 66000)
            return "S5";
        else if(score < 82000)
            return "S6";
        else if(score < 100000)
            return "S7";
        else if(score < 120000)
            return "S8";
        else if(grandmasterValid && score >= 126000)
            return "GM";
        else
            return "S9";
    }

    public byte[][] getStack() { return gameBoard.getStack(); }
    public int getLevel() { return level; }
    public int getMaxLevel() { return maxLevel; }
    public int getScore() { return score; }
    public int getFrames() { return elapsedFrames; }
    public int getSoundEffectToPlay() { return soundEffectToPlay; }
    public Boolean getGameOver() { return gameOver; }
    public Block getCurrentBlock() { return currentBlock; }
    public boolean getStackRedraw() { return stackRedraw; }
    public boolean getPieceRedraw() { return pieceRedraw; }
    public Block getLastBlock() { return lastBlock; }
    public void clearLastBlock() { lastBlock = null; }

    // ===== Parcelable Stuff ============================================
    protected GameManager(Parcel in) {
        currentBlock = in.readParcelable(Block.class.getClassLoader());
        nextBlock = in.readParcelable(Block.class.getClassLoader());
        gameBoard = in.readParcelable(Board.class.getClassLoader());
        level = in.readInt();
        score = in.readInt();
        lockWait = in.readInt();
        droppedLines = in.readInt();
        combo = in.readInt();
        spawnWait = in.readInt();
        fallWait = in.readInt();
        autoShiftWait = in.readInt();
        lineClearWait = in.readInt();
        elapsedFrames = in.readInt();
        if (in.readByte() == 0x01) {
            history = new LinkedList<>();
            in.readList(history, Integer.class.getClassLoader());
        } else {
            history = null;
        }
        grandmasterValid = in.readByte() != 0x00;
        check1 = in.readByte() != 0x00;
        check2 = in.readByte() != 0x00;
        check3 = in.readByte() != 0x00;
        pieceRedraw = true;
        stackRedraw = true;
        byte gameOverVal = in.readByte();
        gameOver = gameOverVal == 0x02 ? null : gameOverVal != 0x00;
        gravity = in.readInt();
        superGravity = in.readInt();
        maxLevel = in.readInt();
        soundEffectToPlay = -1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(currentBlock, 0);
        dest.writeParcelable(nextBlock, 0);
        dest.writeParcelable(gameBoard, 0);
        dest.writeInt(level);
        dest.writeInt(score);
        dest.writeInt(lockWait);
        dest.writeInt(droppedLines);
        dest.writeInt(combo);
        dest.writeInt(spawnWait);
        dest.writeInt(fallWait);
        dest.writeInt(autoShiftWait);
        dest.writeInt(lineClearWait);
        dest.writeInt(elapsedFrames);
        if (history == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(history);
        }
        dest.writeByte((byte) (grandmasterValid ? 0x01 : 0x00));
        dest.writeByte((byte) (check1 ? 0x01 : 0x00));
        dest.writeByte((byte) (check2 ? 0x01 : 0x00));
        dest.writeByte((byte) (check3 ? 0x01 : 0x00));
        if (gameOver == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (gameOver ? 0x01 : 0x00));
        }
        dest.writeInt(gravity);
        dest.writeInt(superGravity);
        dest.writeInt(maxLevel);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<GameManager> CREATOR = new Parcelable.Creator<GameManager>() {
        @Override
        public GameManager createFromParcel(Parcel in) {
            return new GameManager(in);
        }

        @Override
        public GameManager[] newArray(int size) {
            return new GameManager[size];
        }
    };
}
