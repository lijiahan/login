package com.login.service;

import com.login.entity.CmdMessage;
import com.login.util.cmd.CmdServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DispatchService {
    @Autowired
    private CmdServices cmdServices;

    public String dispatch(CmdMessage cmdMsg) {
        return cmdServices.dispath(cmdMsg);
    }
}
