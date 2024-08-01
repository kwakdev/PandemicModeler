import java.awt.Color;
import java.util.Random;

public class Person {
    private boolean isAlive;
    private boolean isInfected;
    private int immunityStatus;
    private Color color;
    private int xCoordinate;
    private int yCoordinate;
    private int xIncrementValue;
    private int yIncrementValue;
    private int cycleCounter;
    private static final int MAX_CYCLE = 150;
    private static final int MAX_MOVE = 5;
    private Random rand;

    public Person(int immunityStatus, Color color) {
        this.isAlive = true;
        this.isInfected = false;
        this.immunityStatus = immunityStatus;
        this.color = color;
        this.rand = new Random();
        this.xCoordinate = rand.nextInt(800); // Assuming 800x600 drawing panel
        this.yCoordinate = rand.nextInt(600);
        this.xIncrementValue = rand.nextInt(MAX_MOVE * 2) - MAX_MOVE;
        this.yIncrementValue = rand.nextInt(MAX_MOVE * 2) - MAX_MOVE;
        this.cycleCounter = 0;
    }

    public void move() {
        if (isAlive) {
            xCoordinate += xIncrementValue;
            yCoordinate += yIncrementValue;
            // Ensure the person stays within bounds
            if (xCoordinate < 0 || xCoordinate > 800) xIncrementValue *= -1;
            if (yCoordinate < 0 || yCoordinate > 600) yIncrementValue *= -1;
        }
    }

    public void checkCollision(Person other) {
        if (isAlive && other.isAlive && this != other) {
            int dx = this.xCoordinate - other.xCoordinate;
            int dy = this.yCoordinate - other.yCoordinate;
            if (Math.sqrt(dx * dx + dy * dy) < 10) { // Assuming radius of 5 pixels
                // Handle collision
                this.xIncrementValue = rand.nextInt(MAX_MOVE * 2) - MAX_MOVE;
                this.yIncrementValue = rand.nextInt(MAX_MOVE * 2) - MAX_MOVE;
                other.xIncrementValue = rand.nextInt(MAX_MOVE * 2) - MAX_MOVE;
                other.yIncrementValue = rand.nextInt(MAX_MOVE * 2) - MAX_MOVE;
                if (this.isInfected && !other.isInfected) {
                    other.attemptInfection(this);
                }
                if (!this.isInfected && other.isInfected) {
                    this.attemptInfection(other);
                }
            }
        }
    }

    public void attemptInfection(Person infected) {
        int chance = 0;
        switch (this.immunityStatus) {
            case 1: chance = 80; break;
            case 2: chance = 60; break;
            case 3: chance = 30; break;
            case 4: chance = 10; break;
            case 5: chance = 40; break;
        }
        if (rand.nextInt(100) < chance) {
            this.isInfected = true;
            this.color = Color.RED;
        }
    }

    public void updateInfectionStatus() {
        if (isInfected) {
            cycleCounter++;
            if (cycleCounter > MAX_CYCLE) {
                this.isInfected = false;
                this.color = Color.GREEN;
                updateDeathStatus();
            }
        }
    }

    private void updateDeathStatus() {
        int chance = 0;
        switch (this.immunityStatus) {
            case 1: chance = 10; break;
            case 2: chance = 7; break;
            case 3: chance = 3; break;
            case 4: chance = 1; break;
            case 5: chance = 3; break;
        }
        if (rand.nextInt(100) < chance) {
            this.isAlive = false;
            this.color = Color.BLACK;
            this.xIncrementValue = 0;
            this.yIncrementValue = 0;
        }
    }

    // Getters and Setters
    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public boolean isInfected() {
        return isInfected;
    }

    public void setInfected(boolean infected) {
        isInfected = infected;
        this.color = infected ? Color.RED : this.color;
    }

    public int getImmunityStatus() {
        return immunityStatus;
    }

    public void setImmunityStatus(int immunityStatus) {
        this.immunityStatus = immunityStatus;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getxCoordinate() {
        return xCoordinate;
    }

    public void setxCoordinate(int xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public int getyCoordinate() {
        return yCoordinate;
    }

    public void setyCoordinate(int yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    public int getxIncrementValue() {
        return xIncrementValue;
    }

    public void setxIncrementValue(int xIncrementValue) {
        this.xIncrementValue = xIncrementValue;
    }

    public int getyIncrementValue() {
        return yIncrementValue;
    }

    public void setyIncrementValue(int yIncrementValue) {
        this.yIncrementValue = yIncrementValue;
    }

    public int getCycleCounter() {
        return cycleCounter;
    }

    public void setCycleCounter(int cycleCounter) {
        this.cycleCounter = cycleCounter;
    }
}
