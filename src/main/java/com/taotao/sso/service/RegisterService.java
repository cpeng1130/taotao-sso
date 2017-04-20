package com.taotao.sso.service;

import org.springframework.stereotype.Service;

import com.taotao.common.utils.TaotaoResult;
import com.taotao.pojo.TbUser;
@Service
public interface RegisterService {

	TaotaoResult checkData(String param, int type);
	TaotaoResult register(TbUser user);
}
