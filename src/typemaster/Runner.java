package typemaster;

import javax.swing.SwingUtilities;

/* This is the runner for the program. It's only function is to create an instance
 * of the Base class.*/

public class Runner {
	public static void createAndShowGUI() {
		new Base();
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override 
			public void run() {
				createAndShowGUI();
			}
		});
	}
}
