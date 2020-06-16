package sgh;

import com.sun.net.httpserver.Headers;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class StockData {


    public static void getAndProcessChange(String stock) throws IOException {
        File Existe = new File("data_in/" + stock + ".csv");
        boolean Exists = Existe.exists();
        if (Exists == false) {
            String filePath = "data_in/" + stock + ".csv";
            download("https://query1.finance.yahoo.com/v7/finance/download/" + stock + "?period1=1560667712&period2=1592290112&interval=1d&events=history",
                    filePath);
            String[] row;
            ArrayList<String> header= new ArrayList<>();
            ArrayList<String> Lines = new ArrayList<>();
            ArrayList<Double> Open = new ArrayList<>();
            ArrayList<Double> Close = new ArrayList<>();
            ArrayList<String> Term = new ArrayList<>();


            try (Scanner scanner = new Scanner(Paths.get("data_in/"+stock+".csv"))) {
                while (scanner.hasNextLine()) {
                    String ThisLine = scanner.nextLine();
                    row = ThisLine.split(",");
                    Lines.add(ThisLine);
                    if (!(row[1].equals("Open"))) {
                        double op = Double.valueOf(row[1]);
                        Open.add(op);
                    }
                    if (!row[4].equals("Close")) {
                        double finish = Double.valueOf(row[4]);
                        Close.add(finish);
                    }
                }
            }
            catch (Exception exception) {
                System.out.println("Warning" + exception.getMessage());
            }
            for (int i = 0; i < Open.size(); i++) {
                Term.add(Double.toString(100.0* (Open.get(i) - Close.get(i))/Open.get(i)));
            }
            FileWriter filewriter = new FileWriter("data_out/"+stock+".csv");
            BufferedWriter bufferedwriter = new BufferedWriter(filewriter);
            for (int j = 0; j < Lines.size(); j++) {
                bufferedwriter.write(Lines.get(j) + Term.get(j));
            }
            bufferedwriter.close();
            filewriter.close();
        }
    }

    public static void download(String url, String NameOfFile) throws IOException {
        try (InputStream inputstream = URI.create(url).toURL().openStream()) {
            Files.copy(inputstream, Paths.get(NameOfFile));
        }
    }

    public static void main(String[] args) throws IOException {
        String[] stocks = new String[]{"TSLA", "AAPL", "GOOG"};
        for (String s : stocks) {
            getAndProcessChange(s);
        }
    }
}