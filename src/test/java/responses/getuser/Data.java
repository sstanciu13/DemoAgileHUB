package responses.getuser;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class Data {
    public int id;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public String email;

    public String first_name;
    public String last_name;
    public String avatar;
}
