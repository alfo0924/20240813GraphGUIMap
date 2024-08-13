import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class RandomRoadMapGUI extends JFrame {
    private static final int MAP_SIZE = 600;
    private static final int NODE_SIZE = 20;
    private static final int NODE_COUNT = 10;

    private ArrayList<Point> nodes;
    private ArrayList<int[]> edges;
    private JPanel mapPanel;
    private JButton generateButton;
    private JButton shortestPathButton;
    private int startNode = -1;
    private int endNode = -1;
    private ArrayList<Integer> shortestPath;

    public RandomRoadMapGUI() {
        setTitle("Random Road Map Generator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        mapPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawMap(g);
            }
        };
        mapPanel.setPreferredSize(new Dimension(MAP_SIZE, MAP_SIZE));
        mapPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectNode(e.getX(), e.getY());
            }
        });

        JPanel buttonPanel = new JPanel();
        generateButton = new JButton("Generate New Map");
        generateButton.addActionListener(e -> generateMap());
        shortestPathButton = new JButton("Find Shortest Path");
        shortestPathButton.addActionListener(e -> findShortestPath());

        buttonPanel.add(generateButton);
        buttonPanel.add(shortestPathButton);

        add(mapPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        generateMap();
    }

    private void generateMap() {
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
        Random random = new Random();

        // Generate nodes
        for (int i = 0; i < NODE_COUNT; i++) {
            int x = random.nextInt(MAP_SIZE - NODE_SIZE);
            int y = random.nextInt(MAP_SIZE - NODE_SIZE);
            nodes.add(new Point(x, y));
        }

        // Generate edges
        for (int i = 0; i < NODE_COUNT; i++) {
            for (int j = i + 1; j < NODE_COUNT; j++) {
                if (random.nextDouble() < 0.3) { // 30% chance to create an edge
                    edges.add(new int[]{i, j});
                }
            }
        }

        startNode = -1;
        endNode = -1;
        shortestPath = null;
        mapPanel.repaint();
    }

    private void drawMap(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw edges
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1));
        for (int[] edge : edges) {
            Point start = nodes.get(edge[0]);
            Point end = nodes.get(edge[1]);
            g2d.drawLine(start.x + NODE_SIZE/2, start.y + NODE_SIZE/2,
                    end.x + NODE_SIZE/2, end.y + NODE_SIZE/2);
        }

        // Draw shortest path
        if (shortestPath != null) {
            g2d.setColor(Color.ORANGE);
            g2d.setStroke(new BasicStroke(3));
            for (int i = 0; i < shortestPath.size() - 1; i++) {
                Point start = nodes.get(shortestPath.get(i));
                Point end = nodes.get(shortestPath.get(i + 1));
                g2d.drawLine(start.x + NODE_SIZE/2, start.y + NODE_SIZE/2,
                        end.x + NODE_SIZE/2, end.y + NODE_SIZE/2);

                // Draw path order
                int midX = (start.x + end.x) / 2;
                int midY = (start.y + end.y) / 2;
                g2d.setColor(Color.WHITE);
                g2d.fillOval(midX - 10, midY - 10, 20, 20);
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, 12));
                g2d.drawString(Integer.toString(i + 1), midX - 4, midY + 4);
                g2d.setColor(Color.ORANGE);
            }
        }

        // Draw nodes
        for (int i = 0; i < nodes.size(); i++) {
            Point node = nodes.get(i);
            if (i == startNode) {
                g2d.setColor(Color.GREEN);
            } else if (i == endNode) {
                g2d.setColor(Color.RED);
            } else {
                g2d.setColor(Color.BLUE);
            }
            g2d.fillOval(node.x, node.y, NODE_SIZE, NODE_SIZE);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString(Integer.toString(i), node.x + 6, node.y + 15);
        }
    }

    private void selectNode(int x, int y) {
        for (int i = 0; i < nodes.size(); i++) {
            Point node = nodes.get(i);
            if (x >= node.x && x <= node.x + NODE_SIZE &&
                    y >= node.y && y <= node.y + NODE_SIZE) {
                if (startNode == -1) {
                    startNode = i;
                } else if (endNode == -1) {
                    endNode = i;
                    findShortestPath();
                } else {
                    startNode = i;
                    endNode = -1;
                    shortestPath = null;
                }
                mapPanel.repaint();
                break;
            }
        }
    }

    private void findShortestPath() {
        if (startNode == -1 || endNode == -1) return;

        // Dijkstra's algorithm
        int[] distances = new int[NODE_COUNT];
        int[] previousNodes = new int[NODE_COUNT];
        boolean[] visited = new boolean[NODE_COUNT];
        Arrays.fill(distances, Integer.MAX_VALUE);
        distances[startNode] = 0;

        for (int i = 0; i < NODE_COUNT; i++) {
            int current = -1;
            for (int j = 0; j < NODE_COUNT; j++) {
                if (!visited[j] && (current == -1 || distances[j] < distances[current])) {
                    current = j;
                }
            }

            if (distances[current] == Integer.MAX_VALUE) break;

            visited[current] = true;

            for (int[] edge : edges) {
                int neighbor = -1;
                if (edge[0] == current) neighbor = edge[1];
                if (edge[1] == current) neighbor = edge[0];

                if (neighbor != -1 && !visited[neighbor]) {
                    int distance = distances[current] + 1; // Assuming all edges have weight 1
                    if (distance < distances[neighbor]) {
                        distances[neighbor] = distance;
                        previousNodes[neighbor] = current;
                    }
                }
            }
        }

        // Reconstruct path
        shortestPath = new ArrayList<>();
        for (int at = endNode; at != -1; at = previousNodes[at]) {
            shortestPath.add(at);
        }
        Collections.reverse(shortestPath);
        mapPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RandomRoadMapGUI().setVisible(true));
    }
}