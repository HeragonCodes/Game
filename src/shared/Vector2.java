package shared;

public class Vector2 {

    public int x, y;

    public static Vector2 vecEmpty = new Vector2(0, 0);
    public static Vector2 vecUp = new Vector2(0, -1);
    public static Vector2 vecDown = new Vector2(0, 1);
    public static Vector2 vecLeft = new Vector2(-1, 0);
    public static Vector2 vecRight = new Vector2(1, 0);

    public Vector2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vector2 vecAdd(Vector2 vector) {
        return new Vector2( this.x + vector.x, this.y + vector.y);
    }

    public Vector2 vecMul(int constant) {
        return new Vector2( this.x * constant, this.y * constant);
    }
}
