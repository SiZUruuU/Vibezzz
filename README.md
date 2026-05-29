Vibezz is a custom desktop media player built from scratch using Java Swing and the JLayer audio library.

Music playback is handled by the JLayer library.

Basic track information (Title, Artist, and Duration) is read using Java's native javax.sound.sampled.AudioSystem. When a user selects a music folder, the app scans the directory, reads these properties, and converts the microsecond durations into standard MM:SS string formats.

Instead of relying on an external library for album art, the app uses a custom binary parser built with java.io.RandomAccessFile. It opens the MP3 file at the byte level, reads the ID3v2 headers (calculating sync-safe integers), and hunts down the APIC (Attached Picture) frame. Once the raw image bytes are extracted, javax.imageio.ImageIO loads the image into memory and mathematically crops it into a perfect 1:1 square before caching it locally.

The entire interface is drawn from scratch using standard Java Swing (JPanel and Graphics2D). Instead of attaching individual action listeners to dozens of buttons (which bloats memory), the app uses a single global MouseHandler. This handler tracks raw X/Y coordinates and uses basic collision math to route clicks, scroll events, and window drags to the appropriate custom hitboxes based on the current UI state.



Unadded Features:
1. Delete Playlist (we forgot)
2. Lyrics Reader (lrc files/embedded)
3. Overall a better interface
4. Intro Sequence with branding (we did not know how to do art)

Ai: Used Ai especially for the UI and library usage, the overall structure was made by humans.

File Overview:

Codebase is separated into the backend (control panel) and the rendering pipeline (Main)

---Control panel---
- Handles all file persistence, audio, and data manipulation

AudioEngine: It runs the JLayer audio player on a background thread so the app never freezes while music is playing. This is how it figures out where you are in a song.

MusicHandler: Scans the files, uses ID3v2 parser to rip embedded metadata and saves the playlists

VolumeController: Handles volume, also why the player can press pause without too much delay

Song: Holds the track data for the app

---Inputs/Buttons---

UI: It keeps track of what you can see: whats playing, where you click, and connects it to the right button

MouseHandler: handles mouse inputs

KeyInputHandler: Keyboard inputs

ButtonManager: Draws the hitboxes 

Buttons and Views: Holds the logic for the buttons and draws the specific windows (ps views is in the same package as main because java was being annoying and couldnt read the rest of the files while inside another subfolder.)


How to run the app

Download the Music Playlist File - Extract it
Make sure the libraries are loaded - those 3 in the lib folder
vscode: you can check this by going to the editors and looking at java projects - Referenced Libraries to see if they are inside

Run Vibezz.java
Library - Add Folder
Select the music folder
Wait for the metadata to load
Done!

p.s: if AudioEngine and VolumeController has some underlined red stuff dont worry it still runs - probably some library issue again.
