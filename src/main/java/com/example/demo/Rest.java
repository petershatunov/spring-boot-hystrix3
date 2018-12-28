package com.example.demo;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api2")
@Slf4j
public class Rest {


    @Autowired
    private Tracer tracer;

    @Autowired
    FeignClientService4 feignClientService4;

    @GetMapping(path = "/test/{millisec}")
    @HystrixCommand(
                    commandProperties=
                                    {@HystrixProperty(
                                                    name="execution.isolation.thread.timeoutInMilliseconds",
                                                    value="5050")},
                    fallbackMethod = "hystrixFall")
    public String testHystrix(@PathVariable int millisec) {

        heavyMethod(millisec);
        heavyMethod(250);

        log.info("span:");
        log.info(tracer.getCurrentSpan().traceIdString());


        String service4 = "";
        try {
            //Thread.sleep(millisec);
            service4 = feignClientService4.getService4(millisec);
        } catch (Exception e) {
            log.info("ex:");
            log.info("e", e);
        }

        return service4;


    }

    public void heavyMethod(int millesec) {

        Span newSpan = tracer.createSpan("hyTst_Heavy!");

        try {
            Thread.sleep(millesec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            newSpan.tag("srv_heavy", "HY_Heavy!");
            newSpan.logEvent(org.springframework.cloud.sleuth.Span.CLIENT_RECV);
            tracer.close(newSpan);
        }

    }


    private String hystrixFall(int millisec) {
        return "Sorry service3 is down at " + millisec + ". Please try again later";
    }


}
