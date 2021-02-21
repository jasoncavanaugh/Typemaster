package typemaster;

import javax.swing.*;
import java.util.Map;

import javax.swing.text.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Stack;

/* This class represents a special kind of DefaultStyledDocument. The contents of this */
public class TypingDocument extends DefaultStyledDocument {
	int location = 0, wrongLocation = 0;
	long startTime;
	final StyleContext cont;
	final AttributeSet redAttribute, greenAttribute, whiteAttribute;
	boolean wrong = false, over = false, wrongSpace = false, end = false, started = false;
	Stack<Integer> wrongSpaceLocations = new Stack<Integer>();
	PlayScreen playScreen;

	public TypingDocument (PlayScreen playScreen) {
		super();
		this.playScreen = playScreen;
		cont = StyleContext.getDefaultStyleContext();
		redAttribute = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground , Color.RED);
		greenAttribute = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, Color.GREEN);
		whiteAttribute = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, Color.WHITE);
	}
	
	@Override
	public void insertString(int offset, String inputStr, AttributeSet a) throws BadLocationException {
		if (end) return;//If we have reached the end of the text, we do not want this function to do anything
		if (getLength() == 0) {
			super.insertString(offset, inputStr, whiteAttribute);//Since the setText() method of JTextPane calls this method, we want to
			//make sure that it's calling the parent version of insertString() and not this one when it is initializing text
		} else if (wrong) {
			//If wrong, then we want everything to just be red text
			String curStr = getText(location, 1);//The current string in the document
			super.remove(location, 1);//We want to remove the location the first character of the document
			if (curStr.matches(" ")) {
				//If the character we pulled is a non-word character (i.e. a space), we want to insert the red exclamation mark
				super.insertString(location, "!", redAttribute);
				wrongSpaceLocations.push(location);
				wrongSpace = true;
			} else {
				//Otherwise, we just reinsert the current string into the document
				super.insertString(location, curStr, redAttribute);
			}
			location++;
		} else {
			if (!started) {
				started = true;
				startTime = System.currentTimeMillis();
			}
			char input = inputStr.charAt(0);//What the user inputted
			String curStr = getText(location, 1);//What the current string is in the document
			super.remove(location, 1);//Remove the current string in the document (i.e. the first character)
			char curChar = curStr.charAt(0);
			if (input == curChar) {
				//If they match, we can just insert the input string with the green attribute
				super.insertString(location, inputStr, greenAttribute);
			} else {
				//Otherwise, we insert something with the red attribute
				if (curStr.matches(" ")) {
					super.insertString(location, "!", redAttribute);
					wrongSpace = true;
					wrongSpaceLocations.push(location);
				} else {
					super.insertString(location, curStr, redAttribute);
				}
				wrong = true;
				wrongLocation = location;
				if (playScreen.masterMode == true) {
					over = true;
					end = true;
					String[] options = {"Try again?", "Back to menu"};

					int output = JOptionPane.showOptionDialog(null, "You are typing in Master Mode! One mistake means you lose!", 
							"", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, null);
					if (output == 0) {
						terminate();
						super.insertString(0, playScreen.getStringBufferContents().trim(), whiteAttribute);
					} else if (output == 1) {
						playScreen.back.doClick();
					}
					return;
				}
			}
			location++;
		}
		if (location == getLength()) {
			System.out.println(location);
			end = true;
			if (!wrong) {
				over = true;
				playScreen.showTime(System.currentTimeMillis(), startTime);
			}
			//If the location == the length of the document, we've come to the end
		}
	}
	
	@Override
	public void remove(int offset, int len) throws BadLocationException {
		if (over) return;
		if (getLength() == 0) {
			//If there is no text, I just want this method to behave normally
			super.remove(offset, len);
		} else {
			if (location == 0) return;//If the location is 0, do nothing
			String curString = getText(--location, 1);
			super.remove(location, 1);
			over = false;
			if (wrongSpace && location == wrongSpaceLocations.peek()) {
				wrongSpaceLocations.pop();
				super.insertString(location, " ", whiteAttribute);
				if (wrongSpaceLocations.isEmpty()) wrongSpace = false;
			} else {
				super.insertString(location, curString, whiteAttribute);
			}
			if (location == wrongLocation) wrong = false;
			end = false;
		}
	}
	
	public void terminate() throws BadLocationException {
		super.remove(0, super.getLength());
		wrongSpaceLocations.clear();
		wrong = false;
		over = false;
		wrongSpace = false;
		end = false;
		started = false;
		location = 0;
		wrongLocation = 0;
	}
}

