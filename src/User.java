public class User {
    private String _userName, _location, _company;
    private int _contributes;
    
    User(String userName, String location, String company, int contributes){
        this._userName = userName;
        this._location = location;
        this._company = company;
        this._contributes = contributes;
    }
    
    //Setters
    public void setUserName(String userName) {
        this._userName = userName;
    }
    
    public void setLocation(String location) {
        this._location = location;
    }
    
    public void setCompany(String company) {
        this._company = company;
    }

    public void setContributes(int contributes) {
        this._contributes = contributes;
    }

    //Getters
    public String getUserName() {
        return _userName;
    }

    public String getLocation() {
        return _location;
    }

    public String getCompany() {
        return _company;
    }
    
    public int getContributes() {
        return _contributes;
    }
}
