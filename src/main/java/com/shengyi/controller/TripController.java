package com.shengyi.controller;

import com.shengyi.dto.TripCopyDTO;
import com.shengyi.dto.TripSaveDTO;
import com.shengyi.service.TripService;
import com.shengyi.vo.Result;
import com.shengyi.vo.TripVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 补录行程控制器
 */
@RestController
@RequestMapping("/api/trip")
public class TripController {

    @Autowired
    private TripService tripService;

    /**
     * 保存补录行程（新建或更新）
     * @param dto 行程数据传输对象
     * @return 新创建或更新的行程ID
     */
    @PostMapping("/save")
    public Result<String> POST_TRIP_SAVE(@RequestBody TripSaveDTO dto) {
        String tripId = tripService.POST_TRIP_SAVE(dto);
        return Result.success(tripId);
    }
    /**
     * 删除补录行程
     * @param tripId 行程ID
     * @return 操作结果
     */
    @DeleteMapping("/delete/{tripId}")
    public Result<Void> DELETE_TRIP(@PathVariable String tripId) {
        tripService.DELETE_TRIP(tripId);
        return Result.success();
    }
    /**
     * 复制补录行程（可跨报销单）
     * @param dto 行程复制数据传输对象
     * @return 复制后的行程信息
     */
    @PostMapping("/copy")
    public Result<TripVO> POST_TRIP_COPY(@RequestBody TripCopyDTO dto) {
        TripVO tripVO = tripService.POST_TRIP_COPY(dto);
        return Result.success(tripVO);
    }
}
