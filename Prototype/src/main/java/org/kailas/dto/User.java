package org.kailas.dto;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;
@Data
public class User {
    String name;
    String email;
    PasswordProfile profile;
    List<Device> devices;
}
