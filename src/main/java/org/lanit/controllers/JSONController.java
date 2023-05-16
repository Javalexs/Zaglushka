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
    public Object response (@RequestBody String requestBody, String action, @RequestParam(value = "id")String userId) throws IOException{

        String str = "\\src\\main\\resources\\files\\templates\\json\\AddResponse.json";
        BufferedReader br = new BufferedReader(new FileReader(str));
        String value;
        String templateResponse = "";
        while((value = br.readLine()) != null){
            templateResponse += value ;
        }

        UUID uuid = UUID.randomUUID();

        LocalDateTime dt = LocalDateTime.now();
        DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String lastUpdate = dtFormatter.format(dt);

        List<AlertsItem> alerts = new ArrayList<>();
        List<TickersItem> tickers = new ArrayList<>();

        Add add = new Add();
        AlertsItem alertsItem = new AlertsItem(add.getTimeFrame(), add.getPercent());
        alerts.add(alertsItem);
        TickersItem tickersItem = new TickersItem(add.getName(), alerts);
        tickers.add(tickersItem);
        Info info = new Info(userId, tickers);

        Delete delete = new Delete();
        for(TickersItem a : tickers){
            if(a.getTicker().equals(delete.getTickerName())){
                a.getAlerts().remove(delete.getAlertIndex());
            }
        }

        ObjectMapper objectMapper = new ObjectMapper();
        AddRequestJson addRequestJson = objectMapper.readValue(requestBody, AddRequestJson.class);
        addRequestJson.setAdd();

        addRequestJson.setLastUpdate();
        addRequestJson.setInfo();
        String responseBody = String.format(templateResponse, );







        AddRequestJson addRequestJson = new AddRequestJson(add, uuid, lastUpdate, info);

        long startTime = System.currentTimeMillis();
        //2.
//        String str = "C:\\Users\\Алексей\\IdeaProjects\\Mock\\src\\main\\resources\\files\\templates\\json\\getResponse.json";
        String str = "\\src\\main\\resources\\files\\templates\\json\\getResponse.json";
        BufferedReader br = new BufferedReader(new FileReader(str));
        String value;
        String templateResponse = "";
        while((value = br.readLine()) != null){
            templateResponse += value ;
        }


        //3.
        String randomBalance = RandomStringUtils.randomNumeric(4);
        String randomAddress = RandomStringUtils.randomAlphanumeric(50);

        //4.
//        LocalDateTime dt = LocalDateTime.now();
//        DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
//        String formattedDT = dtFormatter.format(dt);
        //5.
        String responseBody = String.format(templateResponse, id, uuid, randomBalance, randomAddress, formattedDT);
        //6.
        Logger.info(String.format("Заглушка отработала за %s мс. ID клиента - %s. UUID ответа - %s.", System.currentTimeMillis() - startTime, id, uuid));
        //7.
        return ResponseEntity.ok().header("content-type", "application/json").body(responseBody);
    }
    @PostMapping(value = "json")
    public Object DeleteResponse(@RequestBody String requestBody) throws IOException{
        //1.
        long startTime = System.currentTimeMillis();
        //2.
        String str = "\\src\\main\\resources\\files\\templates\\json\\getResponse.json";
        BufferedReader br = new BufferedReader(new FileReader(str));
        String value;
        String templateResponse = "";
        while((value = br.readLine()) != null){
            templateResponse += value ;
        }
//        String templateResponse = Files.readString(Paths.get("src\\main\\resources\\files\\templates\\json\\getResponse.json"), StandardCharsets.UTF_8);
        //3.
        String randomBalance = RandomStringUtils.randomNumeric(4);
        String randomAddress = RandomStringUtils.randomAlphanumeric(50);
        UUID uuid = UUID.randomUUID();
        //4.
        LocalDateTime dt = LocalDateTime.now();
        DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String lastActiveDT = dtFormatter.format(dt);

        try{
            //5.
            ObjectMapper objectMapper = new ObjectMapper();
            RequestJson requestJson = objectMapper.readValue(requestBody, RequestJson.class);
            //6.
            String id = requestJson.getId();
            int balance = Integer.parseInt(requestJson.getDebitBalance() + requestJson.getCreditBalance());
            String registeredDT = requestJson.getRegistered();
            int numberOfFriends = requestJson.getFriends().size();
            //7.
            String responseBody = String.format(templateResponse, id, uuid, balance, numberOfFriends, registeredDT, lastActiveDT);
            //8.
            Logger.info(String.format("Заглушка отработала за %s мс. ID клиента - %s. UUID ответа - %s.", System.currentTimeMillis() + startTime, id, uuid));
            //9.
            return ResponseEntity.ok().header("content-type", "application/json").body(responseBody);
        } catch (Exception e){
            //10.
            Logger.error(String.format("%s\n%s", e.getMessage(), requestBody));
            //11.
            return ResponseEntity.badRequest().header("content-type", "application/json").body(String.format("{\"message\": \"Передана невалидная json\", \"request\": \"%s\"}", requestBody));
        }
    }
}
