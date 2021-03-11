package com.login.util.cmd;
import com.login.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

public class CmdServiceConstruct {
    private static final Logger logger = LoggerFactory.getLogger(CmdServiceConstruct.class);

    public static void constructService(CmdService cmdService) {
        String handlerName = cmdService.getHandlerName();
        RestTemplate restTemplate = (RestTemplate) SpringUtil.getBean(RestTemplate.class);
        switch (handlerName) {
            default: {
                ICmdHandler iCmdHandler = (req)->{
                    return restTemplate.getForObject("http://" + cmdService.getHandlerInfo().getUrl(), String.class);
                };
                cmdService.setCmdHandler(iCmdHandler);
            }
        }
    }
}
