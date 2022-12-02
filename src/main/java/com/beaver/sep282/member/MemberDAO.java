package com.beaver.sep282.member;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.beaver.db.manager.BeaverDBManager;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

public class MemberDAO {

	public static void signUp(HttpServletRequest req) {
		// 일단 파일업로드 시도
		// 10 * 1024 * 1024
		String path = null;
		MultipartRequest mr = null;
		try {
			path = req.getServletContext().getRealPath("img");
			System.out.println(path);
			mr = new MultipartRequest(req, path, 10 * 1024 * 1024, "EUC-KR",
					new DefaultFileRenamePolicy());
			
		} catch (Exception e) { // 파일 업로드 실패의 경우
			e.printStackTrace();
			req.setAttribute("r", "회원가입 실패(사진파일 용량)");
			return; // 업로드에 실패하면 뒤에 남은 DB작업 하지 말자 (강제 종료)
		}
		
		// 파일 업로드 성공시에 계속 진행
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = BeaverDBManager.connect("beaverPool");
			
			String id = mr.getParameter("m_id");
			String pw = mr.getParameter("m_pw");
			String name = mr.getParameter("m_name");
			String phone = mr.getParameter("m_phone");
			String y = mr.getParameter("m_y");
			int m = Integer.parseInt(mr.getParameter("m_m"));
			int d = Integer.parseInt(mr.getParameter("m_d"));
			String birthday = String.format("%s%02d%02d", y, m ,d);
			String photo = mr.getFilesystemName("m_photo");
			photo = URLEncoder.encode(photo, "EUC-KR").replace("+", " ");
			
			String sql = "insert into sep28_sns values("
					+ "?, ?, ?, ?, to_date(?, 'YYYYMMDD'), ?)";
			pstmt = con.prepareStatement(sql);
			
			pstmt.setString(1, id);
			pstmt.setString(2, pw);
			pstmt.setString(3, name);
			pstmt.setString(4, phone);
			pstmt.setString(5, birthday);
			pstmt.setString(6, photo);
			
			if (pstmt.executeUpdate() == 1) {
				req.setAttribute("r", "회원가입 성공!");
			}
		} catch (Exception e) { // ID중복, DB서버 연결 상태가 :p
			e.printStackTrace();
			// DB쪽 문제로 가입은 실패지만, 사진파일이 업로드는 되어있는 상태
			// 사진파일 지워줘야(Java로 - 파일명에 한글처리가 안되어있어야)
			File f = new File(path + "/" + mr.getFilesystemName("m_photo"));
			f.delete();
			req.setAttribute("r", "회원가입 실패(DB서버)");
		}
		BeaverDBManager.close(con, pstmt, null);
	}
	
	public static void login(HttpServletRequest req, HttpServletResponse res) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			con = BeaverDBManager.connect("beaverPool");
			
			req.setCharacterEncoding("EUC-KR");
			String id = req.getParameter("m_id");
			String pw = req.getParameter("m_pw");
			
			// String sql = "select * from sep28_sns"; // 회원이 1000명이면 그거 다 ???
			// String sql = "select * from sep28_sns where m_id = ? and m_pw = ?";
			// 해킹 기법 - SQL Injection
			//		보안상 취약점을 이용해서 데이터베이스가 비정상적인 동작을 하게 하는 기법
			String sql = "select * from sep28_sns where m_id = ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			
			if (rs.next()) { // 데이터가 있는지 + 그 데이터를 가리키게
				String dbPw = rs.getString("m_pw");
				if (dbPw.equals(pw)) {
					Member m = new Member(rs.getString("m_id"), dbPw, rs.getString("m_name"),
							rs.getString("m_phone"), rs.getDate("m_birthday"), 
							rs.getString("m_photo"));
					req.getSession().setAttribute("loginMember", m);
					req.getSession().setMaxInactiveInterval(600);
					
					Cookie c = new Cookie("lastLoginId", id);
					c.setMaxAge(60 * 60 * 24 * 5);
					res.addCookie(c);
					req.setAttribute("r", "로그인성공");
				} else {
					req.setAttribute("r", "로그인실패(PW오류)");
				}
			} else { // 데이터가 없는 경우
				req.setAttribute("r", "로그인실패(미가입ID)");
			}
		} catch (Exception e) {
			e.printStackTrace();
			req.setAttribute("r", "로그인실패(DB서버)");
		}
		BeaverDBManager.close(con, pstmt, rs);
	}
	
	public static boolean loginCheck(HttpServletRequest req) {
		Member m = (Member) req.getSession().getAttribute("loginMember");
		if (m != null) {
			// 로그인 성공 + 상태 유지시
			req.setAttribute("lp", "member/welcome.jsp");
			return true;
		}
		// 로그인상태가 아니거나 + 로그인 실패시
		req.setAttribute("lp", "member/login.jsp");
		return false;
	}
	
	public static void logout(HttpServletRequest req) {
		// 세션 끊기 : 다른 세션도 들어가있기 때문에 비추
		// req.getSession().setMaxInactiveInterval(-1);
		
		// Member에 대한 session만 null로 바꿔주면...
		req.getSession().setAttribute("loginMember", null);
		req.setAttribute("r", "로그아웃 성공");
	}
	
	public static void delete(HttpServletRequest request) {
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = BeaverDBManager.connect("beaverPool");
			Member m = (Member) request.getSession().getAttribute("loginMember");
			String m_id = m.getM_id();

			String sql = "delete from sep28_sns where m_id=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, m_id);

			if (pstmt.executeUpdate() == 1) {
				request.setAttribute("r", "탈퇴성공");
				// 그 회원이 쓴 글?
				// 그 사람이 올린 파일들?
				// 그 사람의 프사 -> 지워야
			// 지금 DB에서 지웠음
			// session에는 여전히 그 정보 남아있음(DeleteMemberController의 16번 위치 진행중)
			//	(18번줄에서 session이 날아감)				
				String m_photo = m.getM_photo(); // %2D.jpg (한글처리 되어있음)
				m_photo = URLDecoder.decode(m_photo, "euc-kr"); // gogae.jpg
				String path = request.getServletContext().getRealPath("img");
				File f = new File(path + "/" + m_photo);
				f.delete();
			} else {
				request.setAttribute("r", "이미 탈퇴처리됨");
			}

		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("r", "탈퇴실패(DB서버 사망)");
		}
		BeaverDBManager.close(con, pstmt, null);
	}

	public static void update(HttpServletRequest request) {
		// 파일 업로드 실패하면(파일 용량초과) -> 거기서 땡!
		String path = null;
		MultipartRequest mr = null;
		Member m = (Member) request.getSession().getAttribute("loginMember");
		String old_m_photo = m.getM_photo();
		String new_m_photo = null;
		try {
			path = request.getServletContext().getRealPath("img");
			mr = new MultipartRequest(request, path, 10 * 1024 * 1024, "euc-kr", new DefaultFileRenamePolicy());

			new_m_photo = mr.getFilesystemName("m_photo"); // 선택한 파일명
			if (new_m_photo != null) { // 이 있으면 (새로운 파일을 넣었으면)
				new_m_photo = URLEncoder.encode(new_m_photo, "euc-kr").replace("+", " ");
			} else { // 이 없으면 (새로운 파일을 안넣었으면)
				new_m_photo = old_m_photo;
			}

		} catch (Exception e) {
			e.printStackTrace(); // NullPointerException은 여기서 뜨는게 아니야!
			System.out.println("파일용량..");
			return;
		}

		// 여기까지 진행하는데 별 일 없었다

		// 파일 사이즈가 10MB보다 적은걸로 잘 선택해서 - 파일 업로드 성공
		// 파일 선택 안해서(0MB)

		// 파일 선택을 했다 : 프사 바꾸겠다 -> 새 프사 파일명을 DB에 넣어야 -> 새 프사 파일명을 확보
		// 기존 프사 파일 삭제도 해야 -> 원래 프사 파일명을 확보

		// 파일 선택을 안하면 : 프사는 안바꾸겠다 -> 기존 프사 파일명을 DB에 넣어야 -> 원래 프사 파일명을 확보

		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = BeaverDBManager.connect("beaverPool");
			String m_id = mr.getParameter("m_id");
			String m_pw = mr.getParameter("m_pw");
			String m_name = mr.getParameter("m_name");
			String m_phone = mr.getParameter("m_phone");
			String m_y = mr.getParameter("m_y");
			int m_m = Integer.parseInt(mr.getParameter("m_m"));
			int m_d = Integer.parseInt(mr.getParameter("m_d"));
			String m_birthday = String.format("%s%02d%02d", m_y, m_m, m_d);

			String sql = "update sep28_sns set m_pw=?, m_name=?, m_phone=?, m_birthday=to_date(?, 'YYYYMMDD'), m_photo=? where m_id=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, m_pw);
			pstmt.setString(2, m_name);
			pstmt.setString(3, m_phone);
			pstmt.setString(4, m_birthday);
			pstmt.setString(5, new_m_photo); // ㄴㅇㄱ!!
			pstmt.setString(6, m_id);
			if (pstmt.executeUpdate() == 1) {
				request.setAttribute("r", "정보수정 성공");
				if (!new_m_photo.equals(old_m_photo)) { // 프사를 바꿨으면
					new File(path + "/" + URLDecoder.decode(old_m_photo, "euc-kr")).delete(); // 기존 사진파일 지우기
				}
				SimpleDateFormat sdf =new SimpleDateFormat("yyyyMMdd");
				m = new Member(m_id, m_pw, m_name, m_phone, sdf.parse(m_birthday), new_m_photo);
				request.getSession().setAttribute("loginMember", m);
			} else {
				request.setAttribute("r", "정보수정 실패");
				if (!new_m_photo.equals(old_m_photo)) { // 새로운 사진 올라간거 삭제
					new File(path + "/" + URLDecoder.decode(new_m_photo, "euc-kr")).delete();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("r", "정보수정 실패(DB문제)");
			if (!new_m_photo.equals(old_m_photo)) {
				try {
					new File(path + "/" + URLDecoder.decode(new_m_photo, "euc-kr")).delete();
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
			}
		}
		BeaverDBManager.close(con, pstmt, null);
	}
}

























