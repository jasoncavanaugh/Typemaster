package typemaster;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JFrame;

/* This is the "base" of the program. It contains the mainFrame, which is where
 * we will place all the different menus. It also has references to instances
 * of all the possible menus within the game. The most important method in
 * the Base class is the runAgain() method. When runAgain() is called, all the contents
 * of the current menu are removed from the main frame and are replaced by
 * the new menu (as determined by the mode field). */
public class Base {
	/* This is the main frame of the typing program. We will be displaying
	 * all the different kinds of menus on this frame.  */
	JFrame mainFrame; 

	/* The mode basically is just variable that tells us which menu to display 
	 * and also allows us to switch between the different menus. */
	Menus mode;

	/* Here are all of our menus. */
	MainMenu mainMenu;
	PlayMenu playMenu;
	YourTextsMenu yourTextsMenu;
	ProfileMenu profileMenu;
	OptionsMenu optionsMenu;
	PlayScreen playScreen;
	AddCustomText addCustomText;

	/* Here is the path to our userData directory. This directory is going to
	   store all the stats that are particular to the user (custom texts, typing
	   speed, etc.) */
	Path userDataDir;

	/* Here is the fileName of whatever file we are trying to work with. */
	String fileName, newFileName;

	public Base() {
		/* First we need to set up the mainFrame of the program. Scroll to the 
		 * bottom of the file in order to see this method's declaration. */
		setUpMainFrame();
		
		/* When we start the game, we want to start out by displaying the 
		 * main menu. So we'll set the mode to that first. */
		mode = Menus.MainMenu;
		
		/* If the userData directory has not yet been created (meaning that
		 * this is the first time the program is being played on this machine),
		 * then we need to create it. Scroll to the bottom of this file to
		 * see this method's declaration. */
		createUserDataDirectory();
		
		/* Making all the different menu objects. You'll notice that every
		 * menu object has a reference to "this". That is because in order
		 * to switch between menus, the mode field of this class must be changed
		 * and then the runAgain() method must be called. In order to do this, 
		 * all the methods need to have a reference to "this".*/
		mainMenu = new MainMenu(this);
		addCustomText = new AddCustomText(this);
		yourTextsMenu = new YourTextsMenu(this, addCustomText);
		playMenu = new PlayMenu(this, yourTextsMenu);
		playScreen = new PlayScreen(this, playMenu);
		profileMenu = new ProfileMenu();
		optionsMenu = new OptionsMenu(this, playScreen);

		/* And we run for the first time, displaying all the contents of the 
		 * main menu. */
		runAgain();
	}


	public void clearContents() {
		mainFrame.getContentPane().removeAll();
		mainFrame.getContentPane().repaint();
		mainFrame.getContentPane().revalidate();
	}
	
	public void runAgain() {
		/* First, we clear out the current menus contents */
		clearContents();
		mainFrame.getContentPane().setVisible(false);

		switch(mode) {
		case MainMenu:
			mainMenu.displayContent();
			break;
		case PlayMenu: 
			playMenu.displayContent();
			break;
		case YourTextsMenu: 
			yourTextsMenu.displayContent();
			break;
		case ProfileMenu: 
			profileMenu.displayContent();
			break;
		case OptionsMenu: 
			optionsMenu.displayContent();
			break;
		case PlayScreen: 
			playScreen.displayContent();
			break;
		case AddCustomText:
			addCustomText.displayContent();
			break;
		default: 
			break;
		}

		mainFrame.getContentPane().setVisible(true);
	}
	
	private void setUpMainFrame() {
		mainFrame = new JFrame("TypeMaster");	
		/* We'll use GridBagLayout for the mainFrame because it's very flexible.*/
		mainFrame.setLayout(new GridBagLayout());
		/* We want the user to be able to close the program by pressing the exit button. */ 
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		/* Add a nice purple color for the background */
		mainFrame.getContentPane().setBackground(new Color(200, 180, 255));
		mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		mainFrame.setUndecorated(true);
		//mainFrame.setSize(new Dimension(400, 800));
		mainFrame.setVisible(true);
	}
	
	private void createUserDataDirectory() {
		/*We are going to try to create the userData directory when we start the
	   	  game. If the directory already exists, that shouldn't be a problem 
		  since we are using createDirectories(), which does not throw an 
		  exception if the directory already exists.*/ 
		userDataDir = Paths.get("./userData");
		try {
			Files.createDirectories(userDataDir);
		} catch (IOException e) {
			System.err.println(e);
		}
	}
}

