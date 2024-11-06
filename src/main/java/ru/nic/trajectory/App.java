package ru.nic.trajectory;

import javax.swing.SwingUtilities;


public class App 
{
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
