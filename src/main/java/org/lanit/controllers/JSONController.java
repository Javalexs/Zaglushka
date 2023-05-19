package org.lanit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.lanit.models.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Controller
public class JSONController {
    @PostMapping(value = "json")
    public Object response (@RequestBody String requestbody, @RequestParam String action) throws Exception {

        UUID uuid = UUID.randomUUID();

        LocalDateTime dt = LocalDateTime.now();
        DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String lastUpdate = dtFormatter.format(dt);

//        ObjectMapper objectMapper = new ObjectMapper();
//        RequestJson requestJson = objectMapper.readValue(requestbody, RequestJson.class);
//
//        Info info = requestJson.getInfo();
//        String userId = info.getUserID();
//        List<TickersItem> tickerItem = info.getTickers();

        ResponseJson responseJson = new ResponseJson();
        switch (action) {
            case "add":
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    RequestJson requestJson = objectMapper.readValue(requestbody, RequestJson.class);

                    Info info = requestJson.getInfo();
                    String userId = info.getUserID();
                    List<TickersItem> tickerItem = info.getTickers();
                    Add add = requestJson.getAdd();
                    String addTicker = add.getName();
                    int timeframe = add.getTimeFrame();
                    int percent = add.getPercent();

                    AlertsItem alertsItem = new AlertsItem();
                    alertsItem.setTimeframe(timeframe);
                    alertsItem.setPercent(percent);
    //
                    int addValue = 0;
                    for (TickersItem ticker : tickerItem) {
                        if (ticker.getTicker().equals(addTicker)) {
                            addValue = tickerItem.indexOf(ticker);
                        }
                    }
                    if (!tickerItem.get(addValue).getAlerts().contains(alertsItem)) {
                        tickerItem.get(addValue).getAlerts().add(alertsItem);
                        info.setUserID(userId);
                        info.setTickers(tickerItem);

                        responseJson.setInfo(info);
                        responseJson.setUuid(uuid.toString());
                        responseJson.setLastUpdate(lastUpdate);

                    } else {
                        TickersItem tickerItem2 = new TickersItem();
                        tickerItem2.setTicker(addTicker);
                        tickerItem2.getAlerts().add(alertsItem);
                        info.getTickers().add(tickerItem2);

                        responseJson.setInfo(info);
                        responseJson.setUuid(uuid.toString());
                        responseJson.setLastUpdate(lastUpdate);
                    }
                    String responseBody = null;
                    responseBody = objectMapper.writeValueAsString(responseJson);
                    return ResponseEntity.ok().header("content-type", "application/json").body(responseBody);
                } catch (Exception e) {

                    // #9 Logging error message and request body if json parsing failed


                    //  #10 Returning response with status 200 and request body
                    return ResponseEntity.badRequest().header("content-type", "application/json").
                            body(String.format("{\"message\": \"Передана невалидная json\", \"request\": \"%s\"}", responseJson));
                }

            case "delete":
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    RequestJson requestJson = objectMapper.readValue(requestbody, RequestJson.class);

                    Info info = requestJson.getInfo();
                    String userId = info.getUserID();
                    List<TickersItem> tickerItem = info.getTickers();


                    Delete delete = requestJson.getDelete();
                    String delTicker = null;
                    int alertIndex = 0;
                    if(delete != null) {
                        delTicker = delete.getTickerName();
                        alertIndex = delete.getAlertIndex();
                    }

                    int delValue = 0;
                    for (TickersItem ticker : tickerItem) {
                        if (ticker.getTicker().equals(delTicker)) {
                            delValue = tickerItem.indexOf(ticker);
                        }
                    }
                    tickerItem.get(delValue).getAlerts().remove(alertIndex);

                    info.setUserID(userId);
                    info.setTickers(tickerItem);

                    responseJson.setInfo(info);
                    responseJson.setUuid(uuid.toString());
                    responseJson.setLastUpdate(lastUpdate);

                    String responseBody = objectMapper.writeValueAsString(responseJson);

                    return ResponseEntity.ok().header("content-type", "application/json").body(responseBody);
                }catch (Exception e) {
                    return ResponseEntity.badRequest().header("content-type", "application/json").
                            body(String.format("{\"message\": \"Передана невалидная json\", \"request\": \"%s\"}", responseJson));

                }
                    default:
                        String errorMessage = "Передан некорректный action - " + action + "";
                        return ResponseEntity.badRequest().header("content-type", "application/json").body(errorMessage);

        }
    }

}
