package ca.alexcomeau.texmobile;

// Data class
public class Coordinate {
    public int x;
    public int y;

    public Coordinate(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public Coordinate add(Coordinate other) { return new Coordinate(x + other.x, y + other.y); }
    public boolean equals(Coordinate other) { return (x == other.x && y == other.y); }
}
