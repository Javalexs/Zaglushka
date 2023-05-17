package org.lanit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.RandomStringUtils;
import org.lanit.addJson.*;
import org.lanit.deleteJson.Delete;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.tinylog.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class JSONController {
    @GetMapping(value = "json")
    public Object response (@RequestBody String requestBody, @RequestParam(value = "id")String userId) throws IOException {

        String str1 = "\\src\\main\\resources\\files\\templates\\json\\AddResponse.json";
        BufferedReader buf1 = new BufferedReader(new FileReader(str1));
        String value1;
        String tempAddResponse = "";
        while ((value1 = buf1.readLine()) != null) {
            tempAddResponse += value1;
        }

        String str2 = "\\src\\main\\resources\\files\\templates\\json\\DeleteResponse.json";
        BufferedReader buf2 = new BufferedReader(new FileReader(str2));
        String value2;
        String tempDelResponse = "";
        while ((value2 = buf2.readLine()) != null) {
            tempDelResponse += value2;
        }

        UUID uuid = UUID.randomUUID();

        LocalDateTime dt = LocalDateTime.now();
        DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String lastUpdate = dtFormatter.format(dt);

        List<AlertsItem> alerts = new ArrayList<>();
        List<TickersItem> tickers = new ArrayList<>();

        switch (action) {
            case "add":
                Add add = new Add();
                AlertsItem alertsItem = new AlertsItem(add.getTimeFrame(), add.getPercent());
                alerts.add(alertsItem);
                TickersItem tickersItem = new TickersItem(add.getName(), alerts);
                tickers.add(tickersItem);
                Info info = new Info(userId, tickers);

                break;

            case "delete":
                Delete delete = new Delete();
                for (TickersItem a : tickers) {
                    if (a.getTicker().equals(delete.getTickerName())) {
                        a.getAlerts().remove(delete.getAlertIndex());
                    }
                }
                break;

            default:
                System.out.println("Передан некорректный action " + action);
                break;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        AddRequestJson addRequestJson = objectMapper.readValue(requestBody, AddRequestJson.class);
        String responseBody = String.format(tempAddResponse, addRequestJson.getAdd(), lastUpdate, uuid, addRequestJson.getInfo());
        return ResponseEntity.ok().header("content-type", "application/json").body(responseBody);
    }



//            ObjectMapper objectMapper = new ObjectMapper();
//            RequestJson requestJson = objectMapper.readValue(requestBody, RequestJson.class);
//            //6.
//            String id = requestJson.getId();
//            int balance = Integer.parseInt(requestJson.getDebitBalance() + requestJson.getCreditBalance());
//            String registeredDT = requestJson.getRegistered();
//            int numberOfFriends = requestJson.getFriends().size();
//            //7.
//            String responseBody = String.format(templateResponse, id, uuid, balance, numberOfFriends, registeredDT, lastActiveDT);
//            //8.
//            Logger.info(String.format("Заглушка отработала за %s мс. ID клиента - %s. UUID ответа - %s.", System.currentTimeMillis() + startTime, id, uuid));
//            //9.
//            return ResponseEntity.ok().header("content-type", "application/json").body(responseBody);


}
