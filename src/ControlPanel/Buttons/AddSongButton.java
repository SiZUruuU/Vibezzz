package ControlPanel.Buttons;

import ControlPanel.ButtonManager;
import ControlPanel.Song;
import ControlPanel.SongClicker;
import Main.Panel;
import Main.UI;
import javax.swing.JFileChooser;
import java.util.ArrayList;

public class AddSongButton extends ButtonManager {
    public AddSongButton(Panel panel, UI ui) { super(0, 0, 0, 0, panel, ui); }

    @Override
    public void execute(int mouseX, int mouseY) {
        if (!ui.insidePlaylistView) return;

        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            // Create song and add to a map in UI
            Song s = new Song(chooser.getSelectedFile().getName(), "Unknown", path, "NO_IMAGE", "0:00");

            ui.playlistSongs.putIfAbsent(ui.selectedPlaylistName, new ArrayList<>());
            ui.playlistSongs.get(ui.selectedPlaylistName).add(s);
            ui.backEndButtons.add(new SongClicker(panel, ui, s));
            panel.repaint();
        }
    }
}