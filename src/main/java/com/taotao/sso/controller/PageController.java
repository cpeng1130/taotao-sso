package com.taotao.sso.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {

	@RequestMapping("/page/login")
	public String showLogin(String redirectURL,Model model){
		model.addAttribute("redirect",redirectURL);
		return "login";
	}
	
	@RequestMapping("/page/register")
	public String showRegister(){
		return "register";
	}
}
