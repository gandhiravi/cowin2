package com.kheeso.cowin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kheeso.cowin.model.Center;
import com.kheeso.cowin.model.Root;
import com.kheeso.cowin.model.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class CowinApplication {
	@Value( "${cowin.slotcheck.url}" )
	String cowinUrl;

	@Value( "${cowin.districtCodes}" )
	List<String> districts;

	@Value( "${cowin.vaccines}" )
	List<String> vaccines;

	@Value("${cowin.ageGroups}")
	List<Integer> ageGroups;

	@Value("${cowin.doseNumber}")
	Integer doseNumber;

    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    public static void main(String[] args) {
    	SpringApplication.run(CowinApplication.class, args);
    }

    // Check every 5 sec
    @Scheduled(fixedRate = 10000)
    @GetMapping(value = "/cowin")
    public void cowinSlotCheck() throws IOException {
		for (String districtCode : districts){
			if (!fetchResults(districtCode)) {
				log.error("No Slot found for district {} ", districtCode);
			}
		}
    }
	private boolean fetchResults(String disctrictCode) throws MalformedURLException {
		URL url = new URL(String.format(cowinUrl, disctrictCode, LocalDate.now().format(dateFormatter)));
		boolean flag = false;
		try {
			Root response = new ObjectMapper().readValue(getResponse(url), Root.class);
			for (Center c : response.getCenters()) {
				for (Session s : c.getSessions()) {
					if (ageGroups.contains(s.getMin_age_limit()))
						if(vaccines.contains(s.getVaccine())) {
							if((doseNumber == 1 && s.available_capacity_dose1> 0)
								|| (doseNumber == 2 && s.available_capacity_dose2>0)) {
								log.info(c.getPincode() + " : " + s.getDate() + " : " + s.getAvailable_capacity() + " -- " + c.getName() + " - " + s);
								final Runnable runnable =
										(Runnable) Toolkit.getDefaultToolkit().getDesktopProperty("win.sound.exclamation");
								if (runnable != null) runnable.run();
								flag = true;
							}
						}
				}
			}
		} catch (IOException e) {
			System.err.println(e);
		}
		return flag;
	}

	private String getResponse(URL url) throws IOException {
		HttpURLConnection connection = ((HttpURLConnection) url.openConnection());
		connection.addRequestProperty("Cache-Control", "public,max-age=0");
		connection.addRequestProperty("User-Agent", "chrome");
		connection.addRequestProperty("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
		connection.addRequestProperty("User-Agent", "Mozilla/4.0");
		InputStream input;
		if (connection.getResponseCode() == 200)
			input = connection.getInputStream();
		else input = connection.getErrorStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		String msg = "";
		String message = "";
		while ((msg = reader.readLine()) != null) {
			message = message + msg;
		}
		reader.close();
		return message;
	}

}
