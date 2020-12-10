package com.example.demo.service.ParamsServiceImp;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.DTO.ParamsDTO;
import com.example.demo.entity.Params;
import com.example.demo.repository.ParamsRepository;
import com.example.demo.service.IParamsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ParamsServiceImp implements IParamsService {

  @Autowired
  ParamsRepository paramsRepository;
  @Autowired
  RedisTemplate<String,String> redisTemplate;


  @Override
  public Params insert(ParamsDTO dto) {
    String str = redisTemplate.opsForValue().get(dto.getParam1() + ":" + dto.getParam2());
    if(StringUtils.hasText(str)) {
      return JSONObject.parseObject(str,Params.class);
    }
    Params param = paramsRepository
        .findByParam1AndParam2(dto.getParam1(), dto.getParam2());
    if(param == null) {
      Params paramsEntity = new Params();
      paramsEntity.setParam1(dto.getParam1());
      paramsEntity.setParam2(dto.getParam2());
      param = paramsRepository.save(paramsEntity);
    }
    String paramJSON = JSONObject.toJSONString(param);
    redisTemplate.opsForValue().set(param.getParam1()+":"+param.getParam2(),paramJSON);
    return param;
  }


}
