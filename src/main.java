import java.io.IOException;

public class main {
    public static void main(String []args) throws IOException {

        JSON json = new JSON("files/", "pruebaJSON");
        JSONStatsWrapper myStats1 = new JSONStatsWrapper(json);

        myStats1.verification();
        myStats1.predictionInterval();

    }
}
