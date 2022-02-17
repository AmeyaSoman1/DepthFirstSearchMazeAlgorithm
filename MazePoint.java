package mazegame;

public class MazePoint implements Comparable<MazePoint> {

	// define point co-ordinates x and y
	private int xPoint;
	private int yPoint;
	
	public MazePoint(int xPoint, int yPoint) {
		super();
		this.xPoint = xPoint;
		this.yPoint = yPoint;
	}

	public int getXPoint() {
		return xPoint;
	}

	public void setXPoint(int xPoint) {
		this.xPoint = xPoint;
	}

	public int getYPoint() {
		return yPoint;
	}

	public void setYPoint(int yPoint) {
		this.yPoint = yPoint;
	}
	
	public boolean isSamePoint(MazePoint otherPoint) {
		if (otherPoint.getXPoint() == getXPoint() && otherPoint.getYPoint() == getYPoint())
			return true;
		else
			return false;
	}
	
	public void copyPoint(MazePoint otherPoint) {
		this.xPoint = otherPoint.getXPoint();
		this.yPoint = otherPoint.getYPoint();
	}

	@Override
	public int compareTo(MazePoint otherPoint) {
		if (otherPoint.getXPoint() == getXPoint() && otherPoint.getYPoint() == getYPoint())
			return 0;
		else {
			
			if (otherPoint.getXPoint() < getXPoint() && otherPoint.getYPoint() == getYPoint())
				return -1;
			else if (otherPoint.getXPoint() == getXPoint() && otherPoint.getYPoint() < getYPoint())
				return -1;
			else if (otherPoint.getXPoint() > getXPoint() && otherPoint.getYPoint() == getYPoint())
				return 1;
			else 
				return 1;

		}
	}
	
	public static MazePoint copy(MazePoint point) {
		return new MazePoint(point.getXPoint(), point.getYPoint());
	}

	@Override
	public boolean equals(Object otherPoint) {
		return isSamePoint((MazePoint)otherPoint);
	}

	public String getKey() {
		return getKeyPoint(getXPoint(), getYPoint());
	}
	
	public static String getKeyPoint(int xPoint, int yPoint) {
		return "key-" + xPoint + "-" + yPoint;
	}
}
