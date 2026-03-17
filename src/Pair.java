public class Pair {
    String word;
    int distance;

    public Pair(String word, int editDistance){
        this.word = word;
        this.distance = editDistance;
    }

    public String getWord() {
        return word;
    }

    public int getDistance() {
        return this.distance;
    }

}
