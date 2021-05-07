package com.kheeso.cowin.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Center{


        public int center_id;
        public String name;
        public String address;
        public String state_name;
        public String district_name;
        public String block_name;
        public int pincode;
        public int lat;
        public String from;
        public String to;
        public String fee_type;
        public List<Session> sessions;

}
