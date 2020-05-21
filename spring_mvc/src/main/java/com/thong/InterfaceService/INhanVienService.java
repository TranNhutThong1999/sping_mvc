package com.thong.InterfaceService;

import java.util.List;

import com.thong.DTO.NhanVienDTO;
import com.thong.Entity.NhanVien;

public interface INhanVienService {
	NhanVienDTO checkLogin(String userName, String password);

	int save(NhanVien nv);

	boolean checkUserName(String userName);

	List<NhanVienDTO> searchNhanVien(String keyWords,String sortBy,String typeSort,int begin,int quantity);
	
	List<NhanVienDTO> findAll(int begin,int quantity,String sortBy,String typeSort);
	
	void delete (List<Integer> idUser);
	
	NhanVienDTO findOneById(int idUser);
	NhanVien findOneById2(int idUser);
	
	boolean checkEmail(String email);
	
	NhanVien findByUserName(String userName);
	
	 void update(NhanVien nv);
	 
	 NhanVien findByToken(String token);
}