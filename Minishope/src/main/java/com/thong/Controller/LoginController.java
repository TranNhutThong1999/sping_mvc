package com.thong.Controller;


import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.hibernate.search.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.thong.DTO.NhanVienDTO;
import com.thong.Entity.NhanVien;
import com.thong.InterfaceService.INhanVienService;
import com.thong.Service.MailSerive;
import com.thong.Service.NhanVienService;
import com.thong.Util.MessagesUtil;

@Controller
@RequestMapping("/login")
@SessionAttributes({"user","gioHang","tongSoLuongGioHang"})
public class LoginController {
	@Autowired
	private INhanVienService nhanVienService;
	@Autowired
	private MessageSource mes;
	@Autowired
	private MessagesUtil messagesUtil;
	@Autowired
	private MailSerive mailSerive;
	
	@GetMapping
	public String Default(@RequestParam(required = false,defaultValue ="") String message, ModelMap modelMap) {
		if(!message.equals("")) {
			Map<String, String> messages= messagesUtil.Messages(message);
			modelMap.addAttribute("message", messages.get("message"));
			modelMap.addAttribute("alert", messages.get("alert"));
		}
		return "LogIn";
	}
	
//	@PostMapping
//	public String LogInProccess(@RequestParam String username, @RequestParam String password,
//			 ModelMap modelMap) {
//		NhanVienDTO nv = nhanVienService.checkLogin(username, password);
//		if (nv != null) {
//			modelMap.addAttribute("user", nv);
//		
//		} else {
//			modelMap.addAttribute("SignUp", "sai mật khẩu hoặc tài khoản");
//			modelMap.addAttribute("alert", "danger");
//			System.out.println("ko thành công");
//			return "LogIn";
//		}
//		return "redirect:/Home/";
//	}
	@PostMapping("fb")
	public String ForgetPassword(HttpServletRequest r) {
		System.out.println("vao fb");
		return null;
	}
	
	static boolean isValid(String email) {
		String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
		return email.matches(regex);
	}
}
