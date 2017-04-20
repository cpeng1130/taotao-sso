package com.taotao.sso.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.taotao.common.utils.TaotaoResult;
@Service
public interface LoginService {
	TaotaoResult login(String username,String password,HttpServletRequest request,HttpServletResponse response);
}
