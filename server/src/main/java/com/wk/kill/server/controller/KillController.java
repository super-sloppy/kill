package com.wk.kill.server.controller;

import com.wk.kill.api.enums.StatusCode;
import com.wk.kill.api.response.BaseResponse;
import com.wk.kill.model.dto.KillSuccessUserInfo;
import com.wk.kill.model.mapper.ItemKillSuccessMapper;
import com.wk.kill.server.dto.KillDto;
import com.wk.kill.server.service.IKillService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Controller
public class KillController {
    private static final Logger Log = LoggerFactory.getLogger(KillController.class);

    private static final String prefix = "kill";

    @Resource
    private IKillService killService;

    @Resource
    private ItemKillSuccessMapper itemKillSuccessMapper;


    /**
     * 商品秒杀核心业务逻辑
     * @param dto
     * @param result
     * @return
     */
    @RequestMapping(value = prefix + "/execute", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public BaseResponse execute(@RequestBody @Validated KillDto dto, BindingResult result){
        if(result.hasErrors() || dto.getKillId() <= 0){
            return new BaseResponse(StatusCode.InvalidParams);
        }
        BaseResponse response = new BaseResponse(StatusCode.Success);
        try{
            boolean res = killService.killItem(dto.getKillId(), dto.getUserId());
            if(!res){
                return new BaseResponse(StatusCode.Fail.getCode(), "商品已经抢购完啦，或者不在时间段内");
            }

        }catch (Exception e){
            response = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
        }
        return response;
    }

    @RequestMapping(value = prefix + "/record/detail/{orderNo}", method = RequestMethod.GET)
    public String killRecordDetail(@PathVariable String orderNo, ModelMap modelMap){
        if(StringUtils.isBlank(orderNo)){
            return "error";
        }
        KillSuccessUserInfo info = itemKillSuccessMapper.selectByCode(orderNo);
        if(info ==  null){
            return "error";
        }
        modelMap.put("info", info);
        return "killRecord";
    }


    //抢购成功
    @RequestMapping(value = prefix + "/execute/success", method = RequestMethod.GET)
    public String executeSuccess(){
        return "executeSuccess";
    }
    //抢购失败
    @RequestMapping(value = prefix + "/execute/fail", method = RequestMethod.GET)
    public String executeFail(){
        return "executeFail";
    }










}
