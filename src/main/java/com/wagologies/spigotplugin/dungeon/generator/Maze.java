package com.wagologies.spigotplugin.dungeon.generator;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

public class Maze {
    private final int width, height;
    // 0 = No Doors
    // 1 = North Door
    // 2 = East Door
    // 4 = South Door
    // 8 = West Door
    private final int[][] mazeWalls;

    public Maze(int width, int height) {
        this.width = width;
        this.height = height;

        mazeWalls = new int[height][width];
        generateMaze();
    }

    public void generateMaze() {
        Random random = new Random();
        List<Integer> visitedCells = new ArrayList<>();
        List<Integer> currentPath = new ArrayList<>();
        int index = random.nextInt(width * height);
        while(visitedCells.size() < width * height) {
            int x = index % width;
            int y = index / width;

            List<Integer> possibleNextCells = generateNextCells(index, visitedCells, currentPath);

            if(possibleNextCells.isEmpty()) {
                if(currentPath.isEmpty()) {
                    break;
                }
                int lastCellIndex = currentPath.removeLast();
                visitedCells.add(index);
                index = lastCellIndex;
                continue;
            }

            int nextIndex = possibleNextCells.get(random.nextInt(possibleNextCells.size()));
            int nextX = nextIndex % width;
            int nextY = nextIndex / width;
            mazeWalls[y][x] |= getDoorDirection(nextIndex, index);
            mazeWalls[nextY][nextX] |= getDoorDirection(index, nextIndex);

            currentPath.add(index);
            index = nextIndex;
        }
        int middleX = width / 2;
        mazeWalls[0][middleX] |= 1; // Add door in middle of north wall
    }

    private int getDoorDirection(int nextIndex, int index) {
        int x = index % width;
        int y = index / width;
        int nextX = nextIndex % width;
        int nextY = nextIndex / width;
        int deltaX = nextX - x;
        int deltaY = nextY - y;
        if(deltaX == 1) {
            return 2;
        } else if(deltaX == -1) {
            return 8;
        } else if(deltaY == 1) {
            return 4;
        } else if(deltaY == -1) {
            return 1;
        }
        return 0;
    }

    private List<Integer> generateNextCells(int index, List<Integer> visitedCells, List<Integer> currentPath) {
        int x = index % width;
        int y = index / width;
        List<Integer> nextCells = new ArrayList<>();
        int[] idxDeltas = new int[]{-1, 1, -width, width};
        for (int idxDelta : idxDeltas) {
            int nextIndex = index + idxDelta;
            if (nextIndex >= width * height || nextIndex < 0) {
                continue;
            }
            int nextX = nextIndex % width;
            int nextY = nextIndex / width;
            if (Math.abs(x - nextX) + Math.abs(y - nextY) != 1) {
                continue;
            }
            if (visitedCells.contains(nextIndex) || currentPath.contains(nextIndex)) {
                continue;
            }
            nextCells.add(nextIndex);
        }
        return nextCells;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public EnumSet<Door> getDoorsAt(int x, int y) {
        EnumSet<Door> doors = EnumSet.noneOf(Door.class);
        int mazeWall = mazeWalls[y][x];
        if((mazeWall & 1) != 0) {
            doors.add(Door.NORTH);
        }
        if((mazeWall & 2) != 0) {
            doors.add(Door.EAST);
        }
        if((mazeWall & 4) != 0) {
            doors.add(Door.SOUTH);
        }
        if((mazeWall & 8) != 0) {
            doors.add(Door.WEST);
        }
        return doors;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        // top border
        sb.append("+---".repeat(Math.max(0, width)));
        sb.append("+\n");

        for (int y = 0; y < height; y++) {
            // row of cells and vertical walls
            sb.append("|");
            for (int x = 0; x < width; x++) {
                sb.append("   ");
                if ((mazeWalls[y][x] & 2) != 0) { // East door
                    sb.append(" ");
                } else {
                    sb.append("|");
                }
            }
            sb.append("\n");
            // row of horizontal walls
            for (int x = 0; x < width; x++) {
                if ((mazeWalls[y][x] & 4) != 0) { // South door
                    sb.append("+   ");
                } else {
                    sb.append("+---");
                }
            }
            sb.append("+\n");
        }

        return sb.toString();
    }
}