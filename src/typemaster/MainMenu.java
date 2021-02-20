package typemaster;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
/* This is the main menu for the game. When the game starts up, this is the 
 * first menu that will be loaded up. 
 * 
 * The menu will look like this
 * 
 *  		---------------------------------------------
 *  		|											|
 *			|											|
 *			|				 TYPEMASTER				    |
 *			|											|
 *			|___________________________________________|
 *  			
 * 							 | Play |
 *							
 *					     | Your Texts |
 *							
 *						   | Profile |
 *						
 *						   | Options |
 *						
 *							 | Quit |
 * 
 */
public class MainMenu implements MenuInt {
	/* We are going to put the contents of this menu directly on the 
	 * main frame. Therefore, we need to have a GridBagConstraints object. */
	GridBagConstraints gbc;

	/* As always, we will need a reference to the base in order to call 
	 * the runAgain() method. */
	Base base;

	/* The logo for the game. I made this image using a free website online. 
	 * Ideally, I would be able to make it just by using code, but
	 * I don't know how to do that, so for the time being, this is what we have. */
	JLabel title = new JLabel(new ImageIcon("images/textfx.jpg"));

	/* Our buttons */
	JButton play, yourTexts, profile, options, quit;

	public MainMenu(Base base) {
		this.base = base;
		gbc = new GridBagConstraints();

		/* Initializing our buttons */
		play = new JButton("Play");
		yourTexts = new JButton("Your Texts");
		profile = new JButton("Profile");
		options = new JButton("Options");
		quit = new JButton("Quit");
		
		/* Setting up the play button */
		play.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				base.mode = Menus.PlayMenu;
				base.runAgain();
			}
		});

		/* Setting up the yourTexts button */
		yourTexts.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				base.mode = Menus.YourTextsMenu;
				base.runAgain();
			}
		});

		/* Setting up the profile button */
		profile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				base.mode = Menus.ProfileMenu;
				base.runAgain();
			}
		});

		/* Setting up the options button */
		options.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				base.mode = Menus.OptionsMenu;
				base.runAgain();
			}
		});

		/* Setting up the quit button */
		quit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
	}
    

	@Override
	public void displayContent() {
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.weighty = 1;

		gbc.gridy = 1;
		base.mainFrame.getContentPane().add(title);
		gbc.gridy = 2;
		base.mainFrame.getContentPane().add(play, gbc);
		gbc.gridy = 3;
		base.mainFrame.getContentPane().add(yourTexts, gbc);
		gbc.gridy = 4;
		base.mainFrame.getContentPane().add(profile, gbc);
		gbc.gridy = 5;
		base.mainFrame.getContentPane().add(options, gbc);
		gbc.gridy = 6;
		base.mainFrame.getContentPane().add(quit, gbc);
	}
}
