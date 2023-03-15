package override.logic;

import java.util.*;

public class Player implements LabyrinthPlayer {

    private int myNum;
    int[][] actualMap;
    private int kolvoSteps = 0;
    int[] previousCoordinates = {0, 0};

    @Override
    public void takeYourNumber(int number) {
        myNum = number;
    }

    @Override
    public Direction step(GameState gameState) {
        try {
            if (kolvoSteps == 0) {
                actualMap = gameState.getMap();
                System.out.println("мой бот " + myNum);
            }

            int i = getMyI(gameState);
            int j = getMyJ(gameState);
            System.out.println(kolvoSteps + ")" + " " + "i = " + getMyI(gameState) + " j  =  " + getMyJ(gameState) + " ");
            for (int k = 0; k < GameState.HEIGHT; k++) {
                for (int l = 0; l < GameState.WIDTH; l++) {
                    System.out.printf("%5d ", actualMap[k][l]);
                }
                System.out.println();
            }
            System.out.print("SCORE: ");
            if (myNum == -1) {
                System.out.println(gameState.getTeam1Score());
            } else {
                System.out.println(gameState.getTeam2Score());
            }

            Direction rez = decideDirection(getMyI(gameState), getMyJ(gameState), gameState);
            System.out.println("иду " + rez.name());
            kolvoSteps++;


            if (rez.equals(Direction.UP)) {
                actualMap[i][j] = -100;
                actualMap[i - 1][j] = myNum;
            }
            if (rez.equals(Direction.BOTTOM)) {
                actualMap[i][j] = -100;
                actualMap[i + 1][j] = myNum;
            }
            if (rez.equals(Direction.LEFT)) {
                actualMap[i][j] = -100;
                actualMap[i][j - 1] = myNum;
            }
            if (rez.equals(Direction.RIGHT)) {
                actualMap[i][j] = -100;
                actualMap[i][j + 1] = myNum;
            }


            return rez;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Direction.NONE;
    }

    @Override
    public String getTelegramNick() {
        return "HLPTM";
    }

    private int getMyI(GameState gameState) {
        for (int i = 0; i < GameState.HEIGHT; i++) {
            for (int j = 0; j < GameState.WIDTH; j++) {
                if (gameState.getMap()[i][j] == myNum) {
                    return i;
                }
            }
        }
        return 0;
    }

    private int getMyJ(GameState gameState) {
        for (int i = 0; i < GameState.HEIGHT; i++) {
            for (int j = 0; j < GameState.WIDTH; j++) {
                if (gameState.getMap()[i][j] == myNum) {
                    return j;
                }
            }
        }
        return 0;
    }

    private Direction decideDirection(int i, int j, GameState gameState) {
        List<Direction> possibledirs = new LinkedList<>();
        possibledirs.add(Direction.UP);
        possibledirs.add(Direction.BOTTOM);
        possibledirs.add(Direction.LEFT);
        possibledirs.add(Direction.RIGHT);

// определение углов
        if (i == 0 && j == 0) {
            possibledirs.remove(Direction.UP);
            possibledirs.remove(Direction.LEFT);
        }
        if (i == 0 && j == GameState.WIDTH - 1) {
            possibledirs.remove(Direction.UP);
            possibledirs.remove(Direction.RIGHT);
        }
        if (i == GameState.HEIGHT - 1 && j == 0) {
            possibledirs.remove(Direction.BOTTOM);
            possibledirs.remove(Direction.LEFT);
        }
        if (i == GameState.HEIGHT - 1 && j == GameState.WIDTH - 1) {
            possibledirs.remove(Direction.BOTTOM);
            possibledirs.remove(Direction.RIGHT);
        }
        // поределение стенок
        if (i == 0) {
            possibledirs.remove(Direction.UP);
        }
        if (i == GameState.HEIGHT - 1) {
            possibledirs.remove(Direction.BOTTOM);
        }
        if (j == GameState.WIDTH - 1) {
            possibledirs.remove(Direction.RIGHT);
        }
        if (j == 0) {
            possibledirs.remove(Direction.LEFT);
        }

// определение соседних клеток с отрицательными значениями
        if (possibledirs.contains(Direction.UP)) {
            if (actualMap[i - 1][j] < 0) {
                possibledirs.remove(Direction.UP);
            }
        }
        if (possibledirs.contains(Direction.BOTTOM)) {
            if (actualMap[i + 1][j] < 0) {
                possibledirs.remove(Direction.BOTTOM);
            }
        }
        if (possibledirs.contains(Direction.LEFT)) {
            if (actualMap[i][j - 1] < 0) {
                possibledirs.remove(Direction.LEFT);
            }
        }
        if (possibledirs.contains(Direction.RIGHT)) {
            if (actualMap[i][j + 1] < 0) {
                possibledirs.remove(Direction.RIGHT);
            }
        }
        Direction rez;

        // если все соседние клетки с отрицательным значением
        if (possibledirs.isEmpty()) {
            List<Direction> n = new ArrayList<>();
            if (i != 0 && actualMap[i - 1][j] == -100 && (gameState.getMap()[i - 1][j] != -1 || gameState.getMap()[i - 1][j] != -2)) {
                n.add(Direction.UP);
            }
            if (i != GameState.HEIGHT - 1 && actualMap[i + 1][j] == -100 && (gameState.getMap()[i + 1][j] != -1 || gameState.getMap()[i + 1][j] != -2)) {
                n.add(Direction.BOTTOM);
            }
            if (j != 0 && actualMap[i][j - 1] == -100 && (gameState.getMap()[i][j - 1] != -1 || gameState.getMap()[i][j - 1] != -2)) {
                n.add(Direction.LEFT);
            }
            if (j != GameState.WIDTH - 1 && actualMap[i][j + 1] == -100 && (gameState.getMap()[i][j + 1] != -1 || gameState.getMap()[i][j + 1] != -2)) {
                n.add(Direction.RIGHT);
            }

            if (i - 1 == previousCoordinates[0]) {
                n.remove(Direction.UP);
            }

            if (i + 1 == previousCoordinates[0]) {
                n.remove(Direction.BOTTOM);
            }

            if (j - 1 == previousCoordinates[1]) {
                n.remove(Direction.LEFT);
            }

            if (j + 1 == previousCoordinates[1]) {
                n.remove(Direction.RIGHT);
            }

            rez = n.get(new Random().nextInt(n.size()));
        } else {
            rez = possibledirs.get(new Random().nextInt(possibledirs.size()));
        }

        // если рядом клетка с монеткой
        HashMap<Integer, Direction> directs = new HashMap<>();
        if (j != GameState.WIDTH - 1 && gameState.getMap()[i][j + 1] > 0) {
            directs.put(gameState.getMap()[i][j + 1], Direction.RIGHT);
        }
        if (j != 0 && gameState.getMap()[i][j - 1] > 0) {
            directs.put(gameState.getMap()[i][j - 1], Direction.LEFT);
        }
        if (i != GameState.HEIGHT - 1 && gameState.getMap()[i + 1][j] > 0) {
            directs.put(gameState.getMap()[i + 1][j], Direction.BOTTOM);
        }
        if (i != 0 && gameState.getMap()[i - 1][j] > 0) {
            directs.put(gameState.getMap()[i - 1][j], Direction.UP);
        }
        if (!directs.isEmpty()) {
            rez = directs.get(Collections.max(directs.keySet()));
        }

        // если рядом клеток с монеткой нет, но есть наискосок
        if (!ifOnEdge(i, j, gameState)) {
            if (ifSurroundedByEmpty(i, j, gameState)) {
                int[] coordinatesMaxCoin = findMax(gameState);
                if (coordinatesMaxCoin[0] > i && gameState.getMap()[i + 1][j] > 0) {
                    rez = Direction.BOTTOM;
                }
                if (coordinatesMaxCoin[0] < i && gameState.getMap()[i - 1][j] > 0) {
                    rez = Direction.UP;
                }
                if (coordinatesMaxCoin[1] > j && gameState.getMap()[i][j + 1] > 0) {
                    rez = Direction.RIGHT;
                }
                if (coordinatesMaxCoin[1] < j && gameState.getMap()[i][j - 1] > 0) {
                    rez = Direction.LEFT;
                }
            }
        }

        previousCoordinates[0] = i;
        previousCoordinates[1] = j;
        return rez;

    }


    private boolean ifSurroundedByEmpty(int i, int j, GameState gameState) {
        if (!(j != GameState.WIDTH - 1 && (gameState.getMap()[i][j + 1] == 0 || actualMap[i][j + 1] == -100))) {
            return false;
        }
        if (!(j != 0 && (gameState.getMap()[i][j - 1] == 0 || actualMap[i][j - 1] == -100))) {
            return false;
        }
        if (!(i != 0 && (gameState.getMap()[i - 1][j] == 0 || actualMap[i - 1][j] == -100))) {
            return false;
        }
        if (!(i != GameState.HEIGHT - 1 && (gameState.getMap()[i + 1][j] == 0 || actualMap[i + 1][j] == -100))) {
            return false;
        }
        return true;
    }


    private boolean ifOnEdge(int i, int j, GameState gameState) {
        if (i == 0 || i == GameState.HEIGHT - 1 || j == 0 || j == GameState.WIDTH - 1) {
            return true;
        }
        return false;
    }

    private int[] findMax(GameState gameState) {
        int[] rez = new int[2];
        int max = 0;
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (gameState.getMap()[i][j] > max) {
                    rez = new int[]{i, j};
                    max = gameState.getMap()[i][j];
                }
            }
        }
        return rez;
    }

}
