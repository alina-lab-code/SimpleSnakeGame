import java.awt.*;
import java.util.Random;

public class Apple {
    private Point position;
    private Random random;
    private Image appleImage;

    public Apple(Image appleImage) {
        random = new Random();
        position = new Point(0, 0);
        this.appleImage = appleImage;
    }

    public void generateNewPosition(Snake snake) {
        int newX, newY;
        boolean validPosition;

        do {
            validPosition = true;
            newX = random.nextInt(GameConstants.BOARD_WIDTH / GameConstants.UNIT_SIZE) * GameConstants.UNIT_SIZE;
            newY = random.nextInt(GameConstants.BOARD_HEIGHT / GameConstants.UNIT_SIZE) * GameConstants.UNIT_SIZE;

            for (Point p : snake.getBody()) {
                if (p.getX() == newX && p.getY() == newY) {
                    validPosition = false;
                    break;
                }
            }
        } while (!validPosition);

        position.setX(newX);
        position.setY(newY);
    }

    public void draw(Graphics g) {
        if (appleImage != null) {
            g.drawImage(appleImage, position.getX(), position.getY(),
                    GameConstants.UNIT_SIZE, GameConstants.UNIT_SIZE, null);
        } else {
            g.setColor(Color.RED);
            g.fillOval(position.getX(), position.getY(), GameConstants.UNIT_SIZE, GameConstants.UNIT_SIZE);
        }
    }

    public Point getPosition() {
        return position;
    }
}