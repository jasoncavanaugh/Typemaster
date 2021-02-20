package typemaster;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

	/*
	 * The menu will look like this:
	 * 
	 *                   |Back|
	 *      
	 *     -----------------------------------
	 *     |CustomTexts              		 | 
	 *     |	*custom_text1				 |	
	 *     |	*custom_text2				 |
	 *     |	*custom_text3   			 | 						 
	 *     |								 |
	 *     |								 |
	 *     |								 |
	 *     -----------------------------------
	 *     
	 *      |Add new custom text|      |edit| |delete|
	 */
public class YourTextsMenu implements MenuInt {
	/* We are going to put the contents of this menu directly on the 
	 * main frame. Therefore, we need to have a GridBagConstraints object. */
	GridBagConstraints gbc;

	/* As always, we will need a reference to the base in order to call 
	 * the runAgain() method. We also will need a reference to the addCustomText
	 * object in order to allow the addCustomText to know what files
	 * it needs to load. */
	Base base;
	AddCustomText addCustomText;

	/* Here are the buttons we will be using for this menu. */
	JButton back, addNewCustomText, edit, save, delete;

	/*Our text tree for showing the names of all the custom texts */
	JTree customTextTree;

	/*The scroll pane that we will place our customTextTree in */
	JScrollPane scrollPane;

	/* The root node for the customTextTree */
	DefaultMutableTreeNode customTextRoot;

	/* If the user decides they want to edit an existing file, the name of that
	 * file will be stored in the fileToEdit variable. If the user decides that
	 * they want to add a new file, the fileToEdit variable will be null. */
	String fileToEdit; 

	/* If the user decides to edit an existing file, this variable will be used
	 * to store the index of that file in the customTextTree. */
	int fileIdx;
	boolean editing;

	/* This stack is what we will use to load the custom texts into the custom 
	 * texts tree when the game is started up. */
	ArrayList<String> loadedCustomTexts;

	/* This path variable will store the location of the customTextDirectory
	 * (./userData/customText). When the game is started up, all the files
	 * from that directory will be loaded into the customTextTree. */
	Path customTextsDir;

	public YourTextsMenu(Base base, AddCustomText addCustomText) {
		/* First, we initialize all the instance variables of the class */
		this.base = base;
		this.addCustomText = addCustomText;
		gbc = new GridBagConstraints();

		/* Buttons */
		addNewCustomText = new JButton("Add new custom text");
		edit = new JButton("Edit");
		delete = new JButton("Delete");
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (fileToEdit == null) {
					JOptionPane.showMessageDialog(null, "Please select a file");
				} else {
					int answer = JOptionPane.showConfirmDialog(null, 
							"Are you sure you want to delete " + fileToEdit + "?", 
							"", JOptionPane.YES_NO_OPTION);
					if (answer == 0) {
						DefaultTreeModel model = (DefaultTreeModel) customTextTree.getModel();
						DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
						root.remove(fileIdx);
						Path toDelete = Paths.get("./userData/customTexts/" + fileToEdit.replaceAll(" ", "_") + ".txt");
						try {
							Files.delete(toDelete);
						} catch (NoSuchFileException x) {
							System.err.println(e);
						} catch (IOException x) {
							System.err.println(e);
						}
						model.reload(root);
					}
				}
			}
		});

		loadedCustomTexts = new ArrayList<String>();

		/* This is the root of the customTextTree. This is pretty important
		 * because we will need this guy in the PlayMenu class, which is why
		 * he has his own get method (getCustomTextRoot()). */
		customTextRoot = new DefaultMutableTreeNode("Custom Texts");

		/* Create the customText directory and load up all the files from that
		 * directory into the customTextTree. Scroll to bottom to see method
		 * declaration. */
		createCustomTextDirectoryAndLoad();


		/* Setting up the customTextTree */
		customTextTree = new JTree(customTextRoot);
		customTextTree.setPreferredSize(new Dimension(800, 800));
		customTextTree.setBackground(new Color(180, 180, 180));
		customTextTree.setFont(new Font("Monospaced", Font.ROMAN_BASELINE, 23));
		customTextTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		customTextTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) customTextTree.getLastSelectedPathComponent();
				if (node == null) return;
				if (node.isLeaf()) {
					fileToEdit = (String) node.getUserObject();
					fileIdx = customTextRoot.getIndex(node);
				}
			}
		});
		
		/* Setting up the edit button */
		edit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (fileToEdit == null) {
					JOptionPane.showMessageDialog(null, "Please select a text to edit.");
				} else {
					addCustomText.setFileToEdit(fileToEdit);
					base.mode = Menus.AddCustomText;
					base.runAgain();
				}
			}
		});

		/* Setting up the addNewCustomText button */
		addNewCustomText.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fileToEdit = null;
				addCustomText.setFileToEdit(fileToEdit);
				base.mode = Menus.AddCustomText;
				base.runAgain();
			}
		});

		/* Setting up a customTextTree to the scrollPane. */
		scrollPane = new JScrollPane(customTextTree);
		
		/* Setting up the back button */
		back = new JButton("Back");
		back.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				base.mode = Menus.MainMenu;
				base.runAgain();
			}
		});
	}

	public DefaultMutableTreeNode getCustomTextRoot() {
		return this.customTextRoot;
	}

	@Override
	public void displayContent() {
		if (addCustomText.newFileNameIsNotNull()) {
			String newFileName = addCustomText.getNewFileName();
			DefaultTreeModel model = (DefaultTreeModel) customTextTree.getModel();
			DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
			if (fileToEdit != null) {
				root.remove(fileIdx);
				if (!fileToEdit.equals(newFileName)) {
					Path toDelete = Paths.get("./userData/customTexts/" + fileToEdit.trim().replaceAll(" ", "_") + ".txt");
					try {
						Files.delete(toDelete);
					} catch (NoSuchFileException e) {
						System.err.println(e);
					} catch (IOException e) {
						System.err.println(e);
					}
				}
			} 
			root.add(new DefaultMutableTreeNode(newFileName));
			model.reload(root);
		}

		gbc.anchor = GridBagConstraints.CENTER;
		gbc.weighty = 1;

		gbc.gridy = 1;
		gbc.gridx = 0;
		base.mainFrame.getContentPane().add(back, gbc);

		gbc.gridy = 2;
		base.mainFrame.getContentPane().add(scrollPane, gbc);

		gbc.gridy = 3;
		gbc.gridx = 0;
		base.mainFrame.getContentPane().add(addNewCustomText, gbc);
		gbc.gridx = 1;
		base.mainFrame.getContentPane().add(edit, gbc);
		gbc.gridx = 2;
		base.mainFrame.getContentPane().add(delete, gbc);
	}

	
	private void createCustomTextDirectoryAndLoad() {
		customTextsDir = Paths.get("./userData/customTexts");
		try {
			Files.createDirectories(customTextsDir);
		} catch(IOException x) {
			System.err.println(x);
		}

		String s;
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(customTextsDir)) {
		    for (Path file: stream) {
		    	s = file.toString().substring(23, file.toString().length() - 4).replaceAll("_", " ");
		    	loadedCustomTexts.add(s);
		    }
		} catch (IOException | DirectoryIteratorException e) {
		    System.err.println(e);
		}
		while (!loadedCustomTexts.isEmpty()) {
			customTextRoot.add(new DefaultMutableTreeNode(loadedCustomTexts.remove(0)));
		}
	}
}
