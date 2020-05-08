package com.thong.Controller;

import java.util.List;
import java.util.Locale;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.thong.Entity.NhanVien;
import com.thong.InterfaceService.INhanVienService;
import com.thong.Service.MailSerive;

@Controller
@RequestMapping("SignUp/")
@SessionAttributes({ "user", "gioHang", "tongSoLuongGioHang" })
public class SignUpController {
	@Autowired
	private INhanVienService nhanVienService;
	@Autowired
	private MessageSource mes;
	@Autowired
	private MailSerive mailSerive;

	@GetMapping
	public String Default(HttpServletRequest request, ModelMap modelMap) {
		return "SignUp";
	}

	@PostMapping
	public String SignUpProccess(@ModelAttribute @Valid NhanVien nv, BindingResult bindingResult, ModelMap modelMap) {
		System.out.println(nv.toString());
		if (bindingResult.hasErrors()) {
			System.out.println(mes.getMessage("SignUp.fail", null, new Locale("vi")));
			modelMap.addAttribute("SignUp", mes.getMessage("SignUp.fail", null, new Locale("vi")));
			return "SignUp";
		}
		modelMap.addAttribute("user", nv);
		mailSerive.sendMail(nv.getEmail(), "Verify create account", nv.getToken());
		modelMap.addAttribute("message", mes.getMessage("verify.verify", null, new Locale("vi")));
		modelMap.addAttribute("alert", "success");
		return "verify";
	}

	@PostMapping("/confirm-account")
	public String confirm(@RequestParam String token, ModelMap modelMap, HttpSession s) {
		NhanVien nv = (NhanVien) s.getAttribute("user");
		if (nv != null) {
			if (nv.getToken().equals(token)) {
				nv.setEnabled(true);
				nhanVienService.saveNhanVien(nv);
				s.removeAttribute("user");
				return "redirect:/login?message=verify_success";
			} else {
				modelMap.addAttribute("message", "sai code vui lòng kiểm tra code được gửi tới mail");
				return "verify";
			}
		} else {
			return "redirect:/login?message=verify_timeout";
		}
	}

}