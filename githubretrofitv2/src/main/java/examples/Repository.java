package examples;

import com.google.gson.annotations.SerializedName;

public class Repository {
    @SerializedName("id")
    long id;

    @SerializedName("full_name")
    String fullName;

    @SerializedName("url")
    String url;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Repository{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
