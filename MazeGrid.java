package mazegame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Stack;

import javax.swing.JPanel;
import javax.swing.Timer;

import fileReader.FileReader;

public class MazeGrid extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	// define colors
	private static final int BLACK = 1;
	private static final int WHITE = 0;
	private static final int GREEN = 9;
	private static final int GRAY = 2;

	/*
	private int [][] maze = 
			  // col                10
			  // 0 1 2 3 4 5 6 7 8 9 0 1
		{       {0,0,1,1,1,1,1,1,1,1,1,1}, // row 0
				{1,0,1,1,1,1,1,1,1,1,1,1}, // row 1
				{1,0,1,1,1,1,1,1,1,1,1,1}, // row 2
				{1,0,0,0,1,0,1,1,1,1,1,1}, // row 3
				{1,1,0,1,1,0,0,0,0,0,1,1}, // row 4
				{1,1,0,1,1,0,1,1,1,0,1,1}, // row 5
				{1,1,0,0,0,0,1,1,0,0,1,1}, // row 6
				{1,1,1,1,1,0,1,1,0,1,1,1}, // row 7
				{1,1,1,1,1,0,0,0,0,0,0,1}, // row 8
				{1,1,1,1,1,1,1,1,1,1,0,0}  // row 9

		};
	 */

	private int [][] maze;

	private Timer wakeUpTimer = null;

	private int curGreenRow = -1;
	private int curGreenCol = -1;

	private int preGreenColorCode = -1;

	private MazePoint greenSquarePoint;


	private int maxX = 0;
	private int maxY = 0;

	private MazePoint homePoint;

	private Stack<MazePoint> validMovesStack;
	private Map<String, MazePoint> lastGreenSquarePointMap;


	public MazeGrid() throws Exception {

		// load the pattern first
		//String fileNameWithPath = "/Users/somana/Desktop/MazePattern/Pattern04.txt";

		//randomly generate a file

		Random rand = new Random();
		int randFile = rand.nextInt(3) + 1;

		randFile = 1;

		String fileNameWithPath = "/Users/somana/Desktop/MazePattern/Pattern0" + randFile + ".txt";
		Logger.infoLog(fileNameWithPath);
		this.maze = FileReader.readMazePattern(fileNameWithPath);

		this.lastGreenSquarePointMap = new LinkedHashMap<String, MazePoint>();
		this.validMovesStack = new Stack<MazePoint>();

		int rowCount = maze.length;
		int colCount = 0;

		if (rowCount > 0) {

			colCount = maze[0].length;
		}

		setFocusable (true);
		setFocusTraversalKeysEnabled (false);

		this.maxX = colCount;
		this.maxY = rowCount;

		boolean moveValid = false;
		int xIdx = maxX - 1;
		int yIdx = maxY - 1;

		// making it so that the black keeps searching along the axis until it finds a white spot
		while (!moveValid && yIdx >= 0) {

			while (!moveValid && xIdx >= 0) {

				moveValid = isValidMove(xIdx, yIdx);

				if (!moveValid) {
					xIdx = xIdx - 1;
				}
			}

			if (!moveValid) {

				xIdx = maxX -1;
				yIdx = yIdx - 1;

			}
		}

		if (!moveValid) {
			System.out.println("ERROR: CANNOT START AT A BLACK SQUARE IN THE BOTTOM ROW AT ALL. ");
			System.exit(0);
		}

		this.greenSquarePoint = new MazePoint(xIdx, yIdx);

		relocateGreen(greenSquarePoint.getXPoint(), greenSquarePoint.getYPoint());
		
		
		if (moveValid && yIdx == 0) {
			
			System.out.println("we are home");
		}

		this.homePoint = new MazePoint (0, 0);

	}

	public void paint(Graphics g) { 
		super.paint(g);

		drawMazePaint(g);

		// start the refresh timer
		if (wakeUpTimer == null) {
			wakeUpTimer = new Timer(1000, this);
			wakeUpTimer.start();
		}

	}

	private void relocateGreen(int nextGreenX, int nextGreenY) {

		// Logger.infoLog("next x, y move:" + greenSquareX + "," + greenSquareY);

		int nextGreenRow = nextGreenY;
		int nextGreenCol = nextGreenX;

		if (preGreenColorCode >= 0) {
			maze[curGreenRow][curGreenCol] = preGreenColorCode;
		}

		preGreenColorCode = maze[nextGreenRow][nextGreenCol];
		maze[nextGreenRow][nextGreenCol] = GREEN;
		this.curGreenRow = nextGreenRow;
		this.curGreenCol = nextGreenCol;
	}

	private void drawMazePaint(Graphics g) {
		// draw the maze
		for (int row = 0; row < maze.length; row++) {
			// Logger.log("got row:" + row);

			// if (maxY < 0) maxY = maze.length;

			for (int col = 0; col < maze[row].length; col++) {

				// if (maxX < 0) maxX = maze[row].length;

				int pointColorValue = maze[row][col];

				// prepare key for last visit map lookup
				String visitKey = MazePoint.getKeyPoint(col, row);
				if (lastGreenSquarePointMap.containsKey(visitKey))
					pointColorValue = GRAY;

				// Logger.log("drawing row:" + row + "; col:" + col);
				Color color;
				switch (pointColorValue) {
				case BLACK : color = Color.BLACK; break;
				case WHITE : color = Color.WHITE; break;
				case GREEN : color = Color.GREEN; break;
				case GRAY : color = Color.GRAY; break;
				default : color = Color.BLACK;
				}

				drawLocation(g, row, col, color);

			}
		}

	}

	public void drawLocation(Graphics g, int row, int col, Color color) {
		g.setColor(color);
		g.fillRect(30 * col, 30 * row, 30, 30);
		g.setColor(Color.BLACK);
		g.drawRect(30 * col, 30 * row, 30, 30);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// this is the method called after every few seconds by the wakeUpTimer
		if (e.getSource() == wakeUpTimer) {

			prepareNextMove();
			areWeHomeYet();

			this.repaint();
		}
	}

	private void areWeHomeYet() {
		// check if we are home
		if (homePoint.isSamePoint(greenSquarePoint)) {
			Logger.infoLog("We are home, stop the spinning");
			wakeUpTimer.stop();
		}
	}

	private void prepareNextMove() {

		// let's explore X move possibility
		findAllValidXMoves();

		// let's explore Y move since X was not good
		findAllValidYMoves();

		// can we move?
		if (validMovesStack.isEmpty()) {
			Logger.infoLog("We are blocked, no point spinning");
			wakeUpTimer.stop();
		}
		else {
			recordCurrentCoordinates();
			greenSquarePoint = validMovesStack.pop();
			Logger.infoLog("Moving to next point x:" + greenSquarePoint.getXPoint() + "; y:" + greenSquarePoint.getYPoint());
		}

		relocateGreen(greenSquarePoint.getXPoint(), greenSquarePoint.getYPoint());

	}

	private void findAllValidXMoves() {

		// let's try a right move
		int nextGreenSquareX = greenSquarePoint.getXPoint() + 1;
		boolean moveValid = isValidMove(nextGreenSquareX, greenSquarePoint.getYPoint());
		if (moveValid) {
			storeValidMove(nextGreenSquareX, greenSquarePoint.getYPoint());
		}

		// let's try a left move also
		nextGreenSquareX = greenSquarePoint.getXPoint() - 1;
		moveValid = isValidMove(nextGreenSquareX, greenSquarePoint.getYPoint());
		if (moveValid) {
			storeValidMove(nextGreenSquareX, greenSquarePoint.getYPoint());
		}		

	}

	private void storeValidMove(int nextGreenSquareX, int nextGreenSquareY) {
		// store it for future use
		MazePoint anotherValidPoint = new MazePoint(nextGreenSquareX, nextGreenSquareY);
		validMovesStack.push(anotherValidPoint);
	}

	private boolean isValidMove(int selectedGreenSquareX, int selectedGreenSquareY) {
		boolean moveSuccessful = false;
		Logger.debugLog("maxX:" + maxX + "; nextGreenSquareX:" + selectedGreenSquareX + "; maxY:" + maxY + "; nextGreenSquareY:" + selectedGreenSquareY);
		if (selectedGreenSquareX > -1 && selectedGreenSquareX < maxX &&
				selectedGreenSquareY > -1 && selectedGreenSquareY < maxY) {
			// let's check the color at the destination move
			int destColor = getColorForLocationXY(selectedGreenSquareX, selectedGreenSquareY);

			/*if (maxX - 1 == BLACK && maxY - 1 == BLACK) {

				Logger.infoLog("ERROR: CANNOT START AT A BLACK SQUARE.");
				System.exit(0);

				moveSuccessful = false;
				return moveSuccessful;
			}*/

			if (destColor == WHITE) {
				if (!isMoveBackward(selectedGreenSquareX, selectedGreenSquareY)) {
					// recordCurrentCoordinates();
					// greenSquarePoint.setXPoint(selectedGreenSquareX);
					// greenSquarePoint.setYPoint(selectedGreenSquareY);
					moveSuccessful = true;
				}
				else {
					Logger.debugLog("skipped move, cannot move back");
				}
			}
			else
				Logger.debugLog("skipped move not white to:" + selectedGreenSquareX + ", " + selectedGreenSquareY);
		}
		else
			Logger.debugLog("skipped X move index too high to:" + selectedGreenSquareX + ", " + selectedGreenSquareY);
		return moveSuccessful;
	}

	private int getColorForLocationXY(int selectedGreenSquareX, int selectedGreenSquareY) {
		return maze[selectedGreenSquareY][selectedGreenSquareX];
	}

	private void findAllValidYMoves() {

		// let's try a down move
		int nextGreenSquareY = greenSquarePoint.getYPoint() + 1;
		boolean moveValid = isValidMove(greenSquarePoint.getXPoint(), nextGreenSquareY);
		if (moveValid) {
			storeValidMove(greenSquarePoint.getXPoint(), nextGreenSquareY);
		}

		// let's try a up move
		nextGreenSquareY = greenSquarePoint.getYPoint() - 1;
		moveValid = isValidMove(greenSquarePoint.getXPoint(), nextGreenSquareY);
		if (moveValid) {
			storeValidMove(greenSquarePoint.getXPoint(), nextGreenSquareY);
		}

	}

	private void recordCurrentCoordinates() {
		MazePoint nextMazePoint = MazePoint.copy(greenSquarePoint);
		lastGreenSquarePointMap.put(nextMazePoint.getKey(), nextMazePoint);
		Logger.debugLog("Added a new value to last visit map");
	}
	private boolean isMoveBackward(int selectedGreenSquareX, int selectedGreenSquareY) {
		return lastGreenSquarePointMap.containsKey(MazePoint.getKeyPoint(selectedGreenSquareX, selectedGreenSquareY));
	}



}

