import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.util.Timer;

public class GamePanel extends JPanel implements ActionListener {

    // Constants for screen dimensions and game units
    static final int SCREEN_WIDTH = 700;
    static final int SCREEN_HEIGHT = 700;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    static final int DELAY = 80; 

    // Arrays to store snake's body coordinates
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    
    int bodyParts = 5;          // Initial length of the snake
    int applesEaten;            // Number of apples eaten by the snake
    int appleX;                 // X-coordinate of the apple
    int appleY;                 // Y-coordinate of the apple
    char direction = 'D';       // Initial direction (D = Down)
    boolean running = false;    // Flag to indicate if the game is running
    Timer timer;                // Timer for game loop
    Random random;              // Random number generator

    // Constructor
    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(new Color(20, 20, 20));
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    // Start the game
    public void startGame() {
        newApple();
        running = true;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (running) {
                    move();
                    checkApple();
                    checkCollisions();
                }
                repaint();
            }
        }, 0, DELAY);
    }

    // Paint the game panel
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    // Draw the game components
    public void draw(Graphics g) {
        if (running) {
            // Draw the apple
            g.setColor(Color.gray);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            // Draw the snake's body
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(new Color(255, 255, 255));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(100, 100, 100));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }

            // Draw the score
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            FontMetrics metrics2 = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics2.stringWidth("Score: " + applesEaten)) / 2,
                    g.getFont().getSize());
        } else {
            gameOver(g);
        }
    }

    // Generate a new apple at a random location
    public void newApple() {
        appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    // Move the snake
    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    // Check if the snake has eaten the apple
    public void checkApple() {
        if (x[0] == appleX && y[0] == appleY) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    // Check for collisions (with walls and itself)
    public void checkCollisions() {
        if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < 0 || y[0] >= SCREEN_HEIGHT) {
            running = false; // Snake hit a wall, game over
        }

        for (int i = 1; i < bodyParts; i++) {
            if (x[i] == x[0] && y[i] == y[0]) {
                running = false; // Snake collided with itself, game over
            }
        }

        if (!running) {
            timer.cancel(); // Stop the game loop
        }
    }

    // Display game over screen
    public void gameOver(Graphics g) {
        g.setColor(Color.red);
        g.setFont(new Font("Arial", Font.BOLD, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics2.stringWidth("Score: " + applesEaten)) / 2,
                g.getFont().getSize());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    // KeyAdapter to handle user input
    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
                if (direction != 'R') direction = 'L';
            } else if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
                if (direction != 'L') direction = 'R';
            } else if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) {
                if (direction != 'D') direction = 'U';
            } else if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) {
                if (direction != 'U') direction = 'D';
            }
        }
    }
}
