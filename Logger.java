package mazegame;

public class Logger {

	private static boolean debugMsg = false;

	public static void debugLog (String msg) {
		if (debugMsg)
			System.out.println(msg);
	}

	public static void infoLog (String msg) {
		System.out.println(msg);
	}

}

