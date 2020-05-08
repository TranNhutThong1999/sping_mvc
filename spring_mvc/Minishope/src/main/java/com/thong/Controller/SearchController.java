package com.thong.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.thong.DTO.SanPhamDTO;
import com.thong.Entity.SanPham;
import com.thong.InterfaceService.ISanPhamService;
import com.thong.Service.SanPhamService;

@Controller
@RequestMapping("Search/")
public class SearchController {
	@Autowired
	private ISanPhamService sanPhamService;

	private final int numberShows = 4;
	@GetMapping("SanPham")
	public String SearchSanPham(@RequestParam String keyWords, ModelMap modelMap) {
		List<SanPhamDTO> listAll = sanPhamService.searchByFTS(keyWords,-1,4,null,null);
		List<SanPhamDTO> list = sanPhamService.searchByFTS(keyWords,0,numberShows,null,null);
		int du = listAll.size() % numberShows;
		int in = listAll.size() / numberShows;
		if (du > 0) {
			in = in + 1;
		}
		modelMap.addAttribute("numberPagination", in);
		modelMap.addAttribute("listSanPham", list);
		modelMap.addAttribute("keyWords", keyWords);
		if(list==null || list.size()==0) {
			modelMap.addAttribute("messenger_Search_Error", "không tìm thấy sản phẩm");
			return "forward:/Home/";
		}
		return "SanPham";
	}
}