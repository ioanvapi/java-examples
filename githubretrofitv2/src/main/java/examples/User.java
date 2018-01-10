package examples;

/*
* https://api.github.com/users
*
* {
  login: "mojombo",
  id: 1,
  url: "https://api.github.com/users/mojombo",
  ...
}
* */

public class User {
    long id;
    String login;
    String url;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
