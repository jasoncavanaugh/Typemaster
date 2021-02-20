package typemaster;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/* This is the screen that is displayed when the user is actually playing the 
 * game. The text of the file that the user selected is loaded into a textPane
 * that is instantiated with an instance of the TypingDocument class. The user
 * then can type out the contents of the text. If at any time
 * while typing, the user decides they want to quit and go back to the 
 * PlayMenu, they can do that by pressing the back button located at the bottom
 * of the screen. When the user is finished typing the text,  a dialog box will 
 * pop up telling the user how many words per minute they typed. It will also 
 * ask them if they want to type the text again, or if they want to go back to 
 * the PlayMenu. 
 * 
 * The PlayScreen looks like this 
 * 
 * 		--------------------------------------------------------------
 * 		|text to type out 											 |
 * 		|															 |
 * 		|															 |
 * 		|															 |
 * 		|															 |
 * 		|															 |
 * 		|															 |
 * 		|															 |
 * 		|															 |
 * 		|															 |
 * 		|															 |
 * 		|															 |
 * 		|															 |
 * 		|															 |
 * 		|															 |
 * 		--------------------------------------------------------------
 *		|							Back							 | 
 * 		--------------------------------------------------------------
 */
public class PlayScreen implements MenuInt {
	/* As always, we will need a reference to the base in order to call 
	 * the runAgain() method. */
	Base base;

	/* We will need a reference to the PlayMenu in order to know what file was
	 * selected by the user in the PlayMenu. Whichever file they selected will
	 * be the file that we load up into the textPane on this screen. We can 
	 * get the file by using the playMenu.getFileToType().*/
	PlayMenu playMenu;
	

	/* This TypingDocument is a special type of document that makes the text 
	 * change color depending on how the user types. */
	TypingDocument typingDoc;
	
	/* This textPane will be instantiated with the typingDoc above. We will then
	 * load the contents of the chosen text file into this textPane. */
	JTextPane textPane;

	/* Our back button, which allows the user to go back to the PlayMenu if they
	 * decide they don't want to finish typing the text. */
	JButton back;

	/* This StringBuffer object will be used to store the text of the chosen
	 * text file. The StringBuffer's contents will then be loaded into the textPane. */
	StringBuffer stringBuffer;

	/* This double will hold the length of the text in the chosen text file. 
	 * This length will be used in calculating the WPM of the user. */
	double textLength;
	/* This boolean flag will tell us whether the user is playing in master mode
	 * or not. In master mode, one mistake will end the typing round. The purpose
	 * of master mode is 
	 */
	boolean masterMode;
	
	/* This is the panel that we will put the textPane on and the back button
	 * on. This panel will then be put directly onto the mainFrame */
	JPanel typingPanel;

	public PlayScreen(Base base, PlayMenu playMenu) {
		this.base = base;
		this.playMenu = playMenu;
		stringBuffer = new StringBuffer();
		typingDoc = new TypingDocument(this);
		
		/* Setting up the textPane */
		textPane = new JTextPane(typingDoc);
		StyleContext context = StyleContext.getDefaultStyleContext();
		textPane.setCharacterAttributes(context.addAttribute(context.getEmptySet(), 
				StyleConstants.Foreground, Color.WHITE), true);
		textPane.setFont(new Font("Monospaced", Font.ROMAN_BASELINE, 23));
		textPane.setBackground(Color.DARK_GRAY);
		textPane.setCaretColor(Color.DARK_GRAY);
		
		/* Setting up the back button */
		back = new JButton("Back");
		back.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try { 
					typingDoc.terminate();
				} catch (BadLocationException x) {
					System.err.println(x);
				}
				stringBuffer.delete(0, stringBuffer.length());
				base.mode = Menus.PlayMenu;
				base.runAgain();
			}
		});

		/* Setting up the typingPanel */
		typingPanel = new JPanel(new BorderLayout());
		typingPanel.setPreferredSize(new Dimension(800, 800));
		typingPanel.add(textPane, BorderLayout.CENTER);
		typingPanel.add(back, BorderLayout.SOUTH);
	}
	

	@Override
	public void displayContent() {
		loadText();
		
		base.mainFrame.getContentPane().add(typingPanel);

		/* Ordinarily, this step of setting the mainFrame to visible again
		 * would be taken care of directly in the base class. However,
		 * we need to do it from here in order for the textPane to be able
		 * to get the focus once the PlayScreen loads up. */
		base.mainFrame.setVisible(true);
		textPane.requestFocusInWindow();
	}
	
	/* This method loads the text of the selected file into the textPane object.
	 * The selected file can be determined by using the reference to the PlayMenu
	 * and calling getFileToType(). The file is then read using a BufferedReader
	 * and the contents are put inside the stringBuffer object. Once the reading
	 * is completed, the contents of the stringBuffer are loaded into the 
	 * textPane. */
	private void loadText() {
		Path filePath = Paths.get(playMenu.getFileToType()); 
		Charset charset = Charset.forName("US-ASCII");

		try (BufferedReader reader = Files.newBufferedReader(filePath, charset)) {
			String line;
			while ((line = reader.readLine()) != null) {
				stringBuffer.append(line);
			}
		} catch (IOException e) {
			System.err.println(e);
		}

		textPane.setText(stringBuffer.toString().trim());
		textLength = stringBuffer.length();
	}
	
	public void setMasterMode(boolean onoff) {
		masterMode = onoff;
	}
	
	public String getStringBufferContents() {
		return stringBuffer.toString();
	}

	public void showTime(long endTime, long startTime) {
		double time = (endTime - startTime)/1000.0;
		System.out.println(time);
		double timeMin = time / 60.0;
		System.out.println(timeMin);
		double wpm = (Math.round((textLength/5.0)/timeMin)*10000.0)/10000.0;
		if (wpm > 300) {
			JOptionPane.showMessageDialog(null, "Congratulations! You totally cheated. Probably by making a super short text to type out.");
			return;
		}
		String[] options = {"Type again!", "Back to menu"};

		int output = JOptionPane.showOptionDialog(null, "You typed " + wpm + " WPM!", 
				"", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, null);
		if (output == 0) {
			try {
				typingDoc.terminate();
			} catch (BadLocationException e) {
				System.err.println(e);
			}
			textPane.setText(stringBuffer.toString().trim());
		} else if (output == 1) {
			back.doClick();
		}
	}
}
