package fcu.web;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class GraphPathFinder extends JFrame {
    private JTextField vertexField, edgeField, startField, endField;
    private JTextArea resultArea;
    private JButton addEdgeButton, drawGraphButton, dfsButton, bfsButton;
    private Graph graph;
    private int vertices;
    private java.util.List<int[]> edges = new ArrayList<>();
    private GraphPanel graphPanel;

    public GraphPathFinder() {
        setTitle("圖形路徑尋找器");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(5, 2));
        inputPanel.add(new JLabel("頂點數量:"));
        vertexField = new JTextField();
        inputPanel.add(vertexField);
        inputPanel.add(new JLabel("邊 (格式: 起點 終點):"));
        edgeField = new JTextField();
        inputPanel.add(edgeField);
        addEdgeButton = new JButton("添加邊");
        inputPanel.add(addEdgeButton);
        drawGraphButton = new JButton("繪製圖形");
        inputPanel.add(drawGraphButton);
        inputPanel.add(new JLabel("起點:"));
        startField = new JTextField();
        inputPanel.add(startField);
        inputPanel.add(new JLabel("終點:"));
        endField = new JTextField();
        inputPanel.add(endField);

        JPanel buttonPanel = new JPanel();
        dfsButton = new JButton("DFS 尋找最短路徑");
        bfsButton = new JButton("BFS 尋找最短路徑");
        buttonPanel.add(dfsButton);
        buttonPanel.add(bfsButton);

        resultArea = new JTextArea();
        resultArea.setEditable(false);

        graphPanel = new GraphPanel();

        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(resultArea), BorderLayout.EAST);
        add(graphPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        addEdgeButton.addActionListener(e -> addEdge());
        drawGraphButton.addActionListener(e -> drawGraph());
        dfsButton.addActionListener(e -> findShortestPath("DFS"));
        bfsButton.addActionListener(e -> findShortestPath("BFS"));
    }

    private void addEdge() {
        String[] parts = edgeField.getText().split(" ");
        if (parts.length == 2) {
            int v = Integer.parseInt(parts[0]);
            int w = Integer.parseInt(parts[1]);
            edges.add(new int[]{v, w});
            edgeField.setText("");
            resultArea.append("添加邊: " + v + " - " + w + "\n");
        }
    }

    private void drawGraph() {
        vertices = Integer.parseInt(vertexField.getText());
        graph = new Graph(vertices);
        for (int[] edge : edges) {
            graph.addEdge(edge[0], edge[1]);
        }
        resultArea.append("圖形已繪製，共 " + vertices + " 個頂點\n");
        graphPanel.setGraph(graph);
        graphPanel.repaint();
    }

    private void findShortestPath(String method) {
        int start = Integer.parseInt(startField.getText());
        int end = Integer.parseInt(endField.getText());
        long startTime = System.nanoTime();
        java.util.List<Integer> path;
        if (method.equals("DFS")) {
            path = graph.dfs(start, end);
        } else {
            path = graph.bfs(start, end);
        }
        long endTime = System.nanoTime();
        long duration = endTime - startTime;

        if (path.isEmpty()) {
            resultArea.append("未找到路徑\n");
        } else {
            resultArea.append(method + " 最短路徑: " + path + "\n");
            resultArea.append("執行時間: " + duration + " 奈秒\n");
            graphPanel.setPath(path);
            graphPanel.repaint();
        }
    }

    private class Graph {
        private int V;
        private LinkedList<Integer>[] adj;

        Graph(int v) {
            V = v;
            adj = new LinkedList[v];
            for (int i = 0; i < v; ++i)
                adj[i] = new LinkedList<>();
        }

        void addEdge(int v, int w) {
            adj[v].add(w);
            adj[w].add(v); // 假設是無向圖
        }

        java.util.List<Integer> dfs(int start, int end) {
            boolean[] visited = new boolean[V];
            java.util.List<Integer> path = new ArrayList<>();
            dfsUtil(start, end, visited, path);
            return path;
        }

        boolean dfsUtil(int v, int end, boolean[] visited, java.util.List<Integer> path) {
            visited[v] = true;
            path.add(v);

            if (v == end) return true;

            for (int n : adj[v]) {
                if (!visited[n]) {
                    if (dfsUtil(n, end, visited, path)) return true;
                }
            }

            path.remove(path.size() - 1);
            return false;
        }

        java.util.List<Integer> bfs(int start, int end) {
            boolean[] visited = new boolean[V];
            int[] parent = new int[V];
            Arrays.fill(parent, -1);

            LinkedList<Integer> queue = new LinkedList<>();
            visited[start] = true;
            queue.add(start);

            while (!queue.isEmpty()) {
                int v = queue.poll();
                if (v == end) break;

                for (int n : adj[v]) {
                    if (!visited[n]) {
                        visited[n] = true;
                        parent[n] = v;
                        queue.add(n);
                    }
                }
            }

            return reconstructPath(parent, start, end);
        }

        private java.util.List<Integer> reconstructPath(int[] parent, int start, int end) {
            java.util.List<Integer> path = new ArrayList<>();
            for (int at = end; at != -1; at = parent[at]) {
                path.add(at);
            }
            Collections.reverse(path);
            return path.get(0) == start ? path : new ArrayList<>();
        }
    }

    private class GraphPanel extends JPanel {
        private Graph graph;
        private java.util.List<Integer> path;

        public void setGraph(Graph graph) {
            this.graph = graph;
            this.path = null;
        }

        public void setPath(java.util.List<Integer> path) {
            this.path = path;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (graph == null) return;

            int width = getWidth();
            int height = getHeight();
            int radius = 20;

            // 計算頂點位置
            Point[] points = new Point[graph.V];
            for (int i = 0; i < graph.V; i++) {
                int x = width / 2 + (int) (Math.cos(2 * Math.PI * i / graph.V) * (width / 3));
                int y = height / 2 + (int) (Math.sin(2 * Math.PI * i / graph.V) * (height / 3));
                points[i] = new Point(x, y);
            }

            // 繪製邊
            g.setColor(Color.BLACK);
            for (int i = 0; i < graph.V; i++) {
                for (int j : graph.adj[i]) {
                    g.drawLine(points[i].x, points[i].y, points[j].x, points[j].y);
                }
            }

            // 繪製頂點
            for (int i = 0; i < graph.V; i++) {
                g.setColor(Color.WHITE);
                g.fillOval(points[i].x - radius, points[i].y - radius, 2 * radius, 2 * radius);
                g.setColor(Color.BLACK);
                g.drawOval(points[i].x - radius, points[i].y - radius, 2 * radius, 2 * radius);
                g.drawString(Integer.toString(i), points[i].x - 5, points[i].y + 5);
            }

            // 繪製路徑
            if (path != null && !path.isEmpty()) {
                g.setColor(Color.RED);
                for (int i = 0; i < path.size() - 1; i++) {
                    int v = path.get(i);
                    int w = path.get(i + 1);
                    g.drawLine(points[v].x, points[v].y, points[w].x, points[w].y);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GraphPathFinder().setVisible(true));
    }
}