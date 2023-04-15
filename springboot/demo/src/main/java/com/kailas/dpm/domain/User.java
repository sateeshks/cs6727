package com.kailas.dpm.domain;

import lombok.Data;

import java.util.List;

@Data
public class User {
    String name;
    String email;
    PasswordProfile profile;
    List<Device> devices;
}