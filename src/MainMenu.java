import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends JFrame {

    public MainMenu() {
        setTitle("Simple snake-main menu");
        setSize(450, 380); // Made slightly taller for the new button
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel with a nice background color
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        // Title Label
        JLabel titleLabel = new JLabel(" Simple Snake ");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.GREEN);
        panel.add(titleLabel, gbc);

        // Subtitle
        gbc.gridy = 1;
        JLabel subLabel = new JLabel("Classic game");
        subLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subLabel.setForeground(Color.LIGHT_GRAY);
        panel.add(subLabel, gbc);

        // Start Button
        gbc.gridy = 2;
        gbc.insets = new Insets(30, 10, 10, 10);
        JButton startButton = new JButton("Start");
        startButton.setFont(new Font("Arial", Font.BOLD, 18));
        startButton.setBackground(new Color(0, 150, 0));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setPreferredSize(new Dimension(200, 50));

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the menu window
                startGame(); // Start the actual game
            }
        });
        panel.add(startButton, gbc);

        //instructions
        gbc.gridy = 3;
        gbc.insets = new Insets(5, 10, 5, 10);
        JButton instructionsButton = new JButton("Instructions");
        instructionsButton.setFont(new Font("Arial", Font.PLAIN, 14));
        instructionsButton.setBackground(new Color(78, 104, 65, 255));
        instructionsButton.setForeground(Color.WHITE);
        instructionsButton.setFocusPainted(false);
        instructionsButton.setPreferredSize(new Dimension(200, 40));

        instructionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showInstructions();
            }
        });
        panel.add(instructionsButton, gbc);


        // Exit Button
        gbc.gridy = 4;
        gbc.insets = new Insets(10, 10, 10, 10);
        JButton exitButton = new JButton("Close game");
        exitButton.setFont(new Font("Arial", Font.PLAIN, 14));
        exitButton.setBackground(new Color(108, 12, 12));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.setPreferredSize(new Dimension(150, 40));

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // Close the application
            }
        });
        panel.add(exitButton, gbc);

        add(panel);
        setVisible(true);
    }

    // show instructions
    private void showInstructions() {
        String instructions =
                "How to play?:\n\n" +
                        "1. Use keys(← ↑ → ↓)\n" +
                        "   to control the snake.\n" +
                        "2. Eat red apples (🍎),\n" +
                        "   to grow the snake\n" +
                        "   and get points.\n" +
                        "3. don't crash into the walls\n" +
                        "  and don't bite your tail!\n" +
                        "4. if you crash into the wall or tail, game will be over\n" +

                        "5. Enter key (Space),\n" +
                        "   to restart.\n\n" +
                        "Good Luck! 🎮";

        // Create a popup dialog with the instructions
        JOptionPane.showMessageDialog(
                this,
                instructions,
                "Instructions",
                JOptionPane.INFORMATION_MESSAGE,
                new ImageIcon()
        );
    }


    private void startGame() {
        //  create the game frame
        JFrame gameFrame = new JFrame("Simple snake game");
        GamePanel gamePanel = new GamePanel();

        gameFrame.add(gamePanel);
        gameFrame.pack();
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setResizable(false);
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setVisible(true);
    }
}