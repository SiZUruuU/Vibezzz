package ControlPanel.Buttons;

import ControlPanel.ButtonManager;
import Main.Panel;
import Main.UI;
import javax.swing.JOptionPane;

public class AddPlaylistButton extends ButtonManager {

    public AddPlaylistButton(Panel panel, UI ui) {
        super(0, 0, 0, 0, panel, ui);
    }

    @Override
    public void execute(int mouseX, int mouseY) {
        String playlistName = JOptionPane.showInputDialog(null, "Enter Playlist Name:", "Create New Playlist", JOptionPane.PLAIN_MESSAGE);

        if (playlistName != null && !playlistName.trim().isEmpty()) {
            String cleanName = playlistName.trim();

            //Duplicate stopper, if code detects a duplicate, method ends immediately
            if (ui.createdPlaylists.contains(cleanName)) {
                JOptionPane.showMessageDialog(null, "A playlist with that name already exists!", "Duplicate Name", JOptionPane.WARNING_MESSAGE);
                return; // Stopss the method quickly as soon as duplicate is detected
            }

            // Inside AddPlaylistButton.java execute() method:
            ui.createdPlaylists.add(cleanName);
            ui.refreshPlaylistButtons();
            panel.repaint();
        }
    }
}