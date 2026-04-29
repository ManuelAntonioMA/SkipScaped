package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class BoardMapLoader {
    public BoardData load(String mapId){
        String resourcePath = "/maps/" + mapId + ".txt";
        try (InputStream in = Board.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IllegalArgumentException("Map not found: " + resourcePath);
            }
            return parseMapStream(in);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load map: " + mapId, e);
        }
    }
    /**
     * Reads the map input stream and converts it into board data.
     * Blank lines are ignored. All remaining lines must have the same width.
     *
     * @param in input stream of the map file
     * @throws IOException if the file cannot be read
     */
    private BoardData parseMapStream(InputStream in) throws IOException {
        List<String> lines = readNonBlankLines(in);

        if (lines.isEmpty())
            throw new IllegalArgumentException("Map is empty");

        validateDimensions(lines);
        return buildBoardData(lines);
    }

    private List<String> readNonBlankLines(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        List<String> lines = new ArrayList<>();
        String line;

        while ((line = reader.readLine()) != null) {
            if (!line.trim().isEmpty()) {
                lines.add(line);
            }
        }
        return lines;
    }

    private void validateDimensions(List<String> lines) {
        int h = lines.size();
        int w = lines.get(0).length();

        for (String currentLine : lines) {
            if (currentLine.length() != w) {
                throw new IllegalArgumentException("Inconsistent row width in map");
            }
        }

        if (w < 10 || h < 10) {
            throw new IllegalArgumentException("Map too small: " + w + "x" + h);
        }

        if (w > 80 || h > 40) {
            throw new IllegalArgumentException("Map too large: " + w + "x" + h);
        }
    }

    private BoardData buildBoardData(List<String> lines) {
        int height = lines.size();
        int width = lines.get(0).length();

        Cell[][] cells = new Cell[height][width];
        Position startPos = null;
        Position exitPos = null;

        for (int r = 0; r < height; r++) {
            String currentLine = lines.get(r);

            for (int c = 0; c < width; c++) {
                char ch = currentLine.charAt(c);
                CellType type = charToCellType(ch);
                Position pos = new Position(r, c);

                cells[r][c] = new Cell(pos, type);

                if (type == CellType.START) {
                    if (startPos != null) {
                        throw new IllegalArgumentException("Map has multiple START cells");
                    }
                    startPos = pos;
                } else if (type == CellType.EXIT) {
                    if (exitPos != null) {
                        throw new IllegalArgumentException("Map has multiple EXIT cells");
                    }
                    exitPos = pos;
                }
            }
        }

        if (startPos == null) {
            throw new IllegalArgumentException("Map missing START (S)");
        }

        if (exitPos == null) {
            throw new IllegalArgumentException("Map missing EXIT (E)");
        }

        return new BoardData(width, height, cells, startPos, exitPos);
    }

    private CellType charToCellType(char ch) {
        switch (ch) {
            case '#':
                return CellType.WALL;
            case '.':
                return CellType.FLOOR;
            case 'B':
                return CellType.BARRIER;
            case 'S':
                return CellType.START;
            case 'E':
                return CellType.EXIT;
            default:
                throw new IllegalArgumentException("Unknown map char: '" + ch + "'");
        }
    }
}
