import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;
import javax.sound.sampled.*; // Для музыки
import java.io.File;

public class MainScene extends JPanel implements ActionListener {

    private final int BOARD_WIDTH = 600;
    private final int BOARD_HEIGHT = 600;
    private final int DOT_SIZE = 40;
    private final int ALL_DOTS = (BOARD_WIDTH * BOARD_HEIGHT) / (DOT_SIZE * DOT_SIZE);

    private final int[] x = new int[ALL_DOTS];
    private final int[] y = new int[ALL_DOTS];

    private int dots;
    private int appleX;
    private int appleY;
    private int gameTime = 0; // Счётчик времени

    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;

    private boolean inGame = true;
    private Timer timer;
    private Clip backgroundMusic; // Переменная для музыки

    private Image snakeHead;
    private Image snakeHeadOpen;
    private Image snakeBody;
    private Image apple;
    private Image backgroundField;

    public MainScene() {
        setBackground(Color.black);
        setFocusable(true);
        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        addKeyListener(new FieldKeyListener());

        loadImages();
        initGame();
        playBackgroundMusic(); // Запуск музыки при создании сцены
    }

    private void playBackgroundMusic() {
        try {
            File musicPath = new File("resources/music.wav");
            if (musicPath.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                backgroundMusic = AudioSystem.getClip();
                backgroundMusic.open(audioInput);
                backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
                backgroundMusic.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadImages() {
        snakeHead = new ImageIcon("resources/head.png").getImage();
        snakeHeadOpen = new ImageIcon("resources/openMouth.png").getImage();
        snakeBody = new ImageIcon("resources/bodyPart.png").getImage();
        apple = new ImageIcon("resources/apple.png").getImage();
        backgroundField = new ImageIcon("resources/background.jpg").getImage();
    }

    private void initGame() {
        gameTime = 0; // Сброс времени
        rightDirection = true;
        leftDirection = false;
        upDirection = false;
        downDirection = false;
        dots = 3;

        for (int i = 0; i < dots; i++) {
            x[i] = 240 - (i * DOT_SIZE);
            y[i] = 240;
        }

        locateApple();
        inGame = true;

        if (timer != null) timer.stop();
        timer = new Timer(220, this);
        timer.start();
    }

    private void locateApple() {
        Random rand = new Random();
        appleX = (rand.nextInt(BOARD_WIDTH / DOT_SIZE - 2) + 1) * DOT_SIZE;
        appleY = (rand.nextInt(BOARD_HEIGHT / DOT_SIZE - 2) + 1) * DOT_SIZE;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (inGame) {
            if (backgroundField != null) g.drawImage(backgroundField, 0, 0, BOARD_WIDTH, BOARD_HEIGHT, this);

            // Отрисовка таймера
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 22));
            g.drawString("Time: " + (gameTime / 5), 20, 40);

            if (apple != null) g.drawImage(apple, appleX, appleY, DOT_SIZE, DOT_SIZE, this);

            for (int i = dots - 1; i > 0; i--) {
                Graphics2D gBody = (Graphics2D) g.create();
                int centerX = x[i] + DOT_SIZE / 2;
                int centerY = y[i] + DOT_SIZE / 2;
                if (y[i] > y[i - 1]) gBody.rotate(Math.toRadians(-90), centerX, centerY);
                else if (y[i] < y[i - 1]) gBody.rotate(Math.toRadians(90), centerX, centerY);
                else if (x[i] > x[i - 1]) gBody.rotate(Math.toRadians(180), centerX, centerY);
                gBody.drawImage(snakeBody, x[i] - 6, y[i] - 6, 52, 52, this);
                gBody.dispose();
            }

            if (dots > 0) {
                Graphics2D gHead = (Graphics2D) g.create();
                int hCX = x[0] + DOT_SIZE / 2;
                int hCY = y[0] + DOT_SIZE / 2;
                if (upDirection) gHead.rotate(Math.toRadians(-90), hCX, hCY);
                else if (downDirection) gHead.rotate(Math.toRadians(90), hCX, hCY);
                else if (leftDirection) gHead.rotate(Math.toRadians(180), hCX, hCY);
                gHead.drawImage(snakeHead, x[0] - 6, y[0] - 6, 52, 52, this);
                gHead.dispose();
            }
            Toolkit.getDefaultToolkit().sync();
        } else {
            gameOver(g);
        }
    }

    private void gameOver(Graphics g) {
        g.setColor(Color.white);
        g.setFont(new Font("Helvetica", Font.BOLD, 28));
        g.drawString("Game Over", 230, 300);
    }

    private void checkApple() {
        if (x[0] == appleX && y[0] == appleY) { dots++; locateApple(); }
    }

    private void move() {
        for (int i = dots; i > 0; i--) { x[i] = x[i - 1]; y[i] = y[i - 1]; }
        if (leftDirection) x[0] -= DOT_SIZE;
        if (rightDirection) x[0] += DOT_SIZE;
        if (upDirection) y[0] -= DOT_SIZE;
        if (downDirection) y[0] += DOT_SIZE;
    }

    private void checkCollision() {
        for (int i = dots; i > 0; i--) if ((i > 4) && (x[0] == x[i]) && (y[0] == y[i])) inGame = false;
        if (x[0] >= BOARD_WIDTH || x[0] < 0 || y[0] >= BOARD_HEIGHT || y[0] < 0) inGame = false;
        if (!inGame && timer != null) timer.stop();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (inGame) {
            checkApple();
            checkCollision();
            move();
            gameTime++;
        }
        repaint();
    }

    private class FieldKeyListener extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_SPACE && !inGame) { initGame(); repaint(); }
            if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) { leftDirection = true; upDirection = false; downDirection = false; }
            if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) { rightDirection = true; upDirection = false; downDirection = false; }
            if ((key == KeyEvent.VK_UP) && (!downDirection)) { upDirection = true; rightDirection = false; leftDirection = false; }
            if ((key == KeyEvent.VK_DOWN) && (!upDirection)) { downDirection = true; rightDirection = false; leftDirection = false; }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.add(new MainScene());
        frame.setResizable(false);
        frame.pack();
        frame.setTitle("Snake Game");
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}