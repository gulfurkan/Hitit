import java.util.List;

public class Repo {
    private String _repoName;
    private List <User> _contributers;

    Repo(String repoName, List<User> contributers){
        this._repoName = repoName;
        this._contributers = contributers;
    }

    //setters
    public void setName(String repoName) {
        this._repoName = repoName;
    }

    public void setContributer(List<User> contributers) {
        this._contributers = contributers;
    }

    //getters
    public String getName() {
        return _repoName;
    }
    
    public List<User> getContributer() {
        return _contributers;
    }

}
