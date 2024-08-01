import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DiseaseSimulation extends JFrame {
    private JPanel drawingPanel;
    private JButton startButton, pauseButton, resumeButton;
    private JTextField populationField;
    private JSpinner immunitySpinner1, immunitySpinner2, immunitySpinner3, immunitySpinner4, immunitySpinner5;
    private Timer timer;
    private Person[] population;

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

        startButton.addActionListener(e -> startSimulation());
        pauseButton.addActionListener(e -> timer.stop());
        resumeButton.addActionListener(e -> timer.start());
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

        timer = new Timer(200, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (Person p : population) {
                    p.move();
                    p.updateInfectionStatus();
                }
                checkCollisions();
                drawingPanel.repaint();
            }
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
        // Infect one random person
        population[(int) (Math.random() * size)].setInfected(true);
    }

    private void checkCollisions() {
        for (int i = 0; i < population.length; i++) {
            for (int j = i + 1; j < population.length; j++) {
                population[i].checkCollision(population[j]);
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
