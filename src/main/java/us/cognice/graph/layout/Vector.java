package us.cognice.graph.layout;

/**
 * Created by Kirill Simonov on 14.06.2017.
 */
public class Vector {

    private double x, y;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector add(Vector point){
        x += point.x;
        y += point.y;
        return this;
    }

    public Vector subtract(Vector point) {
        x -= point.x;
        y -= point.y;
        return this;
    }

    public Vector unit() {
        if (x == 0 && y == 0) {
            x = 0.00001;
            y = 0.00001;
            return this;
        }
        double l = length();
        x /= l;
        y /= l;
        return this;
    }

    public Vector scale(double k) {
        x *= k;
        y *= k;
        return this;
    }

    public Vector limit(double max) {
        double l = length();
        return l < max ? this : unit().scale(max);
    }

    public double length() {
        return Math.sqrt(x * x + y * y);
    }
}