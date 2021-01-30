package requests.CreateUser;

public class CreateUserRequest {
    public String name;
    public String job;

    public CreateUserRequest setName(String name){
        this.name = name;
        return this;
    }

    public CreateUserRequest setJob(String job){
        this.job = job;
        return this;
    }
}
