package com.chandima.branchconnector.branchui.services;

import com.chandima.branchconnector.branchui.config.AccessToken;
import com.chandima.branchconnector.commons.model.customerservice.Customer;
import com.chandima.branchconnector.commons.model.deliveryservice.Delivery;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Service
public class DeliveryUIServerImpl implements DeliveryUIServer {

    @LoadBalanced
    @Autowired
    RestTemplate restTemplate;

    @Override
    public Model loadDeliveries(Model model) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", AccessToken.getAccessToken());
        HttpEntity<Delivery> customerHttpEntity = new HttpEntity<>(httpHeaders);
        try {
            ResponseEntity<Delivery[]> responseEntity = restTemplate.exchange("http://deliveryservice/services/getAllDeliverys", HttpMethod.GET, customerHttpEntity, Delivery[].class);
            model.addAttribute("deliveryList", responseEntity.getBody());
            System.out.println(responseEntity.getBody().length+">>>>>>>>>>>>");
        } catch (HttpStatusCodeException e) {
            ResponseEntity responseEntity = ResponseEntity.status(e.getRawStatusCode()).headers(e.getResponseHeaders()).body(e.getResponseBodyAsString());
            model.addAttribute("error", responseEntity);
        }
        return model;
    }

    @Override
    public Model addDelivery(Delivery delivery, Model model) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add("Authorization", AccessToken.getAccessToken());
        JSONObject customerJsonObject = new JSONObject();
        customerJsonObject.put("id", delivery.getId());
        customerJsonObject.put("orderID", delivery.getOrderID());
        customerJsonObject.put("pickUpLocation", delivery.getPickUpLocation());
        customerJsonObject.put("deliveryLocation", delivery.getDeliveryLocation());
        customerJsonObject.put("orderDate", delivery.getOrderDate());
        customerJsonObject.put("estimateDate", delivery.getEstimateDate());
        customerJsonObject.put("doneStatus", delivery.getDoneStatus());
        customerJsonObject.put("deliveryCost", delivery.getDeliveryCost());
        HttpEntity<String> request =
                new HttpEntity<String>(customerJsonObject.toString(), httpHeaders);
        try {
            Delivery checkDelivery = restTemplate.postForObject("http://deliveryservice/services/addDelivery", request, Delivery.class);
            model.addAttribute("delivery", checkDelivery);
        } catch (HttpStatusCodeException e) {
            ResponseEntity responseEntity = ResponseEntity.status(e.getRawStatusCode()).headers(e.getResponseHeaders()).body(e.getResponseBodyAsString());
            model.addAttribute("error", responseEntity);
        }
        return model;
    }
}
