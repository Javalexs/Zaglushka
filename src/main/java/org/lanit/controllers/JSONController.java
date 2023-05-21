package org.lanit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.lanit.models.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.tinylog.Logger;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/*
Разработка заглушки Веб-Сервиса, которая преобразует входящий JSON
в зависимости от передаваемого параметра action:

add – Добавить указанное оповещение в список тикеров JSON, в случае отсутствия
необходимого тикера, сначала добавить тикер и создать внутри него оповещение

delete – Удалить оповещение внутри тикера по индексу по передаваемым параметрам
 */
@Controller
public class JSONController {
    @PostMapping(value = "json")
    public Object response (@RequestBody String requestBody, @RequestParam String action) throws IOException {

        //#1 Генерируем рандомные UUID
        UUID uuid = UUID.randomUUID();

        //#2 Устанавливаем дату и время в требуемом формате
        LocalDateTime dt = LocalDateTime.now();
        DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String lastUpdate = dtFormatter.format(dt);

        //#3 Фиксируем время получение запроса
        long startTime = System.currentTimeMillis();

        //#4 Преобразуем JSON полученный в запросе в класс
        ObjectMapper objectMapper = new ObjectMapper();
        RequestJson requestJson = objectMapper.readValue(requestBody, RequestJson.class);

        //#5 Парсим общие данные JSON
        Info info = requestJson.getInfo();
        String userId = info.getUserID();
        List<TickersItem> tickerItem = info.getTickers();

        //#6 Создаем объект класса ResponseJson для создания ответа
        ResponseJson responseJson = new ResponseJson();

        //#7 Выбор преобразования в зависимости от значения параметра action
        switch (action) {
            case "add":
                try {
                    //#8 Парсим данные JSON для action = add
                    Add add = requestJson.getAdd();
                    String addTicker = add.getName();
                    int timeFrame = add.getTimeFrame();
                    int percent = add.getPercent();

                    //#9 Создаем новое оповещение
                    AlertsItem alertsItem = new AlertsItem();
                    alertsItem.setTimeframe(timeFrame);
                    alertsItem.setPercent(percent);

                    //#10 Ищем индекс тикера
                    int index = 0;
                    for (TickersItem arr : tickerItem) {
                        if (arr.getTicker().equals(addTicker)) {
                            index = tickerItem.indexOf(arr);
                        }
                    }
                    //#11 Добавляем новое оповещение
                    if (!tickerItem.get(index).getAlerts().contains(alertsItem)) {
                        tickerItem.get(index).getAlerts().add(alertsItem);
                        info.setUserID(userId);
                        info.setTickers(tickerItem);
                    } else {
                        TickersItem newTicker = new TickersItem();
                        newTicker.setTicker(addTicker);
                        newTicker.getAlerts().add(alertsItem);
                        info.getTickers().add(newTicker);
                    }
                    //#12 Создаем тело ответа
                    responseJson.setInfo(info);
                    responseJson.setUuid(uuid.toString());
                    responseJson.setLastUpdate(lastUpdate);

                    //#12 Создаем строку ответа с учетом добавленного оповещения
                    String responseBody = objectMapper.writeValueAsString(responseJson);

                    //#13 Логируем время обработки, id клиента, UUID ответа
                    Logger.info(String.format("Заглушка отработала за %s мс. ID клиента - %s. UUID ответа - %s.", System.currentTimeMillis() - startTime, userId, uuid));

                    //#14 Отправляем ответ со статусом 200  и телом ответа с необходимыми параметрами
                    return ResponseEntity.ok().header("content-type", "application/json").body(responseBody);

                } catch (Exception e) {

                    //#15 Логируем тело запроса если не удалось распарсить входящий JSON
                    Logger.error(String.format("%s\n%s", e.getMessage(), requestBody));

                    //#16 Отправляем ответ со статусом 400 и текстом запроса
                    return ResponseEntity.badRequest().header("content-type", "application/json").
                            body(String.format("{\"message\": \"Передана невалидная json\", \"request\": \"%s\"}", responseJson));
                }

            case "delete":
                try {
                    //#8 Парсим данные JSON для action = delete
                    Delete delete = requestJson.getDelete();
                    String deleteTicker = null;
                    int alertIndex = 0;
                    if(delete != null) {
                        deleteTicker = delete.getTickerName();
                        alertIndex = delete.getAlertIndex();
                    }

                    //#9 Ищем индекс тикера
                    int index = 0;
                    boolean isOK = false;
                    for (TickersItem ticker : tickerItem) {
                        if (ticker.getTicker().equals(deleteTicker)) {
                            isOK = true;
                            index = tickerItem.indexOf(ticker);
                        }
                    }

                    //#10 Проверка на несуществующий тикер
                    if(!isOK){
                        String errorMessage = "{\"message\": \"Передан некорректный тикер\"}";
                        return ResponseEntity.badRequest().header("content-type", "application/json").body(errorMessage);
                    }

                    //#11 Проверка на несуществующий индекс
                    if(alertIndex >= tickerItem.get(index).getAlerts().size()){
                        String errorMessage = "{\"message\": \"Передан некорректный индекс\"}";
                        return ResponseEntity.badRequest().header("content-type", "application/json").body(errorMessage);
                    }

                    //#12 Удаление оповещения
                    tickerItem.get(index).getAlerts().remove(alertIndex);

                    //#13 Создаем тело объекта
                    info.setUserID(userId);
                    info.setTickers(tickerItem);
                    responseJson.setInfo(info);
                    responseJson.setUuid(uuid.toString());
                    responseJson.setLastUpdate(lastUpdate);

                    //#12 Создаем строку ответа с учетом удаленного оповещения
                    String responseBody = objectMapper.writeValueAsString(responseJson);

                    //#14 Логируем время обработки, id клиента, UUID ответа
                    Logger.info(String.format("Заглушка отработала за %s мс. ID клиента - %s. UUID ответа - %s.", System.currentTimeMillis() - startTime, userId, uuid));

                    //#15 Отправляем ответ со статусом 200 и телом ответа с необходимыми параметрами
                    return ResponseEntity.ok().header("content-type", "application/json").body(responseBody);
                }catch (Exception e) {

                    //#16 Логируем тело запроса если не удалось распарсить входящий JSON
                    Logger.error(String.format("%s\n%s", e.getMessage(), requestBody));

                    //#17 Отправляем ответ со статусом 400 и текстом запроса
                    return ResponseEntity.badRequest().header("content-type", "application/json").
                            body(String.format("{\"message\": \"Передана невалидная json\", \"request\": \"%s\"}", responseJson));
                }
            default:
                //## Отправляем ответ со статусом 400 и текстом запроса если передан не верный action
                String errorMessage = "{\"message\": \"Передан некорректный action - " + action + "\"}";
                return ResponseEntity.badRequest().header("content-type", "application/json").body(errorMessage);
        }
    }
}
