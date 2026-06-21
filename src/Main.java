import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // launch the main menu, it will open the game when clicked
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainMenu();
            }
        });
    }
}