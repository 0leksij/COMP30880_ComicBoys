import java.net.*;
import java.io.*;


public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        try {
            URL myURL = new URL("https://www.scrapethissite.com/pages/");
            URLConnection myURLConnection = myURL.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    myURLConnection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                System.out.println(inputLine);
            in.close();
        }
        catch (MalformedURLException e) {
            System.out.println("new URL() failed");
        }
        catch (IOException e) {
            System.out.println("openConnection() failed - server may be down or not exist");
        }
    }
}