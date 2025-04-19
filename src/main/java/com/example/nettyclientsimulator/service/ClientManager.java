package com.example.nettyclientsimulator.service;

import com.example.nettyclientsimulator.web.dto.CloseConnectionDTO;
import com.example.nettyclientsimulator.web.dto.ReportQoeDTO;
import com.example.nettyclientsimulator.web.vo.CloseConnectionVO;
import com.example.nettyclientsimulator.web.vo.ReportQoeVO;

/**
 * @author sunxu
 */
public interface ClientManager {

    /**
     * 添加客户端
     * @param batch 总共需要添加客户端的数量
     */
    void addClient(int batch);

    /**
     * 添加延时任务
     * @param reportQoEDTO 参数
     * @return 返回前端信息
     */
    ReportQoeVO reportQoe(ReportQoeDTO reportQoEDTO);

    /**
     * 关闭特定设备的连接
     * @param closeConnectionDTO 需要关闭设备的连接信息
     * @return 返回成功关闭连接的设备信息
     */
    CloseConnectionVO closeConnection(CloseConnectionDTO closeConnectionDTO);

    /**
     * 关机
     */
    void shutdown();
}
