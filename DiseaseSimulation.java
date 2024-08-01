import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.*;

public class DiseaseSimulation extends JFrame {
    private JPanel drawingPanel;
    private JButton startButton, pauseButton, resumeButton, aboutButton, reportButton;
    private JTextField populationField;
    private JSpinner immunitySpinner1, immunitySpinner2, immunitySpinner3, immunitySpinner4, immunitySpinner5;
    private Timer timer;
    private Person[] population;
    private JLabel cycleCounterLabel;
    private int cycleCount;
    private StringBuilder reportData;
    private int deathCount;
    private int recoveryCount;

    public DiseaseSimulation() {
        setTitle("Disease Spread Simulation");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initializeComponents();
    }

    private void initializeComponents() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(3, 3));

        controlPanel.add(new JLabel("Population Size:"));
        populationField = new JTextField("600");
        controlPanel.add(populationField);

        controlPanel.add(new JLabel("No Immunity (%):"));
        immunitySpinner1 = new JSpinner(new SpinnerNumberModel(100, 0, 100, 1));
        controlPanel.add(immunitySpinner1);

        controlPanel.add(new JLabel("One Shot (%):"));
        immunitySpinner2 = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        controlPanel.add(immunitySpinner2);

        controlPanel.add(new JLabel("Two Shots (%):"));
        immunitySpinner3 = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        controlPanel.add(immunitySpinner3);

        controlPanel.add(new JLabel("Three Shots (%):"));
        immunitySpinner4 = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        controlPanel.add(immunitySpinner4);

        controlPanel.add(new JLabel("Natural Immunity (%):"));
        immunitySpinner5 = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        controlPanel.add(immunitySpinner5);

        startButton = new JButton("Start");
        controlPanel.add(startButton);
        pauseButton = new JButton("Pause");
        controlPanel.add(pauseButton);
        resumeButton = new JButton("Resume");
        controlPanel.add(resumeButton);

        add(controlPanel, BorderLayout.NORTH);

        drawingPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (population != null) {
                    for (Person p : population) {
                        g.setColor(p.getColor());
                        g.fillOval(p.getxCoordinate(), p.getyCoordinate(), 10, 10);
                    }
                }
            }
        };
        add(drawingPanel, BorderLayout.CENTER);

        cycleCounterLabel = new JLabel("Cycles: 0");
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(cycleCounterLabel, BorderLayout.WEST);

        aboutButton = new JButton("About");
        bottomPanel.add(aboutButton, BorderLayout.EAST);

        reportButton = new JButton("Generate Report");
        bottomPanel.add(reportButton, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);

        startButton.addActionListener(e -> startSimulation());
        pauseButton.addActionListener(e -> timer.stop());
        resumeButton.addActionListener(e -> timer.start());

        aboutButton.addActionListener(e -> {
            String message = "<html><b>Pandemic Modeller - INFO3136 MOBILE DEV</b><br>August 1 2024<br>Created by: Evan Kwak, Dyllon Howard, and Jace Kendel.</html>";
            JOptionPane.showMessageDialog(this, message, "About", JOptionPane.INFORMATION_MESSAGE);
        });

        reportButton.addActionListener(e -> generateReport());
    }

    private void startSimulation() {
        int populationSize = Integer.parseInt(populationField.getText());
        int immunity1 = (int) immunitySpinner1.getValue();
        int immunity2 = (int) immunitySpinner2.getValue();
        int immunity3 = (int) immunitySpinner3.getValue();
        int immunity4 = (int) immunitySpinner4.getValue();
        int immunity5 = (int) immunitySpinner5.getValue();

        if (immunity1 + immunity2 + immunity3 + immunity4 + immunity5 != 100) {
            JOptionPane.showMessageDialog(this, "Immunity percentages must add up to 100%", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        population = new Person[populationSize];
        createPopulation(populationSize, immunity1, immunity2, immunity3, immunity4, immunity5);
        cycleCount = 0;
        deathCount = 0;
        recoveryCount = 0;
        reportData = new StringBuilder();
        appendReportData("Simulation started with the following parameters:\n");
        appendReportData("Population Size: " + populationSize + "\n");
        appendReportData("No Immunity (%): " + immunity1 + "\n");
        appendReportData("One Shot (%): " + immunity2 + "\n");
        appendReportData("Two Shots (%): " + immunity3 + "\n");
        appendReportData("Three Shots (%): " + immunity4 + "\n");
        appendReportData("Natural Immunity (%): " + immunity5 + "\n");
        appendReportData("\nCycle data:\n");

        timer = new Timer(200, (ActionEvent e) -> {
            for (Person p : population) {
                p.move();
                p.updateInfectionStatus();
            }
            checkCollisions();
            updateStatusCounts();
            drawingPanel.repaint();
            cycleCount++;
            cycleCounterLabel.setText("Cycles: " + cycleCount);
            appendReportData("Cycle " + cycleCount + ": Population status - " + getPopulationStatus() + "\n");
        });
        timer.start();
    }

    private void createPopulation(int size, int immunity1, int immunity2, int immunity3, int immunity4, int immunity5) {
        int[] immunities = {immunity1, immunity2, immunity3, immunity4, immunity5};
        Color[] colors = {Color.YELLOW, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.ORANGE};
        int index = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < size * immunities[i] / 100; j++) {
                population[index++] = new Person(i + 1, colors[i]);
            }
        }
        population[(int) (Math.random() * size)].setInfected(true);
    }

    private void checkCollisions() {
        for (int i = 0; i < population.length; i++) {
            for (int j = i + 1; j < population.length; j++) {
                population[i].checkCollision(population[j]);
            }
        }
    }

    private void updateStatusCounts() {
        deathCount = 0;
        recoveryCount = 0;
        for (Person p : population) {
            if (p.getColor() == Color.BLACK) {
                deathCount++;
            } else if (p.getColor() == Color.GREEN) {
                recoveryCount++;
            }
        }
    }

    private void appendReportData(String data) {
        reportData.append(data);
    }

    private String getPopulationStatus() {
        int infected = 0;
        int healthy = 0;
        for (Person p : population) {
            if (p.isInfected()) {
                infected++;
            } else if (p.getColor() == Color.YELLOW || p.getColor() == Color.BLUE || p.getColor() == Color.MAGENTA || p.getColor() == Color.ORANGE) {
                healthy++;
            }
        }
        return "Healthy: " + healthy + ", Infected: " + infected;
    }

    private void generateReport() {
        String fileName = JOptionPane.showInputDialog(this, "Enter filename:", "Save Report", JOptionPane.PLAIN_MESSAGE);
        if (fileName != null && !fileName.trim().isEmpty()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName + ".txt"))) {
                writer.write(reportData.toString());
                writer.write("\nFinal Summary:\n");
                writer.write("Deaths: " + deathCount + "\n");
                writer.write("Recoveries: " + recoveryCount + "\n");
                JOptionPane.showMessageDialog(this, "Report saved as " + fileName + ".txt", "Report Saved", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving report: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DiseaseSimulation app = new DiseaseSimulation();
            app.setVisible(true);
        });
    }
}
