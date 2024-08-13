package fcu.web;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class MazeGame extends JFrame {
    private int size;
    private int[][] maze;
    private int startX, startY, endX, endY;
    private JPanel mazePanel;
    private JTextField sizeInput;
    private JButton generateButton, dfsButton, bfsButton, aStarButton, iddfsButton;
    private JLabel timeLabel;

    public MazeGame() {
        setTitle("D1204433 林俊傑 迷宮小遊戲");
        setSize(600, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();
        sizeInput = new JTextField(5);
        generateButton = new JButton("生成迷宮");
        dfsButton = new JButton("DFS搜索");
        bfsButton = new JButton("BFS搜索");
        aStarButton = new JButton("A*搜索");
        iddfsButton = new JButton("IDDFS搜索");
        timeLabel = new JLabel("搜索時間: ");

        controlPanel.add(new JLabel("迷宮大小: "));
        controlPanel.add(sizeInput);
        controlPanel.add(generateButton);
        controlPanel.add(dfsButton);
        controlPanel.add(bfsButton);
        controlPanel.add(aStarButton);
        controlPanel.add(iddfsButton);
        controlPanel.add(timeLabel);

        add(controlPanel, BorderLayout.NORTH);

        mazePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (maze != null) {
                    int cellSize = Math.min(getWidth(), getHeight()) / size;
                    for (int i = 0; i < size; i++) {
                        for (int j = 0; j < size; j++) {
                            if (maze[i][j] == 1) {
                                g.setColor(Color.BLACK);
                                g.fillRect(j * cellSize, i * cellSize, cellSize, cellSize);
                            } else if (i == startX && j == startY) {
                                g.setColor(Color.GREEN);
                                g.fillRect(j * cellSize, i * cellSize, cellSize, cellSize);
                            } else if (i == endX && j == endY) {
                                g.setColor(Color.RED);
                                g.fillRect(j * cellSize, i * cellSize, cellSize, cellSize);
                            } else if (maze[i][j] == 2) {
                                g.setColor(Color.BLUE);
                                g.fillRect(j * cellSize, i * cellSize, cellSize, cellSize);
                            }
                        }
                    }
                }
            }
        };

        add(mazePanel, BorderLayout.CENTER);

        generateButton.addActionListener(e -> generateMaze());
        dfsButton.addActionListener(e -> search("DFS"));
        bfsButton.addActionListener(e -> search("BFS"));
        aStarButton.addActionListener(e -> search("A*"));
        iddfsButton.addActionListener(e -> search("IDDFS"));

        setVisible(true);
    }

    private void generateMaze() {
        try {
            size = Integer.parseInt(sizeInput.getText());
            if (size < 5) {
                JOptionPane.showMessageDialog(this, "迷宮大小必須至少為5");
                return;
            }
            maze = new int[size][size];
            for (int i = 0; i < size; i++) {
                Arrays.fill(maze[i], 1);
            }

            Random rand = new Random();
            startX = rand.nextInt(size);
            startY = rand.nextInt(size);
            do {
                endX = rand.nextInt(size);
                endY = rand.nextInt(size);
            } while (endX == startX && endY == startY);

            generatePath(startX, startY);

            maze[startX][startY] = 0;
            maze[endX][endY] = 0;

            mazePanel.repaint();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "請輸入有效的迷宮大小");
        }
    }

    private void generatePath(int x, int y) {
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        Collections.shuffle(Arrays.asList(directions));

        for (int[] dir : directions) {
            int nx = x + dir[0] * 2;
            int ny = y + dir[1] * 2;

            if (nx >= 0 && nx < size && ny >= 0 && ny < size && maze[nx][ny] == 1) {
                maze[x + dir[0]][y + dir[1]] = 0;
                maze[nx][ny] = 0;
                generatePath(nx, ny);
            }
        }
    }

    private void search(String method) {
        if (maze == null) {
            JOptionPane.showMessageDialog(this, "請先生成迷宮");
            return;
        }

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (maze[i][j] == 2) maze[i][j] = 0;
            }
        }

        long startTime = System.nanoTime();
        boolean found = false;
        switch (method) {
            case "DFS":
                found = dfs(startX, startY);
                break;
            case "BFS":
                found = bfs();
                break;
            case "A*":
                found = aStar();
                break;
            case "IDDFS":
                found = iddfs();
                break;
        }
        long endTime = System.nanoTime();

        if (found) {
            long duration = endTime - startTime;
            timeLabel.setText("搜索時間: " + duration + " 奈秒");
            JOptionPane.showMessageDialog(this, "找到路徑！");
        } else {
            timeLabel.setText("未找到路徑");
            JOptionPane.showMessageDialog(this, "未找到路徑。請嘗試重新生成迷宮。");
        }

        mazePanel.repaint();
    }

    private boolean dfs(int x, int y) {
        if (x < 0 || x >= size || y < 0 || y >= size || maze[x][y] == 1 || maze[x][y] == 2) return false;
        if (x == endX && y == endY) return true;

        maze[x][y] = 2;

        if (dfs(x + 1, y) || dfs(x - 1, y) || dfs(x, y + 1) || dfs(x, y - 1)) return true;

        maze[x][y] = 0;
        return false;
    }

    private boolean bfs() {
        Queue<int[]> queue = new LinkedList<>();
        boolean[][] visited = new boolean[size][size];
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        queue.offer(new int[]{startX, startY});
        visited[startX][startY] = true;

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int x = curr[0], y = curr[1];

            if (x == endX && y == endY) return true;

            for (int[] dir : dirs) {
                int nx = x + dir[0], ny = y + dir[1];
                if (nx >= 0 && nx < size && ny >= 0 && ny < size && maze[nx][ny] != 1 && !visited[nx][ny]) {
                    queue.offer(new int[]{nx, ny});
                    visited[nx][ny] = true;
                    maze[nx][ny] = 2;
                }
            }
        }

        return false;
    }

    private boolean aStar() {
        PriorityQueue<Node> openList = new PriorityQueue<>();
        boolean[][] closedList = new boolean[size][size];
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        openList.offer(new Node(startX, startY, 0, heuristic(startX, startY)));

        while (!openList.isEmpty()) {
            Node current = openList.poll();
            int x = current.x, y = current.y;

            if (x == endX && y == endY) {
                while (current != null) {
                    maze[current.x][current.y] = 2;
                    current = current.parent;
                }
                return true;
            }

            closedList[x][y] = true;

            for (int[] dir : dirs) {
                int nx = x + dir[0], ny = y + dir[1];
                if (nx >= 0 && nx < size && ny >= 0 && ny < size && maze[nx][ny] != 1 && !closedList[nx][ny]) {
                    Node neighbor = new Node(nx, ny, current.g + 1, heuristic(nx, ny));
                    neighbor.parent = current;
                    if (!openList.contains(neighbor)) {
                        openList.offer(neighbor);
                    }
                }
            }
        }

        return false;
    }

    private int heuristic(int x, int y) {
        return Math.abs(x - endX) + Math.abs(y - endY);
    }

    private boolean iddfs() {
        for (int depth = 0; depth < size * size; depth++) {
            boolean[][] visited = new boolean[size][size];
            if (dfsWithDepthLimit(startX, startY, depth, visited)) {
                return true;
            }
        }
        return false;
    }

    private boolean dfsWithDepthLimit(int x, int y, int depth, boolean[][] visited) {
        if (x < 0 || x >= size || y < 0 || y >= size || maze[x][y] == 1 || visited[x][y]) return false;
        if (x == endX && y == endY) {
            maze[x][y] = 2;
            return true;
        }
        if (depth == 0) return false;

        visited[x][y] = true;
        maze[x][y] = 2;

        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] dir : dirs) {
            int nx = x + dir[0], ny = y + dir[1];
            if (dfsWithDepthLimit(nx, ny, depth - 1, visited)) {
                return true;
            }
        }

        maze[x][y] = 0;
        return false;
    }

    private class Node implements Comparable<Node> {
        int x, y, g, h;
        Node parent;

        Node(int x, int y, int g, int h) {
            this.x = x;
            this.y = y;
            this.g = g;
            this.h = h;
        }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.g + this.h, other.g + other.h);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MazeGame::new);
    }
}