package ui;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioSystem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import transcode.Transcode;
import wiiremotej.PrebufferedSound;
import wiiremotej.WiiRemote;
import wiiremotej.WiiRemoteJ;

public class Main {
    static {
        System.setProperty("bluecove.jsr82.psm_minimum_off", "true");
    }

    public static void main(String args[]) throws IllegalStateException,
            IllegalArgumentException, IOException {
        JFrame frame = new JFrame("Wiimote Audio Player");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        JLabel label = new JLabel("attempting to connect");
        panel.add(label);
        frame.getContentPane().add(panel);

        frame.pack();

        frame.setVisible(true);

        WiiRemote wiimote = getWiimote(label);
        label.setText("connected");
        setLightsOn(wiimote);

        String filename = pickFile();

        File transcoded = Transcode.transcode(filename);

        PrebufferedSound sound = bufferSound(transcoded);
        wiimote.setSpeakerEnabled(true);
        wiimote.playPrebufferedSound(sound, WiiRemote.SF_PCM8S);
    }

    private static String pickFile() {
        JFileChooser jfc = new JFileChooser();
        int showOpenDialog = jfc.showOpenDialog(null);
        while (showOpenDialog != JFileChooser.APPROVE_OPTION) {
            showOpenDialog = jfc.showOpenDialog(null);
        }
        return jfc.getSelectedFile().getAbsolutePath();
    }

    private static void setLightsOn(WiiRemote wiimote) {
        try {
            wiimote.setLEDLights(new boolean[] { true, true, true, true });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static WiiRemote getWiimote(JLabel label) {
        WiiRemote wiimote = null;
        int tryNumber = 0;
        while (wiimote == null) {
            try {
                wiimote = WiiRemoteJ.findRemote();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            label.setText("retrying to connect: " + ++tryNumber);
        }
        return wiimote;
    }

    private static PrebufferedSound bufferSound(File file) {
        try {
            return WiiRemote.bufferSound(AudioSystem.getAudioInputStream(file));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
