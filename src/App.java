import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

//JSON simple library imports
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class App {

    static JSONObject getJSONObject(String s) throws ParseException {
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(s);
    }

    static JSONArray getJSONArray(String s) throws ParseException {
        JSONParser parser = new JSONParser();
        return (JSONArray) parser.parse(s);
    }

    static String getJSONString(String url) throws IOException, ParseException {
        try {

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            return response.body();

        } catch (

        Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static List<User> getContributers(JSONArray contributersJson) throws IOException, ParseException {

        List<User> contributers = new ArrayList<>();

        for (int i = 0; i < contributersJson.size(); i++) {

            // Limiting contributers list
            if (i == 10)
                break;

            // Gettting JSON object from JSONArray
            JSONObject contributer = (JSONObject) contributersJson.get(i);

            // Getting JSON string for every user
            String jsonString = getJSONString(contributer.get("url").toString());

            if (jsonString == null) {
                break;
            }

            // Parsing JSON string to JSON object
            JSONObject userJson = (JSONObject) getJSONObject(jsonString);

            // Getting datas from JSON
            String name = userJson.get("login") != null ? userJson.get("login").toString() : " null ";
            String location = userJson.get("location") != null ? userJson.get("location").toString() : " null ";
            String company = userJson.get("company") != null ? userJson.get("company").toString() : " null ";
            int contributeCount = contributer.get("contributions") != null
                    ? Integer.parseInt(contributer.get("contributions").toString())
                    : 0;

            // Adding to the contributer list
            contributers.add(new User(name, location, company, contributeCount));
        }

        return contributers;
    }

    static void printFile(List<String> list) throws IOException {
        FileWriter fw = new FileWriter("out.txt");
        for (String s : list) {
            fw.write(s + "\n");
        }
        fw.close();
    }

    public static void main(String[] args) throws Exception {

        final String[] repoNames = { "echarts", "superset", "dubbo", "spark", "airflow" };
        final String url = "https://api.github.com/repos/apache/";

        List<Repo> repos = new ArrayList<>();
        List<String> list = new ArrayList<>();

        try {
            for (String repoName : repoNames) {

                // Getting contributers list for every repo as a JSON string
                String jsonString = getJSONString(url + repoName + "/contributors");

                if (jsonString == null) {
                    continue;
                }

                // Parsing the string to JSON Array
                JSONArray contributersJson = (JSONArray) getJSONArray(jsonString);

                // Getting a list of contributers
                List<User> contributers = getContributers(contributersJson);

                // Adding to repo list
                repos.add(new Repo(repoName, contributers));

                for (User user : contributers) {
                    list.add("repo: " + repoName + ", user: " + user.getUserName() +
                            ", location: " + user.getLocation()
                            + ", company: " + user.getCompany() + ", contributions: " +
                            user.getContributes());
                }

                list.add("");
            }
            printFile(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
