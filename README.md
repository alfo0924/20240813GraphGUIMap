# 主程式為 MazeGame.java

這是一個使用 Java Swing 實現的迷宮生成和路徑搜索遊戲。此遊戲可以生成隨機迷宮並使用多種算法來進行迷宮路徑搜索，並將結果視覺化展示。

## 主要功能

- 生成隨機迷宮
- 使用多種算法搜索迷宮路徑
- 視覺化顯示迷宮和搜索路徑
- 計算並顯示搜索時間

## 程式結構

- `MazeGame` 類：主要遊戲類，繼承自 `JFrame`
- `Node` 內部類：用於 A* 算法的節點表示

### 主要方法

- `generateMaze()`: 生成隨機迷宮
- `search(String method)`: 根據選擇的方法執行搜索

### 各種搜索算法的實現方法

1. **深度優先搜索 (DFS)**
   - 方法：`dfs(int x, int y)`
   - 特點：遞迴實現，深入探索直到找到出口或無路可走

2. **廣度優先搜索 (BFS)**
   - 方法：`bfs()`
   - 特點：使用隊列，逐層探索，保證找到最短路徑

3. **A* 算法**
   - 方法：`aStar()`
   - 特點：結合了最佳優先搜索和 Dijkstra 算法的特點，使用啟發式函數優化搜索

4. **迭代加深深度優先搜索 (IDDFS)**
   - 方法：`iddfs()` 和 `dfsWithDepthLimit()`
   - 特點：結合了 DFS 的空間效率和 BFS 的完備性，逐步增加搜索深度

5. **蟻群算法**
   - 方法：`antColonyOptimization()`
   - 特點：模擬蟻群行為，通過信息素迭代優化路徑

## 使用方法

1. 輸入迷宮大小（至少為 5）
2. 點擊 "生成迷宮" 按鈕
3. 選擇搜索算法按鈕執行搜索
4. 觀察視覺化結果和搜索時間

## 視覺化說明

- **黑色**: 墙壁
- **綠色**: 起點
- **紅色**: 終點
- **藍色**: 搜索路徑

## 注意事項

- 迷宮大小影響生成和搜索時間
- 不同算法在不同情況下表現各異
- 蟻群算法可能需要多次運行才能找到最優路徑
