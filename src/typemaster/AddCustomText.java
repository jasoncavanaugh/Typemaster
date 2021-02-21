package typemaster;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;


/* This is the menu that is displayed when the user clicks the 
 * addCustomText button from the YourTexts menu. When this 
 * 
 * */
public class AddCustomText implements MenuInt {
	/* As always, we will need a reference to the base in order to call 
	 * the runAgain() method. We also will need a reference to the addCustomText
	 * object in order to allow the addCustomText to know what files
	 * it needs to load. */
	Base base;
	/* We will be putting all contents of this menu into this JPanel */
	JPanel panel;
	/* This textPane is going to be where the user can type/edit the new file*/
	JTextPane textPane;
	/* A button to save and a button to go back to the YourTextsMenu */
	JButton save, back;
	/* In case the user opts to edit an existing file, this stringBuffer object
	 * will be used to load all of the contents of the file into the textPane. */
	StringBuffer stringBuffer;
	/* If the user decides that they want to edit an existing file, then they
	 * will set the fileToEdit variable to that file's name using the 
	 * setFileToEdit() method. */
	String fileToEdit;
	/* If the user wishes to save the edited/created file, they will be prompted
	 * to enter a new name for that file. The new file name will be stored in 
	 * this variable */
	String newFileName;
	/* This boolean flag tells us whether or not the text file that the user
	 * is editing has been saved. */
	boolean saved;

	public AddCustomText(Base base) {
		/* Initializing instance variables. */
		this.base = base;
		stringBuffer = new StringBuffer();
		
		/* Making the panel object and setting up its size */
		panel = new JPanel(new BorderLayout());
		panel.setPreferredSize(new Dimension(800, 800));

		/* Setting up the textPane. This is where the user will be editing
		 * the text. */
		textPane = new JTextPane();
		/* We need a document listener that will tell us when the 
		 * contents of the textPane have been changed. Whenever the contents of
		 * the textPane change, we will prompt the user to save the contents
		 * of the document again. */
		textPane.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) { saved = false; }
			@Override
			public void removeUpdate(DocumentEvent e) { saved = false; }
			@Override
			public void changedUpdate(DocumentEvent e) { saved = false; }
		}); 
		/* Next we set up the style of the textPane. */
		StyleContext context = StyleContext.getDefaultStyleContext();
		textPane.setCharacterAttributes(context.addAttribute(context.getEmptySet(), 
				StyleConstants.Foreground, Color.WHITE), true);
		textPane.setFont(new Font("Monospaced", Font.ROMAN_BASELINE, 23));
		textPane.setBackground(Color.DARK_GRAY);
		textPane.setCaretColor(Color.WHITE);

		/* Making the save button */
		save = new JButton("Save");
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (textPane.getText().length() == 0) {
					JOptionPane.showMessageDialog(null, "Please enter some text.");
				} else {
					/* So that we don't get any weird names for files */
					newFileName = JOptionPane.showInputDialog
							("Enter the name of this file").replaceAll
							("[~!@#$%^&*()`;:<>?.,\\[\\]\\{\\}'\"_|]", "").trim();
					saved = true;
					writeFileToDirectory();
				}
			}
		}); 

		/* Making the back button */
		back = new JButton("Back");
		back.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!saved && textPane.getText().length() > 0) {
					int output = JOptionPane.showConfirmDialog
					(null, "Are you sure you want to go back without saving?", 
							"", JOptionPane.YES_NO_OPTION);
					if (output == 0) {
						textPane.setText("");
						base.mode = Menus.YourTextsMenu;
						base.runAgain();
					}
				}  else {
						textPane.setText("");
						base.mode = Menus.YourTextsMenu;
						base.runAgain();
				}
			}
		});

		/* Add all the contents to the panel, which will then be added 
		 * to the mainFrame when we call displayContents(). */
		panel.add(back, BorderLayout.NORTH);
		panel.add(textPane, BorderLayout.CENTER);
		panel.add(save, BorderLayout.SOUTH);
	}
	
	private void writeFileToDirectory() {
		String text = textPane.getText();
		/* If there are any em-dashes in the text, we will have trouble loading
		 * the text later on. Therefore, we convert all the em-dashes in the
		 * to "-" by doing this. */
		text = text.replaceAll("\\p{Pd}", "-");

		/* Then we convert the text to bytes and write it to a file. */
		byte[] data = text.getBytes();
		Path path = Paths.get("./userData/customTexts/" +  newFileName.trim().replaceAll(" ", "_") + ".txt");

		try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(path, CREATE, TRUNCATE_EXISTING, WRITE))) {
			out.write(data, 0, data.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	private void loadText() {
		Path filePath = Paths.get("./userData/customTexts/" + fileToEdit.replaceAll(" ", "_")+ ".txt");
		Charset charset = Charset.forName("US-ASCII");
		try (BufferedReader reader = Files.newBufferedReader(filePath, charset)) {
			String line;
			while ((line = reader.readLine()) != null) {
				stringBuffer.append(line);
			}
		} catch (IOException e) {
			System.err.println(e);
		}
		textPane.setText(stringBuffer.toString());
		stringBuffer.delete(0, stringBuffer.length());
	}
	
	@Override
	public void displayContent() {
		if (fileToEdit != null) {
			loadText();
			fileToEdit = null;
		}
		base.mainFrame.add(panel);
		base.mainFrame.getContentPane().setVisible(true);
		textPane.requestFocusInWindow();
	}

	public boolean newFileNameIsNotNull() {
		return newFileName != null;
	}
	
	public void setFileToEdit(String fileToEdit) {
		this.fileToEdit = fileToEdit;
	}

	public String getNewFileName() {
		String output = newFileName;
		newFileName = null;
		return output;
	}
}
