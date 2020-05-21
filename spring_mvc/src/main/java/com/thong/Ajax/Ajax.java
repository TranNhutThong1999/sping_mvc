package com.thong.Ajax;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.persistence.metamodel.SetAttribute;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.tomcat.jni.Local;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.transaction.jta.platform.internal.JOnASJtaPlatform;
import org.hibernate.hql.spi.id.inline.IdsClauseBuilder;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.thong.DTO.NhanVienDTO;
import com.thong.DTO.SanPhamDTO;
import com.thong.Entity.GioHang;
import com.thong.Entity.HinhSanPham;
import com.thong.Entity.NhanVien;
import com.thong.Entity.SanPham;
import com.thong.InterfaceService.INhanVienService;
import com.thong.InterfaceService.ISanPhamService;
import com.thong.Service.MailSerive;
import com.thong.Service.NhanVienService;
import com.thong.Util.SecurityUtil;

@RestController
@RequestMapping("Api/")
@SessionAttributes({ "gioHang", "tongSoLuongGioHang", "SumMoney" })
public class Ajax {
	@Autowired
	private ISanPhamService sanPhamService;

	@Autowired
	SessionFactory sessionFactory;

	@Autowired
	private INhanVienService nhanVienService;

	@Autowired
	private ServletContext context;

	@Autowired
	private MessageSource mes;

	@Autowired // Asysc
	private MailSerive mailSerive;
	
	@Autowired
	private BCryptPasswordEncoder bCrypt;

	@PostMapping(value = "CheckSignUp/", produces = "text/plain;charset=UTF-8")
	public String logInProccess(@RequestBody @Valid NhanVien nv, BindingResult bindingResult) {
		System.out.println(nv.toString());
		if (bindingResult.hasErrors()) {
			System.out.println(bindingResult.getAllErrors().get(0).getDefaultMessage());
			return bindingResult.getAllErrors().get(0).getDefaultMessage();
		}
//		if (nv.getTenDangNhap() != null) {
//			if (nv.getTenDangNhap().length() >= 8 && nv.getTenDangNhap().length() <= 20) {
//				if (isValidUserName(nv.getTenDangNhap()) == false) {
//					return mes.getMessage("NhanVien.tenDangNhap.pattern", null, new Locale("vi"));
//				} else {
//					boolean check = nhanVienService.checkUserName(nv.getTenDangNhap());
//					if (check == true) {
//						return mes.getMessage("NhanVien.tenDangNhap.exist", null, new Locale("vi"));
//					}
//				}
//			} else {
//				return mes.getMessage("NhanVien.tenDangNhap.length", null, new Locale("vi"));
//			}
//		}

//		if (nv.getEmail() != null) {
//			System.out.println(nv.getEmail());
//			if (isValidEmail(nv.getEmail()) == false) {
//				return mes.getMessage("NhanVien.email.pattern", null, new Locale("vi"));
//			}
//		}
//		if (nv.getMatKhau() != null) {
//			if (nv.getMatKhau().length() < 6 || nv.getMatKhau().length() > 20) {
//				return mes.getMessage("NhanVien.matKhau.length", null, new Locale("vi"));
//			} else {
//				System.out.println(isValidMatKhau(nv.getMatKhau()));
//				if (isValidMatKhau(nv.getMatKhau()) == false) {
//					return mes.getMessage("NhanVien.matKhau.pattern", null, new Locale("vi"));
//				}
//			}
//		}

		return "";
	}

	static boolean isValidEmail(String email) {
		String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
		return email.matches(regex);
	}

	static boolean isValidUserName(String tenDangNhap) {
		String regex = "^[a-zA-Z]+[0-9]+";
		return tenDangNhap.matches(regex);
	}

	static boolean isValidMatKhau(String matKhau) {
		String regex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,20}$";
		return matKhau.matches(regex);
	}

	@PostMapping(value = "ThemGioHang/")
	public String themGioHang(@RequestBody GioHang gioHang, HttpSession session, ModelMap modelMap) {
		List<GioHang> data = (List<GioHang>) session.getAttribute("gioHang");
		int tonSoLuongSP = 0;
		if (data == null) {
			gioHang.setSoLuong(1);
			List<GioHang> listGioHang = new ArrayList<GioHang>();
			listGioHang.add(gioHang);
			modelMap.addAttribute("tongSoLuongGioHang", 1);
			session.setAttribute("gioHang", listGioHang);
			return "1";
		} else {
			int index = checkExsts(gioHang.getIdMauSanPham(), gioHang.getIdSizeSanPham(), gioHang.getIdSanPham(), data);
			if (index == -1) {
				gioHang.setSoLuong(1);

				data.add(gioHang);
				tonSoLuongSP = tongSoLuong(data);
			} else {
				int soLuongBanDau = data.get(index).getSoLuong();
				data.get(index).setSoLuong(soLuongBanDau + 1);
				tonSoLuongSP = tongSoLuong(data);
			}
		}
//complete sum money main	
		int sumMoney = SumMoney(data);
		modelMap.addAttribute("SumMoney", sumMoney);
// set quantity sum products
		modelMap.addAttribute("tongSoLuongGioHang", tonSoLuongSP);
		return tongSoLuong(data) + "";
	}

	private int tongSoLuong(List<GioHang> data) {
		int tongSL = 0;
		for (GioHang gh : data) {
			tongSL += gh.getSoLuong();
		}
		return tongSL;
	}

	private int checkExsts(int idMauSanPham, int idSize, int idSanPham, List<GioHang> list) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getIdSanPham() == idSanPham && list.get(i).getIdMauSanPham() == idMauSanPham
					&& list.get(i).getIdSizeSanPham() == idSize) {
				return i;
			}
		}
		return -1;
	}

	@PutMapping(value = "CapNhatGioHang/")
	public String CapNhatGioHang(@RequestBody GioHang gioHang, HttpSession session, ModelMap modelMap) {
		List<GioHang> list = (List<GioHang>) session.getAttribute("gioHang");
		int check = checkExsts(gioHang.getIdMauSanPham(), gioHang.getIdSizeSanPham(), gioHang.getIdSanPham(), list);
		System.out.println(check);
		int soLuongBanDau = list.get(check).getSoLuong();
		int soLuongReal = 0;
		int soLuongSanPham = gioHang.getSoLuong();
		if (soLuongBanDau > soLuongSanPham) {
			soLuongReal = soLuongBanDau - (soLuongBanDau - soLuongSanPham);
		} else if (soLuongBanDau < soLuongSanPham) {
			soLuongReal = soLuongBanDau + (soLuongSanPham - soLuongBanDau);
		} else {
			soLuongReal = soLuongBanDau;
		}
		list.get(check).setSoLuong(soLuongReal);
		modelMap.addAttribute("gioHang", list);
		modelMap.addAttribute("tongSoLuongGioHang", tongSoLuong(list));

//update sum money main		
		int sumMoney = SumMoney(list);
		modelMap.addAttribute("SumMoney", sumMoney);
		return tongSoLuong(list) + " " + sumMoney;

	}

	private int SumMoney(List<GioHang> list) {
		int tonTien = 0;
		for (GioHang gh : list) {
			tonTien += gh.getSoLuong() * gh.getGiaTien();
		}
		return tonTien;
	}

	@DeleteMapping("XoaSanPham/")
	public String XoaSanPham(@RequestBody GioHang gioHang, ModelMap modelMap, HttpSession session) {
		List<GioHang> list = (List<GioHang>) session.getAttribute("gioHang");
		int index = checkExsts(gioHang.getIdMauSanPham(), gioHang.getIdSizeSanPham(), gioHang.getIdSanPham(), list);
		list.remove(index);
		modelMap.addAttribute("gioHang", list);

		int sumMoney = SumMoney((List<GioHang>) session.getAttribute("gioHang"));
		modelMap.addAttribute("SumMoney", sumMoney);

		modelMap.addAttribute("tongSoLuongGioHang", tongSoLuong(list));
		return tongSoLuong(list) + " " + sumMoney;
	}

	@GetMapping(value = "SanPham", produces = "application/json;charset=UTF-8")
	public List<SanPhamDTO> pagination(@RequestParam int trangBatDau, @RequestParam int soLuongSP,
			@RequestParam(required = false, defaultValue = "-1") int idDanhMuc,
			@RequestParam(required = false) String sortBy, @RequestParam(required = false) String typeSort,
			@RequestParam(required = false) String keyWords) {

		if (keyWords == null) {
			if (idDanhMuc == Integer.valueOf("-1")) {// get All product
				if (sortBy == null && typeSort == null) {
					// paging
					return sanPhamService.findAll(trangBatDau, soLuongSP, null, null);
				} else {
					System.out.println(typeSort + " nulll");
					// paging + sort
					return sanPhamService.findAll(trangBatDau, soLuongSP, typeSort, sortBy);
				}
			} else { // get Product By Category
				if (sortBy == null && typeSort == null) {
					// paging by category
					return sanPhamService.findByCategory(idDanhMuc, trangBatDau, soLuongSP, null, null);
				} else {
					// paging + sort by category
					return sanPhamService.findByCategory(idDanhMuc, trangBatDau, soLuongSP, typeSort, sortBy);
				}
			}
		} else {
			if (sortBy == null && typeSort == null) {
				// paging by search
				return sanPhamService.search(keyWords, trangBatDau, soLuongSP, null, null);
			} else {
				// paging + sort by search
				return sanPhamService.search(keyWords, trangBatDau, soLuongSP, typeSort, sortBy);
			}
		}
	}

	@DeleteMapping("Delete_Product/")
	public void deleteProductManager(@RequestBody List<Integer> ob) {
		sanPhamService.delete(ob);
	}

	@PostMapping("create_Post")
	public void createPost(@RequestBody SanPhamDTO ob) {
		sanPhamService.create(ob);
	}

	@PutMapping("update_Post")
	public void updatePost(@RequestBody SanPhamDTO ob) {
		System.out.println(ob.toString());
		HinhSanPham hsp = new HinhSanPham();
		// hsp.setLocation(ob.get);
		sanPhamService.update(ob);
	}

	@PostMapping("upLoadFile")
	public String upLoadFile(MultipartHttpServletRequest request, @RequestParam(required = false) String editor)
			throws IllegalStateException, IOException {
		String part = context.getRealPath("resources/Images/");
		JSONObject json = new JSONObject();
		Iterator<String> name = request.getFileNames();
		while (name.hasNext()) {
			MultipartFile f = request.getFile(name.next());
			File file = new File(part + f.getOriginalFilename());
			System.out.println("sssssssssssssssssssssss" + file.getAbsolutePath());
			f.transferTo(file);
			json.put("fileName", f.getOriginalFilename());
			json.put("url", "/Minishope/resources/Images/" + f.getOriginalFilename());
			json.put("uploaded", 1);
		}
		// System.out.println(json.toString());
		return json.toString();

	}

	@GetMapping("NhanVien/")
	public List<NhanVienDTO> paginationUser(@RequestParam int trangBatDau, @RequestParam int soLuongSP,
			@RequestParam(required = false) String typeSort, @RequestParam(required = false) String sortBy,
			@RequestParam(required = false) String keyWords) {
		System.out.println(soLuongSP + "sss");
		if (keyWords == null) {
			if (typeSort == null && sortBy == null) {
				return nhanVienService.findAll(trangBatDau, soLuongSP, null, null);
			} else {
				return nhanVienService.findAll(trangBatDau, soLuongSP, sortBy, typeSort);
			}
		} else {
			if (typeSort == null && sortBy == null) {
				return nhanVienService.searchNhanVien(keyWords, null, null, trangBatDau, soLuongSP);
			} else {
				return nhanVienService.searchNhanVien(keyWords, sortBy, typeSort, trangBatDau, soLuongSP);
			}
		}

	}

	@DeleteMapping("Delete_User/")
	public String deleteUser(@RequestBody List<Integer> idUser) {
		nhanVienService.delete(idUser);
		return "";
	}

	@GetMapping("OneNhanVien/")
	public NhanVienDTO oneNhanVien(@RequestParam int idUser) {
		return nhanVienService.findOneById(idUser);
	}

	@GetMapping("OneSanPham/")
	public SanPhamDTO OneSanPham(@RequestParam int idSanPham) {
		return sanPhamService.findOneById(idSanPham);
	}

	@PostMapping(value = "addUser/", produces = "application/json;charset=UTF-8")
	public String sigUpAPI(@RequestBody NhanVien nv) {
		System.out.println(nv.toString());
		JSONObject json = new JSONObject();
		validUser(nv, json);
		if (json.length() == 0) {
			nhanVienService.save(nv);
			json.put("DangKy", "true");
			return json.toString();
		}
		json.put("DangKy", "false");
		return json.toString();
	}

	private JSONObject validUser(NhanVien nv, JSONObject jsonObject) {
//valid ten dang nhap
		if (nv.getTenDangNhap() != null && !nv.getTenDangNhap().equals("")) {
			System.out.println(!nv.getTenDangNhap().equals(""));
			if (nv.getTenDangNhap().length() >= 8 && nv.getTenDangNhap().length() <= 20) {
				if (isValidUserName(nv.getTenDangNhap()) == false) {
					jsonObject.put("tenDangNhap",
							mes.getMessage("NhanVien.tenDangNhap.pattern", null, new Locale("vi")));
				} else {
					boolean check = nhanVienService.checkUserName(nv.getTenDangNhap());
					if (check == true) {
						jsonObject.put("tenDangNhap",
								mes.getMessage("NhanVien.tenDangNhap.exist", null, new Locale("vi")));
					}
				}
			} else {
				jsonObject.put("tenDangNhap", mes.getMessage("NhanVien.tenDangNhap.length", null, new Locale("vi")));
			}
		} else {
			jsonObject.put("tenDangNhap", mes.getMessage("NhanVien.tenDangNhap.NotNull", null, new Locale("vi")));
		}
//valid email
//		if (nv.getEmail() != null && !nv.getEmail().equals("")) {
//			System.out.println(nv.getEmail());
//			if (isValidEmail(nv.getEmail()) == false) {
//				jsonObject.put("email", mes.getMessage("NhanVien.email.pattern", null, new Locale("vi")));
//			}
//		} else {
//			jsonObject.put("email", mes.getMessage("NhanVien.email.NotNull", null, new Locale("vi")));
//		}
// valid mat khau
		if (nv.getMatKhau() != null && !nv.getMatKhau().equals("")) {
			if (nv.getMatKhau().length() < 6 || nv.getMatKhau().length() > 20) {
				jsonObject.put("matKhau", mes.getMessage("NhanVien.matKhau.length", null, new Locale("vi")));
			} else {
				System.out.println(isValidMatKhau(nv.getMatKhau()));
				if (isValidMatKhau(nv.getMatKhau()) == false) {
					jsonObject.put("matKhau", mes.getMessage("NhanVien.matKhau.pattern", null, new Locale("vi")));
				}
			}
		} else {
			jsonObject.put("matKhau", mes.getMessage("NhanVien.matKhau.NotNull", null, new Locale("vi")));
		}
		return jsonObject;
	}

	@GetMapping("sendToken")
	public void sendToken(HttpSession s) {
		NhanVien nv = (NhanVien) s.getAttribute("user");
		mailSerive.sendMail(nv.getEmail(), "Verify create account", nv.getToken());
	}

	@PostMapping(value = "sendTokenPassword", produces = "text/phain;charset=UTF-8")
	public String sendTokenPassword(@RequestParam String mail, @RequestParam String userName) {
		NhanVien nv = nhanVienService.findByUserName(userName);
		if (nv != null) {
			nv.setTokenRamdom();
			nv.setTimeTokenFuture(5);
			nhanVienService.update(nv);
			mailSerive.sendMail(mail, "Verify create account", "Mã code dùng để thay đổi mật khẩu: " + nv.getToken());
			return "ok";
		} else {
			return "Tên Đăng Nhập không tồn tại";
		}
	}

	@PostMapping(value = "changePW", produces = "text/phain;charset=UTF-8")
	public String checkChangePW(@RequestParam String token, @RequestParam String password) {
		JSONObject json = new JSONObject();
		NhanVien nv = nhanVienService.findByToken(token.trim());
		if (nv == null) {
			json.put("token", "Code không đúng");
		} else if (nv.isAfterTime()) {
			json.put("token", "Quá thời hạn đổi mật khẩu vui lòng thực hiện lại");
		} else {
			if (password.length() < 6 || password.length() > 20) {
				json.put("matKhau", mes.getMessage("NhanVien.matKhau.length", null, new Locale("vi")));
			} else {
				if (isValidMatKhau(password) == false) {
					json.put("matKhau", mes.getMessage("NhanVien.matKhau.pattern", null, new Locale("vi")));
				}
			}
		}

		if (json.length() == 0) {
			nv.setMatKhau(bCrypt.encode(password));
			nv.setToken("");
			json.put("DangKy", "true");
			return json.toString();
		}
		return json.toString();
	}

	@PutMapping("HandleBan")
	public void handleBan(@RequestBody String json,HttpServletRequest re,HttpServletResponse res) {
		System.out.println(json);
		JSONObject j = new JSONObject(json);// idNhanVien

		NhanVien nv = nhanVienService.findOneById2(Integer.valueOf(j.getString("idNhanVien")));
		nv.setEnabled(Boolean.parseBoolean(j.getString("isEnabled")));
		nhanVienService.update(nv);
	//	SecurityUtil.logOutUser(nv.getTenDangNhap(), re, res);
	}
	
}
