import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class MainScene extends JPanel implements ActionListener {

    // Жесткие размеры игрового поля
    private final int BOARD_WIDTH = 600;
    private final int BOARD_HEIGHT = 600;

    // Крупный шаг сетки движения (40х40)
    private final int DOT_SIZE = 40;
    private final int ALL_DOTS = (BOARD_WIDTH * BOARD_HEIGHT) / (DOT_SIZE * DOT_SIZE);

    // Массивы для координат
    private final int[] x = new int[ALL_DOTS];
    private final int[] y = new int[ALL_DOTS];

    private int dots;
    private int appleX;
    private int appleY;

    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;

    private boolean inGame = true;
    private Timer timer;

    // Картинки элементов игры
    private Image snakeHead;
    private Image snakeHeadOpen;
    private Image snakeBody;
    private Image apple;

    // Переменная для фона поляны
    private Image backgroundField;

    public MainScene() {
        setBackground(Color.black);
        setFocusable(true);
        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        addKeyListener(new FieldKeyListener());

        loadImages();
        initGame();
    }

    private void loadImages() {
        try {
            // Загружаем оригиналы без предварительного обрезания размера
            snakeHead = new ImageIcon("resources/head.png").getImage();
            snakeHeadOpen = new ImageIcon("resources/openMouth.png").getImage();
            snakeBody = new ImageIcon("resources/bodyPart.png").getImage();
            apple = new ImageIcon("resources/apple.png").getImage();

            // Загружаем картинку пастельной поляны
            backgroundField = new ImageIcon("resources/background.jpg").getImage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initGame() {
        rightDirection = true;
        leftDirection = false;
        upDirection = false;
        downDirection = false;

        dots = 3;

        // Синхронный спавн точек строго по сетке 40 в ряд
        for (int i = 0; i < dots; i++) {
            x[i] = 240 - (i * DOT_SIZE);
            y[i] = 240;
        }

        locateApple();
        inGame = true;

        if (timer != null) {
            timer.stop();
        }
        timer = new Timer(220, this);
        timer.start();
    }

    private void locateApple() {
        Random rand = new Random();
        int maxCellsX = BOARD_WIDTH / DOT_SIZE;
        int maxCellsY = BOARD_HEIGHT / DOT_SIZE;

        int r = rand.nextInt(maxCellsX - 2) + 1;
        appleX = r * DOT_SIZE;
        r = rand.nextInt(maxCellsY - 2) + 1;
        appleY = r * DOT_SIZE;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        if (inGame) {
            // ВАЖНО: Рисуем фон поляны самым первым слоем на всё игровое окно (600х600)
            if (backgroundField != null) {
                g.drawImage(backgroundField, 0, 0, BOARD_WIDTH, BOARD_HEIGHT, this);
            }

            // 1. Отрисовка яблока поверх травы
            if (apple != null) {
                g.drawImage(apple, appleX, appleY, DOT_SIZE, DOT_SIZE, this);
            }

            // РАЗМЕР ДЛЯ СШИВАНИЯ СЛОЕВ:
            int layerSize = 52;
            int offset = 6;

            // 2. Рисуем ТЕЛО с конца (от хвоста к шее) с автоповоротом сегментов
            for (int i = dots - 1; i > 0; i--) {
                if (snakeBody != null) {
                    Graphics2D gBody = (Graphics2D) g.create();
                    gBody.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    gBody.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                    int centerX = x[i] + DOT_SIZE / 2;
                    int centerY = y[i] + DOT_SIZE / 2;

                    // Вычисляем направление конкретного сегмента относительно идущего впереди
                    if (y[i] > y[i - 1]) {
                        gBody.rotate(Math.toRadians(-90), centerX, centerY); // Движение вверх
                    } else if (y[i] < y[i - 1]) {
                        gBody.rotate(Math.toRadians(90), centerX, centerY);  // Движение вниз
                    } else if (x[i] > x[i - 1]) {
                        gBody.rotate(Math.toRadians(180), centerX, centerY); // Движение влево
                    }

                    gBody.drawImage(snakeBody, x[i] - offset, y[i] - offset, layerSize, layerSize, this);
                    gBody.dispose();
                }
            }

            // 3. Рисуем ГОЛОВУ на самом верхнем слое с автоповоротом
            if (dots > 0) {
                boolean isNearAppleX = Math.abs(x[0] - appleX) <= DOT_SIZE;
                boolean isNearAppleY = Math.abs(y[0] - appleY) <= DOT_SIZE;

                Image currentHead = (isNearAppleX && isNearAppleY && snakeHeadOpen != null) ? snakeHeadOpen : snakeHead;

                if (currentHead != null) {
                    Graphics2D gHead = (Graphics2D) g.create();
                    gHead.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    gHead.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                    int headCenterX = x[0] + DOT_SIZE / 2;
                    int headCenterY = y[0] + DOT_SIZE / 2;

                    if (upDirection) {
                        gHead.rotate(Math.toRadians(-90), headCenterX, headCenterY);
                    } else if (downDirection) {
                        gHead.rotate(Math.toRadians(90), headCenterX, headCenterY);
                    } else if (leftDirection) {
                        gHead.rotate(Math.toRadians(180), headCenterX, headCenterY);
                    }

                    gHead.drawImage(currentHead, x[0] - offset, y[0] - offset, layerSize, layerSize, this);
                    gHead.dispose();
                }
            }

            Toolkit.getDefaultToolkit().sync();
        } else {
            gameOver(g);
        }
    }

    private void gameOver(Graphics g) {
        String msg = "Game Over";
        String hint = "Press SPACE to Restart";

        Font font = new Font("Helvetica", Font.BOLD, 28);
        Font hintFont = new Font("Helvetica", Font.PLAIN, 16);
        FontMetrics metrics = getFontMetrics(font);
        FontMetrics hintMetrics = getFontMetrics(hintFont);

        g.setColor(Color.white);
        g.setFont(font);
        g.drawString(msg, (BOARD_WIDTH - metrics.stringWidth(msg)) / 2, BOARD_HEIGHT / 2 - 10);

        g.setColor(Color.gray);
        g.setFont(hintFont);
        g.drawString(hint, (BOARD_WIDTH - hintMetrics.stringWidth(hint)) / 2, BOARD_HEIGHT / 2 + 30);
    }

    private void checkApple() {
        if (x[0] == appleX && y[0] == appleY) {
            dots++;
            locateApple();
        }
    }

    private void move() {
        for (int i = dots; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        if (leftDirection)  x[0] -= DOT_SIZE;
        if (rightDirection) x[0] += DOT_SIZE;
        if (upDirection)    y[0] -= DOT_SIZE;
        if (downDirection)  y[0] += DOT_SIZE;
    }

    private void checkCollision() {
        for (int i = dots; i > 0; i--) {
            if ((i > 4) && (x[0] == x[i]) && (y[0] == y[i])) {
                inGame = false;
            }
        }

        if (x[0] >= BOARD_WIDTH || x[0] < 0 || y[0] >= BOARD_HEIGHT || y[0] < 0) {
            inGame = false;
        }

        if (!inGame) {
            if (timer != null) timer.stop();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (inGame) {
            checkApple();
            checkCollision();
            move();
        }
        repaint();
    }

    private class FieldKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_SPACE && !inGame) {
                initGame();
                repaint();
            }

            if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
                leftDirection = true; upDirection = false; downDirection = false;
            }
            if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
                rightDirection = true; upDirection = false; downDirection = false;
            }
            if ((key == KeyEvent.VK_UP) && (!downDirection)) {
                upDirection = true; rightDirection = false; leftDirection = false;
            }
            if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
                downDirection = true; rightDirection = false; leftDirection = false;
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        MainScene game = new MainScene();

        frame.add(game);
        frame.setResizable(false);
        frame.pack();

        frame.setTitle("Snake Game");
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}