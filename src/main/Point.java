package main;
import processing.core.PApplet;

/**
 * Public class for an x y point
 */
public class Point {
  public float x = 0, y = 0;

  public Point(Point aPoint) {
    this.x = aPoint.x;
    this.y = aPoint.y;
  }
  public Point(float x, float y) {
    this.x = x;
    this.y = y;
  }
  public Point() {
  }
  public boolean matches(Point aPoint) {
    if (aPoint.x == this.x && aPoint.y == this.y) return true;
    return false;
  }
  public Point clone(){
    return new Point(this.x, this.y);
  }
  public String toString(){
    return (this.x + " " + this.y);
  }
  

public static float distance(Point point1, Point point2) {
  //float disx = (point1.x - point2.x);
  //float disy = point1.y - point2.y;
  //return sqrt(disx * disx + disy * disy);
  return PApplet.dist(point1.x, point1.y, point2.x, point2.y);
  
}
public static void line(PApplet p, Point firstPoint, Point secondPoint) {
  p.line(firstPoint.x, firstPoint.y, secondPoint.x, secondPoint.y);
}
public static void point(PApplet p, Point aPoint) {
  p.point(aPoint.x, aPoint.y);
}
public static void line(PApplet p, float x, float y, Point secondPoint) {
  p.line(x, y, secondPoint.x, secondPoint.y);
}
}
