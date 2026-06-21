import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GamePanel extends JPanel implements ActionListener {
    private Snake snake;
    private Apple apple;
    private int score;
    private boolean running;
    private boolean paused; // Pause state
    private Timer timer;
    private long startTime; //  counting seconds
    private long pausedTime; //  track pause duration

    // IMAGES
    private Image backgroundImage;
    private Image appleImage;

    private SoundManager soundManager;

    public GamePanel() {
        setPreferredSize(new Dimension(GameConstants.BOARD_WIDTH, GameConstants.BOARD_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);

        soundManager = new SoundManager();
        soundManager.playLoop();


        try {
            backgroundImage = new ImageIcon(getClass().getResource("/background.jpg")).getImage();
        } catch (Exception e) {
            System.out.println("Background not found, using black");
        }

        try {
            appleImage = new ImageIcon(getClass().getResource("/apple.png")).getImage();
        } catch (Exception e) {
            System.out.println("Apple image not found, using red oval");
        }


        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                Snake snake = getSnake();

                // Only allow movement if game is running and NOT paused
                if (running && !paused) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_LEFT:  snake.setDirection('L'); break;
                        case KeyEvent.VK_RIGHT: snake.setDirection('R'); break;
                        case KeyEvent.VK_UP:    snake.setDirection('U'); break;
                        case KeyEvent.VK_DOWN:  snake.setDirection('D'); break;
                    }
                }

                // Handle Pause (P key)
                if (e.getKeyCode() == KeyEvent.VK_P) {
                    togglePause();
                }

                // Handle Restart (Space)
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    if (!running) {
                        restart();
                    }
                }
            }
        });

        snake = new Snake();
        apple = new Apple(appleImage);
        apple.generateNewPosition(snake);
        score = 0;
        running = true;
        paused = false;
        startTime = System.currentTimeMillis(); // Start the clock
        pausedTime = 0;

        timer = new Timer(GameConstants.DELAY, this);
        timer.start();

        requestFocusInWindow();
    }

    // Toggle pause state
    private void togglePause() {
        if (running) {
            paused = !paused;
            if (paused) {
                // When pausing, record the time
                pausedTime = System.currentTimeMillis();
                soundManager.stop(); // Stop music on pause
            } else {
                // When resuming, adjust start time so the timer doesn't count pause duration
                long pauseDuration = System.currentTimeMillis() - pausedTime;
                startTime += pauseDuration;
                soundManager.playLoop(); // Resume music
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, GameConstants.BOARD_WIDTH, GameConstants.BOARD_HEIGHT, this);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, GameConstants.BOARD_WIDTH, GameConstants.BOARD_HEIGHT);
        }

        if (running) {
            apple.draw(g);
            snake.draw(g);


            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, GameConstants.BOARD_WIDTH, 35);

            // Score (Left side)
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.drawString("Score: " + score, 15, 25);

            // Timer (Right side)
            if (!paused) {
                long elapsedMillis = System.currentTimeMillis() - startTime;
                long seconds = (elapsedMillis / 1000) % 60;
                long minutes = (elapsedMillis / (1000 * 60)) % 60;
                String timeString = String.format(" Time: %02d:%02d", minutes, seconds);
                FontMetrics metrics = getFontMetrics(g.getFont());
                g.drawString(timeString, GameConstants.BOARD_WIDTH - metrics.stringWidth(timeString) - 15, 25);
            } else {
                // Show "PAUSED" and the time when paused
                g.setColor(Color.YELLOW);
                g.setFont(new Font("Arial", Font.BOLD, 18));
                long elapsedMillis = System.currentTimeMillis() - startTime;
                long seconds = (elapsedMillis / 1000) % 60;
                long minutes = (elapsedMillis / (1000 * 60)) % 60;
                String timeString = String.format("Pause  %02d:%02d", minutes, seconds);
                FontMetrics metrics = getFontMetrics(g.getFont());
                g.drawString(timeString, GameConstants.BOARD_WIDTH - metrics.stringWidth(timeString) - 15, 25);
            }


            // Pause hint text
            g.setColor(new Color(255, 255, 255, 100));
            g.setFont(new Font("Arial", Font.PLAIN, 12));
            g.drawString("Press P to pause the game", 15, GameConstants.BOARD_HEIGHT - 15);

        } else {
            showGameOver(g);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running && !paused) { // Only move if not paused
            snake.move();

            if (snake.eat(apple)) {
                score++;
                apple.generateNewPosition(snake);
            }

            if (snake.checkCollision()) {
                running = false;
                timer.stop();
                soundManager.stop();
            }
        }
        repaint();
    }

    private void showGameOver(Graphics g) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, GameConstants.BOARD_WIDTH, GameConstants.BOARD_HEIGHT);

        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 60));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Game over",
                (GameConstants.BOARD_WIDTH - metrics1.stringWidth("Game over")) / 2,
                GameConstants.BOARD_HEIGHT / 2 - 60);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Score: " + score,
                (GameConstants.BOARD_WIDTH - metrics2.stringWidth("Score: " + score)) / 2,
                GameConstants.BOARD_HEIGHT / 2);

        // Show final time
        long elapsedMillis = System.currentTimeMillis() - startTime;
        long seconds = (elapsedMillis / 1000) % 60;
        long minutes = (elapsedMillis / (1000 * 60)) % 60;
        String timeString = String.format("Time: %02d:%02d", minutes, seconds);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        FontMetrics metrics3 = getFontMetrics(g.getFont());
        g.drawString(timeString,
                (GameConstants.BOARD_WIDTH - metrics3.stringWidth(timeString)) / 2,
                GameConstants.BOARD_HEIGHT / 2 + 60);
    }

    public boolean isRunning() {
        return running;
    }

    public void restart() {
        snake = new Snake();
        apple = new Apple(appleImage);
        apple.generateNewPosition(snake);
        score = 0;
        running = true;
        paused = false;
        startTime = System.currentTimeMillis(); // Reset timer
        pausedTime = 0;
        timer.start();
        soundManager.restart();
        repaint();
        requestFocusInWindow();
    }

    public Snake getSnake() {
        return snake;
    }
}