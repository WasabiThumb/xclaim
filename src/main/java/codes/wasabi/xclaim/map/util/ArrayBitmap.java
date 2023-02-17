package codes.wasabi.xclaim.map.util;

public class ArrayBitmap implements Bitmap {

    private boolean[][] array;
    private int width;
    private int height;
    public ArrayBitmap(boolean[][] array) {
        setArray(array);
    }

    public boolean[][] getArray() {
        return array.clone();
    }

    public void setArray(boolean[][] array) {
        this.array = array;
        height = array.length;
        int w = 0;
        for (boolean[] a: array) w = Math.max(w, a.length);
        width = w;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public boolean getPixel(int x, int y) {
        if (y < 0 || y >= height) return false;
        boolean[] row = array[y];
        if (x < 0 || x >= row.length) return false;
        return row[x];
    }

}
