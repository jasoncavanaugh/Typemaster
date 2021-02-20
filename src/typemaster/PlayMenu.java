package typemaster;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
/** 
 * This is the menu that is displayed if the Play button from the main menu is
 * clicked. This menu will display a file tree with all the possible texts 
 * that the user can type out. 
 * 		There are two possible texts that the user can decide to type: default 
 * texts and custom texts. Default texts are texts that come preloaded into 
 * the program. There are three of them. The first is called "education" and is 
 * a quote from the book The Blank Slate by Steven Pinker. The second is called 
 * "Sowell" and is a quote from the book A Conflict of Visions by Thomas Sowell. 
 * The third quote is called "unite" and is also from The Blank Slate by Steven 
 * Pinker. All of the default texts are stored in the current directory that the 
 * program is running in (./).
 * 		There are also custom texts, which are texts that the user can add
 * into the program. All of the custom texts are stored in a directory called
 * customTexts. The path for the customTexts directory is "./userData/customTexts". 
 */

/*
 * The menu looks like this
 * 	
 *                   |Back|
 *      
 *     -----------------------------------
 *     |Texts							 |
 *     | Default Texts					 |
 *     | 	*education					 |
 *     |	*Sowell						 |
 *     | 	*unite						 |
 *     | Custom Texts              		 | 
 *     |	*custom_text1				 |	
 *     |	*custom_text2				 |
 *     |	*custom_text3   			 | 						 
 *     |								 |
 *     |								 |
 *     |								 |
 *     -----------------------------------
 *     				
 *     				|Type!|
 *     
 * */
public class PlayMenu implements MenuInt {
	/* We are going to put the contents of this menu directly on the 
	 * main frame. Therefore, we need to have a GridBagConstraints object. */
	GridBagConstraints gbc;
	/* As always, we will need a reference to the base in order to call 
	 * the runAgain() method. */
	Base base;
	/* We will also need a reference to the YourTextsMenu in order
	 * to get the root to the customTextTree object from that class. That
	 * root will then be attached to the text tree in this class, which will
	 * then be displayed in a scroll pane object. */
	YourTextsMenu yourTextsMenu;

	/* Scroll pane for displaying the text tree */
	JScrollPane scrollPane;
	/* Our tree nodes for the text tree. Root refers to the node that 
	 * has the name "Texts". defaultTextRoot refers to the node that
	 * has the name "Default Texts". customTextRoot refers to the node that
	 * has the name "Custom Texts". */
	DefaultMutableTreeNode root, defaultTextRoot, customTextRoot;
	/* Our text tree */
	JTree textTree;
	/* Our buttons. back directs back to the main menu. type directs to the
	 * PlayScreen menu. */
	JButton type, back;
	/* This string holds the name of the file that the user would like to type, 
	 * as indicated by which node they select in the textTree */
	String fileToType;

	public PlayMenu(Base base, YourTextsMenu yourTextsMenu) {
		this.base = base;
		this.yourTextsMenu = yourTextsMenu;
		gbc = new GridBagConstraints();

		/* Making the defaultTextRoot and then adding nodes to the defaultTextRoot */
		defaultTextRoot = new DefaultMutableTreeNode("Default Texts");
		defaultTextRoot.add(new DefaultMutableTreeNode("education"));
		defaultTextRoot.add(new DefaultMutableTreeNode("Sowell"));
		defaultTextRoot.add(new DefaultMutableTreeNode("unite"));

		/* Getting the customTextRoot from the yourTextsMenu */
		customTextRoot = yourTextsMenu.getCustomTextRoot();

		/* Creating the root of the text tree */
		root = new DefaultMutableTreeNode("Texts");
		/* Adding defaultTextRoot to the root of the text tree */
		root.add(defaultTextRoot);
		/* Adding customTextRoot to the root of the text tree */
		root.add(customTextRoot);

		/* Setting up the textTree object */
		textTree = new JTree(root);
		textTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		textTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) textTree.getLastSelectedPathComponent();
				if (node == null) return;

				String s = (String) node.getUserObject();
				s = s.replaceAll(" ", "_");
				if (defaultTextRoot.isNodeChild(node)) {
					fileToType = "./" + s + ".txt";
				} else if (customTextRoot.isNodeChild(node)) {
					fileToType = "./userData/customTexts/" + s + ".txt";
				}
			}
		});
		textTree.setBackground(new Color(180, 180, 180));
		textTree.setPreferredSize(new Dimension(800, 800));
		textTree.setFont(new Font("Monospaced", Font.ROMAN_BASELINE, 23));

		/* Adding the textTree to the scroll pane */
		scrollPane = new JScrollPane(textTree);

		/* Setting up the type button. When clicked, if no file has been
		 * selected, a dialog box will pop up that prompts the user to select
		 * a text. Otherwise, it will redirect to the PlayScreen. */
		type = new JButton("Type!");
		type.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (fileToType == null) {
					JOptionPane.showMessageDialog(null, "Please select a text to type.");
				} else {
					base.mode = Menus.PlayScreen;
					base.runAgain();
				}
			}
		});

		/* Setting up the back button. This redirects back to the MainMenu. */
		back = new JButton("Back");
		back.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fileToType = null;
				base.mode = Menus.MainMenu;
				base.runAgain();
			}
		});
	}
	
	/* This method will be used by the PlayScreen object. In order for the
	 * PlayScreen to know which file to load, it needs to know which file
	 * the user selected from the textTree object. */
	public String getFileToType() {
		String output = fileToType;
		fileToType = null;
		return output;
	}

	@Override
	public void displayContent() {
		/* Every time we display the contents of this menu, we need to update
		 * the text tree just in case the user has added any custom texts. */
		DefaultTreeModel model = (DefaultTreeModel) textTree.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
		model.reload(root);
		
		/* We want to make sure that each of the sub-branches of the tree 
		 * are expanded by default. So we just iterate through them and expand
		 * really quick. */
		for (int i = 0; i < textTree.getRowCount(); i++) {
			textTree.expandRow(i);
		}
		/* Then we display everything */
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.weighty = 1;
		gbc.gridy = 1;
		base.mainFrame.getContentPane().add(back, gbc);
		
		gbc.gridy = 2;
		base.mainFrame.getContentPane().add(scrollPane, gbc);

		gbc.gridy = 3;
		base.mainFrame.getContentPane().add(type, gbc);
	}
}
