package com.example.nettyclientsimulator.web.controller;

import com.example.nettyclientsimulator.service.impl.ClientManagerImpl;
import com.example.nettyclientsimulator.web.dto.ClientDTO;
import com.example.nettyclientsimulator.web.dto.CloseConnectionDTO;
import com.example.nettyclientsimulator.web.dto.ReportQoeDTO;
import com.example.nettyclientsimulator.web.vo.ClientVO;
import com.example.nettyclientsimulator.web.vo.CloseConnectionVO;
import com.example.nettyclientsimulator.web.vo.ReportQoeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author sunxu
 */
@RestController
@Slf4j
public class ClientController {

    private final ClientManagerImpl clientManager;

    public ClientController(ClientManagerImpl clientManager) {
        this.clientManager = clientManager;
    }

    @PostMapping("/client/add")
    public ClientVO add(@RequestBody ClientDTO clientDTO) {
        clientManager.addClient(clientDTO.getBatch());
        return ClientVO.builder()
                .batch(clientDTO.getBatch())
                .build();
    }

    @PostMapping("/client/reportQoe")
    public ReportQoeVO add(@RequestBody ReportQoeDTO reportQoeDTO) {
        return clientManager.reportQoe(reportQoeDTO);

    }

    @PostMapping("/client/closeConnection")
    public CloseConnectionVO close(@RequestBody CloseConnectionDTO closeConnectionDTO) {
        return clientManager.closeConnection(closeConnectionDTO);
    }
}

