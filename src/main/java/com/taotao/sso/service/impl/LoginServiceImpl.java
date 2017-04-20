package com.taotao.sso.service.impl;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.taotao.common.utils.CookieUtils;
import com.taotao.common.utils.JsonUtils;
import com.taotao.common.utils.TaotaoResult;
import com.taotao.mapper.TbUserMapper;
import com.taotao.pojo.TbUser;
import com.taotao.pojo.TbUserExample;
import com.taotao.pojo.TbUserExample.Criteria;
import com.taotao.sso.component.JedisClient;
import com.taotao.sso.service.LoginService;
@Service
public class LoginServiceImpl implements LoginService{

	@Autowired
	private TbUserMapper tbUserMapper;
	@Autowired
	private JedisClient jedisClient;
	@Value("${REDIS_SESSION_KEY}")
	private String REDIS_SESSION_KEY;
	@Value("${SESSION_EXPIRE}")
	private Integer SESSION_EXPIRE;
	@Override
	public TaotaoResult login(String username, String password, HttpServletRequest request,
			HttpServletResponse response) {
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(username);
		
		List<TbUser> list = tbUserMapper.selectByExample(example);
		
		if(list==null || list.isEmpty()){
			return TaotaoResult.build(400, "username or password is invaild");
		}
		TbUser user = list.get(0);
	
		if(!user.getPassword().equals(DigestUtils.md5DigestAsHex(password.getBytes()))){
			return TaotaoResult.build(400, "username or password is invaild");
		}
		
		String token =UUID.randomUUID().toString();
		
		//key : REDIS_SESSION:{TOKEN}
		//value:  JSON(USER)
		user.setPassword(null);
		
		jedisClient.set(REDIS_SESSION_KEY+":"+token, JsonUtils.objectToJson(user));
		jedisClient.expire(REDIS_SESSION_KEY+":"+token, SESSION_EXPIRE);
		// cookie
		CookieUtils.setCookie(request, response, "TT_TOKEN", token);
		return TaotaoResult.ok(token);
	}
	@Override
	public TaotaoResult getUserByToken(String token) {
		String json = jedisClient.get(REDIS_SESSION_KEY+":"+token);
		
		if(StringUtils.isBlank(json)){
			return TaotaoResult.build(400, "Session was expired");
		}
		TbUser user = JsonUtils.jsonToPojo(json, TbUser.class);
		
		user.setPassword(null);
		jedisClient.expire(REDIS_SESSION_KEY+":"+token, SESSION_EXPIRE);
		return TaotaoResult.ok(user);
	}

}
