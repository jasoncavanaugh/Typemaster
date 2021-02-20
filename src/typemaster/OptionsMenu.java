package typemaster;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;

public class OptionsMenu implements MenuInt {
	Base base;
	PlayScreen playScreen;
	JCheckBox masterMode;
	JButton back;

	public OptionsMenu(Base base, PlayScreen playScreen) {
		this.base = base;
		this.playScreen = playScreen;

		masterMode = new JCheckBox("Master mode");
		masterMode.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					playScreen.setMasterMode(true);
				else if (e.getStateChange() == ItemEvent.DESELECTED)
					playScreen.setMasterMode(false);
			}
		});
		
		back = new JButton("Back");
		back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				base.mode = Menus.MainMenu;
				base.runAgain();
			}
		});
	}

	@Override
	public void displayContent() {
		base.mainFrame.getContentPane().add(masterMode);
		base.mainFrame.getContentPane().add(back);
	}

}