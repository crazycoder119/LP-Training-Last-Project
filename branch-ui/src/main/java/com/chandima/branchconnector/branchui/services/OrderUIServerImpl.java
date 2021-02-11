package com.chandima.branchconnector.branchui.services;

import com.chandima.branchconnector.branchui.config.AccessToken;
import com.chandima.branchconnector.commons.model.orderservice.Order;
import com.chandima.branchconnector.commons.model.productservice.Product;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderUIServerImpl implements OrderUIServer {
    @LoadBalanced
    @Autowired
    RestTemplate restTemplate;

    @Override
    public Model loadOrders(Model model) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", AccessToken.getAccessToken());
        HttpEntity<Order> customerHttpEntity = new HttpEntity<>(httpHeaders);
        try {
            ResponseEntity<Order[]> responseEntity = restTemplate.exchange("http://orderservice/services/getAllOrders", HttpMethod.GET, customerHttpEntity, Order[].class);
            model.addAttribute("orderList", responseEntity.getBody());
            System.out.println(responseEntity.getBody().length+">>>>>>>>>>>>");
        } catch (HttpStatusCodeException e) {
            ResponseEntity responseEntity = ResponseEntity.status(e.getRawStatusCode()).headers(e.getResponseHeaders()).body(e.getResponseBodyAsString());
            model.addAttribute("error", responseEntity);
        }
        return model;
    }

    @Override
    public Model addOrder(Order order, Model model) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add("Authorization", AccessToken.getAccessToken());
        JSONObject customerJsonObject = new JSONObject();
        customerJsonObject.put("id",order.getId());
        customerJsonObject.put("customerID", order.getCustomerID());
        customerJsonObject.put("productID", order.getProductID());
        customerJsonObject.put("quantity", order.getQuantity());
        HttpEntity<String> request =
                new HttpEntity<String>(customerJsonObject.toString(), httpHeaders);
        try {
            Order check = restTemplate.postForObject("http://orderservice/services/addOrder", request, Order.class);
            model.addAttribute("order", check);
        } catch (HttpStatusCodeException e) {
            ResponseEntity responseEntity = ResponseEntity.status(e.getRawStatusCode()).headers(e.getResponseHeaders()).body(e.getResponseBodyAsString());
            model.addAttribute("error", responseEntity);
        }
        return model;
    }
}
