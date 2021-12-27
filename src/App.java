import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//JSON simple library imports
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

class App {
    private int _counter=0;
    private StringBuilder _processBar = new StringBuilder("_________________________");
    private String _token;

    App(String token){
        this._token=token;
    }

    private HttpURLConnection _getConnection(String url) throws MalformedURLException, IOException{     
        HttpURLConnection httpClient = (HttpURLConnection) new URL(url).openConnection();        
        httpClient.setRequestProperty("Authorization", "Bearer "+this._token);
        httpClient.setRequestMethod("GET");
        return httpClient;
    }

    private JSONObject _getJSONObject(String s) throws ParseException {
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(s);
    }

    private JSONArray _getJSONArray(String s) throws ParseException {
        JSONParser parser = new JSONParser();
        return (JSONArray) parser.parse(s);
    }

    private String _getResponseString(String url) throws IOException {

        //Getting connection
        HttpURLConnection httpClient = _getConnection(url);

        if(httpClient.getResponseCode()!=200)
            return null;

        //Creating a string from response
        BufferedReader reader = new BufferedReader(new InputStreamReader(httpClient.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line = "";
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        return response.toString();
    }

    private List<User> _getContributers(JSONArray contributersJson) throws IOException, ParseException {

        List<User> contributers = new ArrayList<>();
        

        for (int i = 0; i < Math.min(contributersJson.size(), 10); i++) {

            // Gettting JSON object from JSONArray
            JSONObject contributer = (JSONObject) contributersJson.get(i);

            // Getting JSON string for every user
            String jsonString = _getResponseString(contributer.get("url").toString());

            if (jsonString == null) {
                break;
            }
            
            // Parsing JSON string to JSON object
            JSONObject userJson = (JSONObject) _getJSONObject(jsonString);

            // Getting datas from JSON
            String name = userJson.get("login") != null ? userJson.get("login").toString() : " null ";
            String location = userJson.get("location") != null ? userJson.get("location").toString() : " null ";
            String company = userJson.get("company") != null ? userJson.get("company").toString() : " null ";
            int contributeCount = contributer.get("contributions") != null
                    ? Integer.parseInt(contributer.get("contributions").toString())
                    : 0;

            // Adding to the contributer list
            contributers.add(new User(name, location, company, contributeCount));

            // displaying processbar
            _processBar.setCharAt(_counter / 2, '*');
            _counter++;
            System.out.print("\rCreating contributers.txt file : " + _processBar);
        }

        return contributers;
    }

    private void _printFile(List<String> list) throws IOException {
        FileWriter fw = new FileWriter("contributers.txt");
        for (String s : list) {
            fw.write(s + "\n");
        }
        fw.close();
    }
    public void run() throws ParseException{
        
        final String[] repoNames = { "echarts", "superset", "dubbo", "spark", "airflow" };
        final String url = "https://api.github.com/repos/apache/";

        List<Repo> repos = new ArrayList<>();
        List<String> list = new ArrayList<>();

        boolean haveRequest = true;    

        try {

            for (String repoName : repoNames) {

                // Getting contributers list for every repo as a JSON string
                String jsonString = _getResponseString(url + repoName + "/contributors");

                if (jsonString == null) {
                    haveRequest = false;
                    continue;
                }
                haveRequest = true;
                // Parsing the string to JSON Array
                JSONArray contributersJson = (JSONArray) _getJSONArray(jsonString);

                // Getting a list of contributers
                List<User> contributers = _getContributers(contributersJson);

                // Adding to repo list
                repos.add(new Repo(repoName, contributers));

                for (User user : contributers) {
                    list.add("repo: " + repoName
                            + ", user: " + user.getUserName()
                            + ", location: " + user.getLocation()
                            + ", company: " + user.getCompany()
                            + ", contributions: " + user.getContributes());
                }

                list.add("");
                
            }

            if (!haveRequest) {
                list.add(
                        "\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n You have exceeded the request limit\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
            _printFile(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        String token = "";
        System.out.print("Please Enter your token : ");
        token = sc.nextLine();
        App app = new App(token);
        sc.close();
        app.run();
    }
    
}
