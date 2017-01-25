import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.TreeSet;

public class sweep {
	//Queue to hold given and intersecting points
	PriorityQueue<Point> queue;
	//This sweep line takes value of x-coordinate of current point. Initialized to left infinity.
	double sweepLine = Double.MIN_VALUE;
	
	public static void main(String[] args) {
		sweep obj = new sweep();
		obj.helperFunction();
	}
	
	public void helperFunction() {
		Scanner scan = null;
		try {
			scan = new Scanner(System.in);
			int numLines = scan.nextInt();
			//Create priority queue of size of total number of points
			queue = new PriorityQueue<Point>(2*numLines, new PointComparator());
			//Read points from input and add points to queue
			for (int i=0; i<numLines; i++) {
				Line line = new Line();
				double x1 = scan.nextDouble();
				double y1 = scan.nextDouble();
				double x2 = scan.nextDouble();
				double y2 = scan.nextDouble();
				double leftX, leftY, rightX, rightY; 
				//Identify left and right point from input
				if (x1 < x2) {
					leftX = x1;
					leftY = y1;
					rightX = x2;
					rightY = y2;
				} else if (x1 > x2) {
					leftX = x2;
					leftY = y2;
					rightX = x1;
					rightY = y1;
				} else if (y1 < y2) {
					leftX = x1;
					leftY = y1;
					rightX = x2;
					rightY = y2;
				} else {
					leftX = x2;
					leftY = y2;
					rightX = x1;
					rightY = y1;
				}
				 
				Point leftPoint = new Point(leftX, leftY, true, line, false);
				Point rightPoint = new Point(rightX, rightY, false, line, false);
				line.left = leftPoint;
				line.right = rightPoint;
				//Determine slope and y-intercept of given lines
				line.slope  = (rightPoint.y-leftPoint.y)/(rightPoint.x-leftPoint.x);
				line.intercept = leftPoint.y - (line.slope*leftPoint.x);
				queue.add(leftPoint);
				queue.add(rightPoint);
			}
			//Call to line sweep function
			lineSweep();
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			if (scan != null)
				scan.close();
		}
	}
	
	//This method implements line sweep algorithm.
	public void lineSweep() {
		//Balanced tree to hold list of active lines.
		TreeSet<Line> activeLines = new TreeSet<Line>(new LineComparator());
		//This array list holds list of all intersecting points.
		ArrayList<Point> result = new ArrayList<Point>();
		while (!queue.isEmpty()) {
			Point currPoint = queue.remove();
			sweepLine = currPoint.x;
			Line currLine = currPoint.line;
			Line lineAbove;
			Line lineBelow;
			//Variable to hold intersecting point.
			Point interPoint = new Point();
			//If current point is intersecting point then add it to result and swap intersecting lines.
			//Else if current point is left point of line then add it to active lines and check for intersection with neighbors
			//Else if current point is right point of line then remove it from active lines and check for intersection with below and above neighbors
			if (currPoint.isInterPoint) {
				if (!result.contains(currPoint)) {
					result.add(currPoint);
				}
				Line segE1 = currPoint.line;
				Line segE2 = currPoint.interLine;
				swap(activeLines, segE1, segE2);
				lineAbove = activeLines.higher(segE2);
				lineBelow = activeLines.lower(segE1);
				if (lineAbove != null && intersectPoint(segE2, lineAbove, interPoint)) {
					if (!result.contains(interPoint)) {
						queue.add(interPoint);
					}	
				}
				if (lineBelow != null && intersectPoint(segE1, lineBelow, interPoint)) {
					if (!result.contains(interPoint)) {
						queue.add(interPoint);
					}
				}
			} else if (currPoint.isLeft) {
				activeLines.add(currLine);
				lineAbove = activeLines.higher(currLine);
				lineBelow = activeLines.lower(currLine);
				if (lineAbove != null && intersectPoint(currLine, lineAbove, interPoint)) {
					if (!result.contains(interPoint)) {
						queue.add(interPoint);
					}
				} 
				if (lineBelow != null && intersectPoint(currLine, lineBelow, interPoint)) {
					if (!result.contains(interPoint)) {
						queue.add(interPoint);
					}
				}
			} else {
				lineAbove = activeLines.higher(currLine);
				lineBelow = activeLines.lower(currLine);
				if (lineAbove != null && lineBelow != null && intersectPoint(lineAbove, lineBelow, interPoint)) {
					if (!result.contains(interPoint)) {
						queue.add(interPoint);
					}
				}
				activeLines.remove(currLine);
			}
		}
		printResult(result);
	}
	
	//This method is used to swap intersecting lines
	public void swap (TreeSet<Line> activeLines, Line line1, Line line2) {
		activeLines.remove(line1);
		activeLines.remove(line2);
		sweepLine = queue.peek().x; 
		activeLines.add(line1);
		activeLines.add(line2);
	}
	
	//This method is used to print all the intersection points.
	public void printResult(ArrayList<Point> result) {
		Collections.sort(result, new PointComparator());
		for (int i=0; i<result.size(); i++) {
			Point currPoint = result.get(i);
			DecimalFormat df = new DecimalFormat("0.00");
			System.out.println(df.format(currPoint.x)+ " "+ df.format(currPoint.y));
		}
	}

	/**
	 * This method uses dot product to figure out if line intersects.
	 * If they intersect then it also sets the intersection point in interPoint variable.
	 */
	public boolean intersectPoint(Line line1, Line line2, Point interPoint) {
		double p0_x = line1.left.x;
		double p0_y = line1.left.y;
		double p1_x = line1.right.x;
		double p1_y = line1.right.y;
		double p2_x = line2.left.x;
		double p2_y = line2.left.y;
		double p3_x = line2.right.x;
		double p3_y = line2.right.y;
		
		double s1_x = p1_x - p0_x;     
		double s1_y = p1_y - p0_y;
		double s2_x = p3_x - p2_x;     
		double s2_y = p3_y - p2_y;

	    double s, t;
	    s = (-s1_y * (p0_x - p2_x) + s1_x * (p0_y - p2_y)) / (-s2_x * s1_y + s1_x * s2_y);
	    t = ( s2_x * (p0_y - p2_y) - s2_y * (p0_x - p2_x)) / (-s2_x * s1_y + s1_x * s2_y);

	    if (s >= 0 && s <= 1 && t >= 0 && t <= 1) {
	    	DecimalFormat df = new DecimalFormat("0.00");
	    	interPoint.x = Double.valueOf(df.format(p0_x + (t * s1_x)));
	    	interPoint.y = Double.valueOf(df.format(p0_y + (t * s1_y)));
	    	interPoint.isInterPoint = true;
	    	if (p0_y > p2_y) {
	    		interPoint.line = line1;
		    	interPoint.interLine = line2;
	    	} else {
	    		interPoint.line = line2;
		    	interPoint.interLine = line1;
	    	}
        	return true;
	    }
	    return false;
	}
	
	/**
	 * This class holds detail about points.
	 *
	 */
	static class Point {
		double x;
		double y;
		//Flag to show if given point is intersection point.
		boolean isInterPoint;
		//Flag to show if given point is left or right end point of a line.
		boolean isLeft;
		Line line;
		Line interLine;
		
		public Point(double x, double y, boolean isLeft, Line line, boolean isInterPoint) {
			super();
			this.x = x;
			this.y = y;
			this.isLeft = isLeft;
			this.line = line;
			this.isInterPoint = isInterPoint;
		}
		
		public Point() {
			
		}
		
		//Hash code to treat two objects having same x and y value as same object.
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			long temp;
			temp = Double.doubleToLongBits(x);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			temp = Double.doubleToLongBits(y);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			return result;
		}

		//Two objects having same x and y value are treated as same object.
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Point other = (Point) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}
	}
	
	/**
	 * This class holds details about line.
	 *
	 */
	class Line {
		Point left;
		Point right;
		double slope;
		double intercept;
	}
	
	/**
	 * This comparator is used to sort points left to right. 
	 * If x-coordinate is same then point having lower y-coordinate is given priority.
	 *
	 */
	class PointComparator implements Comparator<Point> {
		public int compare(Point point1, Point point2) {
			if (point1.equals(point2)) {
				return 0;
			} else if (point1.x > point2.x) {
				return 1;
			} else if (point1.x < point2.x) {
				return -1;
			} else if (point1.y > point2.y) {
				return 1;
			} else if (point1.y < point2.y) {
				return -1;
			} else {
				return 0;
			}
		}
	}
	
	/**
	 * This comparator is used to figure out position of two line in active line segments. 
	 * We use (y = slope*X + y-intercept) formula to calculate relative position of lines.
	 *
	 */
	class LineComparator implements Comparator<Line> {
		public int compare(Line line1, Line line2) {
			double y1 = line1.slope * sweepLine + line1.intercept;
			double y2 = line2.slope * sweepLine + line2.intercept;
			if (line1.equals(line2)) {
				return 0;
			} else if (y1 > y2) {
				return 1;
			} else if (y1 < y2) {
				return -1;
			} else if (line1.right.y > line2.right.y) {
				return 1;
			} else if (line1.right.y < line2.right.y) {
				return -1;
			} else {
				return 0;
			} 
		}
	}
}
