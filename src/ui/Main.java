package ui;

import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import wiiremotej.WiiDevice;
import wiiremotej.WiiRemote;
import wiiremotej.WiiRemoteJ;

public class Main {
	static
	{
		System.setProperty("bluecove.jsr82.psm_minimum_off", "true");
	}
	public static void main (String args[]) {
		JFrame frame = new JFrame("Wiimote Audio Player");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panel = new JPanel();
		JLabel label = new JLabel("attempting to connect");
		panel.add(label);
		frame.getContentPane().add(panel);
		
		frame.pack();
		
		frame.setVisible(true);
		
		WiiRemote wiimote = null;
		int tryNumber = 0;
		while (wiimote == null) {
			try
			{
				wiimote = WiiRemoteJ.findRemote();
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			label.setText("retrying to connect: " + ++tryNumber);
		}
		label.setText("connected");
		try {
			wiimote.setLEDLights(new boolean[]{true, true, true, true});
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
