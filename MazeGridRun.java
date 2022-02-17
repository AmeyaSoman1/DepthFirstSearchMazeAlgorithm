package mazegame;

import javax.swing.JFrame;

public class MazeGridRun {
	
	public static void main (String [] args) throws Exception {
		
		JFrame frame = new JFrame();
		MazeGrid game = new MazeGrid();
		frame.setTitle("Maze Game");
		frame.add(game);
		frame.setSize(361, 321);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
	}

}

