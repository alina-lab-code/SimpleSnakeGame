import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Snake {
    private List<Point> body;
    private char direction;
    private boolean alive;

    public Snake() {
        body = new ArrayList<>();
        direction = 'R';
        alive = true;

        // Start with only 2 segments (Head + 1 Tail)
        for (int i = 0; i < 2; i++) {
            body.add(new Point((2 - i) * GameConstants.UNIT_SIZE, 0));
        }
    }

    public void move() {
        Point head = body.get(0);
        Point newHead = new Point(head.getX(), head.getY());

        switch (direction) {
            case 'U': newHead.setY(newHead.getY() - GameConstants.UNIT_SIZE); break;
            case 'D': newHead.setY(newHead.getY() + GameConstants.UNIT_SIZE); break;
            case 'L': newHead.setX(newHead.getX() - GameConstants.UNIT_SIZE); break;
            case 'R': newHead.setX(newHead.getX() + GameConstants.UNIT_SIZE); break;
        }

        body.add(0, newHead);
        body.remove(body.size() - 1);
    }

    public void grow() {
        Point tail = body.get(body.size() - 1);
        body.add(new Point(tail.getX(), tail.getY()));
    }

    public boolean checkCollision() {
        Point head = body.get(0);

        if (head.getX() < 0 || head.getX() >= GameConstants.BOARD_WIDTH ||
                head.getY() < 0 || head.getY() >= GameConstants.BOARD_HEIGHT) {
            alive = false;
            return true;
        }

        for (int i = 1; i < body.size(); i++) {
            if (head.equals(body.get(i))) {
                alive = false;
                return true;
            }
        }

        return false;
    }

    public boolean eat(Apple apple) {
        Point head = body.get(0);
        if (head.equals(apple.getPosition())) {
            grow();
            return true;
        }
        return false;
    }

    public void setDirection(char newDirection) {
        if ((direction == 'U' && newDirection != 'D') ||
                (direction == 'D' && newDirection != 'U') ||
                (direction == 'L' && newDirection != 'R') ||
                (direction == 'R' && newDirection != 'L')) {
            direction = newDirection;
        }
    }

    public void draw(Graphics g) {
        for (int i = 0; i < body.size(); i++) {
            Point p = body.get(i);
            if (i == 0) {
                g.setColor(Color.GREEN);
            } else {
                g.setColor(new Color(45, 180, 0));
            }
            g.fillRect(p.getX(), p.getY(), GameConstants.UNIT_SIZE, GameConstants.UNIT_SIZE);
        }
    }

    public List<Point> getBody() {
        return body;
    }

    public boolean isAlive() {
        return alive;
    }
}