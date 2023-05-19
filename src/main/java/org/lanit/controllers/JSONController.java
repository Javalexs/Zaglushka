package org.lanit.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.lanit.models.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class JSONController {
    @PostMapping(value = "json")
    public Object response (@RequestBody String requestbody, @RequestParam String action) throws Exception {

        UUID uuid = UUID.randomUUID();

        LocalDateTime dt = LocalDateTime.now();
        DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String lastUpdate = dtFormatter.format(dt);

        ObjectMapper objectMapper = new ObjectMapper();
        RequestJson requestJson = objectMapper.readValue(requestbody, RequestJson.class);

        Info info = requestJson.getInfo();
        String userId = info.getUserID();
        List<TickersItem> tickerItem = info.getTickers();

        ResponseJson responseJson = new ResponseJson();
        switch (action) {
            case "add":
                Add add = requestJson.getAdd();
                String addTicker = add.getName();
                int timeframe = add.getTimeFrame();
                int percent = add.getPercent();

                AlertsItem alertsItem = new AlertsItem();
                alertsItem.setTimeframe(timeframe);
                alertsItem.setPercent(percent);

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

            case "delete":
                Delete delete = requestJson.getDelete();
                String delTicker = delete.getTickerName();
                int alertIndex = delete.getAlertIndex();

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

                responseBody = objectMapper.writeValueAsString(responseJson);

                return ResponseEntity.ok().header("content-type", "application/json").body(responseBody);
            default:
                String errorMessage = "Передан некорректный action - " + action + "";
                return ResponseEntity.badRequest().header("content-type", "application/json").body(errorMessage);
        }
    }

}
