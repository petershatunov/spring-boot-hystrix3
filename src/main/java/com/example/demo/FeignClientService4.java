package com.example.demo;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(value = "fourth-service")
public interface FeignClientService4 {

    @RequestMapping(value = "/api2/test/{millisec}", method = { RequestMethod.GET})
    String getService4(@PathVariable("millisec") int millisec);

}
