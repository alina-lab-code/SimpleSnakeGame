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
    private Timer timer;

    // IMAGES
    private Image backgroundImage;
    private Image appleImage;

    private SoundManager soundManager;

    public GamePanel() {
        setPreferredSize(new Dimension(GameConstants.BOARD_WIDTH, GameConstants.BOARD_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);

        soundManager = new SoundManager();
        soundManager.playLoop(); // Music starts playing


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

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:  snake.setDirection('L'); break;
                    case KeyEvent.VK_RIGHT: snake.setDirection('R'); break;
                    case KeyEvent.VK_UP:    snake.setDirection('U'); break;
                    case KeyEvent.VK_DOWN:  snake.setDirection('D'); break;
                    case KeyEvent.VK_SPACE:
                        if (!isRunning()) {
                            restart();
                        }
                        break;
                }
            }
        });


        snake = new Snake();
        apple = new Apple(appleImage);
        apple.generateNewPosition(snake);
        score = 0;
        running = true;

        timer = new Timer(GameConstants.DELAY, this);
        timer.start();

        requestFocusInWindow();
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
            g.fillRect(0, 0, GameConstants.BOARD_WIDTH, 30);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + score,
                    (GameConstants.BOARD_WIDTH - metrics.stringWidth("Score: " + score)) / 2,
                    g.getFont().getSize() + 5);
        } else {
            showGameOver(g);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            snake.move();

            if (snake.eat(apple)) {
                score++;
                apple.generateNewPosition(snake);
            }

            if (snake.checkCollision()) {
                running = false;
                timer.stop();

                soundManager.stop(); // Stop music on game over

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
                GameConstants.BOARD_HEIGHT / 2 - 50);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("score: " + score,
                (GameConstants.BOARD_WIDTH - metrics2.stringWidth("Score: " + score)) / 2,
                GameConstants.BOARD_HEIGHT / 2 + 50);
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
        timer.start();
        repaint();
        requestFocusInWindow();


        soundManager.restart(); //Rewind and play music again

    }

    public Snake getSnake() {
        return snake;
    }
}