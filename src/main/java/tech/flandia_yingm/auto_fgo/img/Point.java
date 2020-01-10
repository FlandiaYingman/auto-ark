package tech.flandia_yingm.auto_fgo.img;

import lombok.Value;
import lombok.var;

@Value
public class Point {

    private final int x, y;

    private final double weight;


    public Point(int x, int y) {
        this(x, y, Double.NaN);
    }

    public Point(int x, int y, double weight) {
        this.x = x;
        this.y = y;
        this.weight = weight;
    }

    public boolean isEmpty() {
        return x == -1 && y == -1 && Double.isNaN(weight);
    }

    public Point add(Point p) {
        return new Point(x + p.x, y + p.y);
    }

    public Point sub(Point p) {
        return new Point(x - p.x, y - p.y);
    }

    public Point multiply(int n) {
        return new Point(x * n, y * n);
    }

    public Point divide(int n) {
        return new Point(x / n, y / n);
    }


    public Point addMultipliedOffset(Point offset, int n) {
        return add(offset.multiply(n));
    }


    public static Point map(Point p,
                            int xStartMin, int xStartMax, int xEndMin, int xEndMax,
                            int yStartMin, int yStartMax, int yEndMin, int yEndMax) {
        var x = p.getX();
        var y = p.getY();
        x = (int) Math.round(((double) (x - xStartMin) / (double) (xStartMax - xStartMin)) * (double) (xEndMax - xEndMin));
        y = (int) Math.round(((double) (y - yStartMin) / (double) (yStartMax - yStartMin)) * (double) (yEndMax - yEndMin));
        return new Point(x, y);
    }

    public static Point getEmpty() {
        return new Point(-1, -1, Double.NaN);
    }

}
