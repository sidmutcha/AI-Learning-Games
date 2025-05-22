import java.util.*;

public class GameHub {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("MAIN MENU:");
            System.out.println("1. Hexapawn");
            System.out.println("2. Tic Tac Toe");
            System.out.println("3. Exit");
            System.out.print("Choose a game (1-3): ");
            String choice = scanner.nextLine();
            if (choice.equals("1")) {
                HexapawnGame.play();
            } else if (choice.equals("2")) {
                TicTacToeGame.play();
            } else if (choice.equals("3")) {
                System.out.println("Goodbye!");
                break;
            } else {
                System.out.println("Invalid input.\n");
            }
        }
    }
}

class HexapawnGame {
    private static char[][] board;
    private static Map<String, List<String>> moveMemory = new HashMap<>();
    private static Scanner scanner = new Scanner(System.in);
    private static final char PLAYER = 'P';
    private static final char AI = 'A';

    public static void play() {
        boolean playAgain = true;
        while (playAgain) {
            initializeBoard();
            playGame();
            System.out.print("Play Hexapawn again? (yes/no): ");
            playAgain = scanner.nextLine().toLowerCase().startsWith("y");
        }
    }

    private static void initializeBoard() {
        board = new char[][] {
            {PLAYER, PLAYER, PLAYER},
            {'.', '.', '.'},
            {AI, AI, AI}
        };
    }

    private static void playGame() {
        while (true) {
            printBoard();
            if (!hasValidMoves(PLAYER)) {
                System.out.println("AI wins!");
                learnFromLoss(PLAYER);
                break;
            }
            playerMove();
            printBoard();
            if (checkWin(PLAYER)) {
                System.out.println("Player wins!");
                learnFromLoss(AI);
                break;
            }
            if (!hasValidMoves(AI)) {
                System.out.println("Player wins!");
                learnFromLoss(AI);
                break;
            }
            aiMove();
            if (checkWin(AI)) {
                System.out.println("AI wins!");
                learnFromLoss(PLAYER);
                break;
            }
        }
    }

    private static void printBoard() {
        System.out.println();
        for (char[] row : board) {
            for (char c : row) {
                System.out.print(c + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private static void playerMove() {
        while (true) {
            System.out.print("Your move (format: fromRow fromCol toRow toCol): ");
            String[] tokens = scanner.nextLine().split(" ");
            if (tokens.length != 4) {
                System.out.println("Invalid format, try again.");
                continue;
            }
            try {
                int fr = Integer.parseInt(tokens[0]);
                int fc = Integer.parseInt(tokens[1]);
                int tr = Integer.parseInt(tokens[2]);
                int tc = Integer.parseInt(tokens[3]);
                if (isValidMove(fr, fc, tr, tc, PLAYER)) {
                    board[tr][tc] = PLAYER;
                    board[fr][fc] = '.';
                    break;
                } else {
                    System.out.println("Invalid move, try again.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input, try again.");
            }
        }
    }

    private static void aiMove() {
        String boardKey = boardToString();
        List<String> possibleMoves = generateMoves(AI);
        if (!moveMemory.containsKey(boardKey)) {
            moveMemory.put(boardKey, new ArrayList<>(possibleMoves));
        }
        List<String> learnedMoves = moveMemory.get(boardKey);
        if (learnedMoves.isEmpty()) {
            System.out.println("AI has no good moves. Player wins!");
            learnFromLoss(AI);
            return;
        }
        String move = learnedMoves.get(new Random().nextInt(learnedMoves.size()));
        String[] parts = move.split(" ");
        int fr = Integer.parseInt(parts[0]);
        int fc = Integer.parseInt(parts[1]);
        int tr = Integer.parseInt(parts[2]);
        int tc = Integer.parseInt(parts[3]);
        board[tr][tc] = AI;
        board[fr][fc] = '.';
        System.out.println("AI moved from " + fr + "," + fc + " to " + tr + "," + tc);
    }

    private static boolean isValidMove(int fr, int fc, int tr, int tc, char player) {
        if (fr < 0 || fr > 2 || fc < 0 || fc > 2 || tr < 0 || tr > 2 || tc < 0 || tc > 2) return false;
        if (board[fr][fc] != player) return false;

        int dir = (player == PLAYER) ? 1 : -1;

        // forward move
        if (tc == fc && tr == fr + dir && board[tr][tc] == '.') return true;

        // diagonal capture
        if (Math.abs(tc - fc) == 1 && tr == fr + dir && board[tr][tc] != '.' && board[tr][tc] != player) return true;

        return false;
    }

    private static boolean hasValidMoves(char player) {
        return !generateMoves(player).isEmpty();
    }

    private static List<String> generateMoves(char player) {
        List<String> moves = new ArrayList<>();
        int dir = (player == PLAYER) ? 1 : -1;

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (board[r][c] != player) continue;

                int nr = r + dir;
                if (nr >= 0 && nr < 3) {
                    if (board[nr][c] == '.') {
                        moves.add(r + " " + c + " " + nr + " " + c);
                    }
                    if (c - 1 >= 0 && board[nr][c - 1] != '.' && board[nr][c - 1] != player) {
                        moves.add(r + " " + c + " " + nr + " " + (c - 1));
                    }
                    if (c + 1 < 3 && board[nr][c + 1] != '.' && board[nr][c + 1] != player) {
                        moves.add(r + " " + c + " " + nr + " " + (c + 1));
                    }
                }
            }
        }
        return moves;
    }

    private static boolean checkWin(char player) {
        int rowToCheck = (player == PLAYER) ? 2 : 0;
        for (int c = 0; c < 3; c++) {
            if (board[rowToCheck][c] == player) {
                return true;
            }
        }
        return false;
    }

    private static void learnFromLoss(char loser) {
        // Reinforcement logic placeholder
        // In real training, adjust move probability based on outcome
        System.out.println("(Reinforcement learning would be applied here for " + loser + ")");
    }

    private static String boardToString() {
        StringBuilder sb = new StringBuilder();
        for (char[] row : board) {
            for (char c : row) sb.append(c);
        }
        return sb.toString();
    }
}


class TicTacToeGame {
    private static char[][] board;
    private static Scanner scanner = new Scanner(System.in);
    private static final char PLAYER = 'X';
    private static final char AI = 'O';

    private static Map<String, List<Integer>> moveMemory = new HashMap<>();
    private static List<String> statesHistory = new ArrayList<>();
    private static List<Integer> movesHistory = new ArrayList<>();
    private static Random random = new Random();

    public static void play() {
        boolean playAgain = true;
        while (playAgain) {
            initializeBoard();
            statesHistory.clear();
            movesHistory.clear();
            playGame();
            System.out.print("Play Tic Tac Toe again? (y/n): ");
            playAgain = scanner.nextLine().toLowerCase().startsWith("y");
        }
    }

    private static void initializeBoard() {
        board = new char[3][3];
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                board[r][c] = '.';
    }

    private static void playGame() {
        boolean playerTurn = true;
        while (true) {
            printBoard();
            if (playerTurn) {
                playerMove();
                if (checkWin(PLAYER)) {
                    printBoard();
                    System.out.println("Player wins!");
                    learnFromLoss(AI);
                    break;
                }
            } else {
                aiMove();
                if (checkWin(AI)) {
                    printBoard();
                    System.out.println("AI wins!");
                    learnFromLoss(PLAYER);
                    break;
                }
            }
            if (isFull()) {
                printBoard();
                System.out.println("It's a draw!");
                learnFromDraw();
                break;
            }
            playerTurn = !playerTurn;
        }
    }

    private static void printBoard() {
        System.out.println();
        for (char[] row : board) {
            for (char c : row) {
                System.out.print(c + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private static void playerMove() {
        while (true) {
            System.out.print("Enter your move (row col, 0-based): ");
            String[] tokens = scanner.nextLine().split(" ");
            if (tokens.length != 2) {
                System.out.println("Invalid input.");
                continue;
            }
            try {
                int r = Integer.parseInt(tokens[0]);
                int c = Integer.parseInt(tokens[1]);
                if (r < 0 || r > 2 || c < 0 || c > 2 || board[r][c] != '.') {
                    System.out.println("Invalid move, try again.");
                    continue;
                }
                board[r][c] = PLAYER;
                break;
            } catch (Exception e) {
                System.out.println("Invalid input.");
            }
        }
    }

    private static void aiMove() {
        String state = boardToString();
        List<Integer> possibleMoves = getPossibleMoves();

        if (!moveMemory.containsKey(state)) {
            moveMemory.put(state, new ArrayList<>(possibleMoves));
        }

        List<Integer> learnedMoves = moveMemory.get(state);
        if (learnedMoves.isEmpty()) {
            learnedMoves.addAll(possibleMoves);
        }

        int chosenIndex = learnedMoves.get(random.nextInt(learnedMoves.size()));
        int r = chosenIndex / 3;
        int c = chosenIndex % 3;

        board[r][c] = AI;

        statesHistory.add(state);
        movesHistory.add(chosenIndex);

        System.out.println("AI moves at (" + r + ", " + c + ")");
    }

    private static List<Integer> getPossibleMoves() {
        List<Integer> moves = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            int r = i / 3;
            int c = i % 3;
            if (board[r][c] == '.') {
                moves.add(i);
            }
        }
        return moves;
    }

    private static boolean checkWin(char player) {
        // Check rows, cols, diagonals
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player) return true;
            if (board[0][i] == player && board[1][i] == player && board[2][i] == player) return true;
        }
        if (board[0][0] == player && board[1][1] == player && board[2][2] == player) return true;
        if (board[0][2] == player && board[1][1] == player && board[2][0] == player) return true;
        return false;
    }

    private static boolean isFull() {
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                if (board[r][c] == '.') return false;
        return true;
    }

    private static String boardToString() {
        StringBuilder sb = new StringBuilder();
        for (char[] row : board)
            for (char c : row)
                sb.append(c);
        return sb.toString();
    }

    private static void learnFromLoss(char loser) {
        if (loser == AI) {
            for (int i = 0; i < statesHistory.size(); i++) {
                String state = statesHistory.get(i);
                int move = movesHistory.get(i);
                List<Integer> moves = moveMemory.get(state);
                if (moves != null) {
                    moves.remove(Integer.valueOf(move));
                }
            }
            System.out.println("AI has learned from loss and adjusted its strategy.");
        } else if (loser == PLAYER) {
            System.out.println("AI wins and retains its strategy.");
        }
    }

    private static void learnFromDraw() {
        System.out.println("Game ended in a draw. AI keeps its current knowledge.");
    }
}