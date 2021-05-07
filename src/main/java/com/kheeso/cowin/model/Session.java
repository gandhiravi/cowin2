package com.kheeso.cowin.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Session{
    public String session_id;
    public String date;
    public int available_capacity;
    public int min_age_limit;
    public String vaccine;
    public List<String> slots;
}

