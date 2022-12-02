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
		// �ϴ� ���Ͼ��ε� �õ�
		// 10 * 1024 * 1024
		String path = null;
		MultipartRequest mr = null;
		try {
			path = req.getServletContext().getRealPath("img");
			System.out.println(path);
			mr = new MultipartRequest(req, path, 10 * 1024 * 1024, "EUC-KR",
					new DefaultFileRenamePolicy());
			
		} catch (Exception e) { // ���� ���ε� ������ ���
			e.printStackTrace();
			req.setAttribute("r", "ȸ������ ����(�������� �뷮)");
			return; // ���ε忡 �����ϸ� �ڿ� ���� DB�۾� ���� ���� (���� ����)
		}
		
		// ���� ���ε� �����ÿ� ��� ����
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
				req.setAttribute("r", "ȸ������ ����!");
			}
		} catch (Exception e) { // ID�ߺ�, DB���� ���� ���°� :p
			e.printStackTrace();
			// DB�� ������ ������ ��������, ���������� ���ε�� �Ǿ��ִ� ����
			// �������� �������(Java�� - ���ϸ� �ѱ�ó���� �ȵǾ��־��)
			File f = new File(path + "/" + mr.getFilesystemName("m_photo"));
			f.delete();
			req.setAttribute("r", "ȸ������ ����(DB����)");
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
			
			// String sql = "select * from sep28_sns"; // ȸ���� 1000���̸� �װ� �� ???
			// String sql = "select * from sep28_sns where m_id = ? and m_pw = ?";
			// ��ŷ ��� - SQL Injection
			//		���Ȼ� ������� �̿��ؼ� �����ͺ��̽��� ���������� ������ �ϰ� �ϴ� ���
			String sql = "select * from sep28_sns where m_id = ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			
			if (rs.next()) { // �����Ͱ� �ִ��� + �� �����͸� ����Ű��
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
					req.setAttribute("r", "�α��μ���");
				} else {
					req.setAttribute("r", "�α��ν���(PW����)");
				}
			} else { // �����Ͱ� ���� ���
				req.setAttribute("r", "�α��ν���(�̰���ID)");
			}
		} catch (Exception e) {
			e.printStackTrace();
			req.setAttribute("r", "�α��ν���(DB����)");
		}
		BeaverDBManager.close(con, pstmt, rs);
	}
	
	public static boolean loginCheck(HttpServletRequest req) {
		Member m = (Member) req.getSession().getAttribute("loginMember");
		if (m != null) {
			// �α��� ���� + ���� ������
			req.setAttribute("lp", "member/welcome.jsp");
			return true;
		}
		// �α��λ��°� �ƴϰų� + �α��� ���н�
		req.setAttribute("lp", "member/login.jsp");
		return false;
	}
	
	public static void logout(HttpServletRequest req) {
		// ���� ���� : �ٸ� ���ǵ� ���ֱ� ������ ����
		// req.getSession().setMaxInactiveInterval(-1);
		
		// Member�� ���� session�� null�� �ٲ��ָ�...
		req.getSession().setAttribute("loginMember", null);
		req.setAttribute("r", "�α׾ƿ� ����");
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
				request.setAttribute("r", "Ż�𼺰�");
				// �� ȸ���� �� ��?
				// �� ����� �ø� ���ϵ�?
				// �� ����� ���� -> ������
			// ���� DB���� ������
			// session���� ������ �� ���� ��������(DeleteMemberController�� 16�� ��ġ ������)
			//	(18���ٿ��� session�� ���ư�)				
				String m_photo = m.getM_photo(); // %2D.jpg (�ѱ�ó�� �Ǿ�����)
				m_photo = URLDecoder.decode(m_photo, "euc-kr"); // gogae.jpg
				String path = request.getServletContext().getRealPath("img");
				File f = new File(path + "/" + m_photo);
				f.delete();
			} else {
				request.setAttribute("r", "�̹� Ż��ó����");
			}

		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("r", "Ż�����(DB���� ���)");
		}
		BeaverDBManager.close(con, pstmt, null);
	}

	public static void update(HttpServletRequest request) {
		// ���� ���ε� �����ϸ�(���� �뷮�ʰ�) -> �ű⼭ ��!
		String path = null;
		MultipartRequest mr = null;
		Member m = (Member) request.getSession().getAttribute("loginMember");
		String old_m_photo = m.getM_photo();
		String new_m_photo = null;
		try {
			path = request.getServletContext().getRealPath("img");
			mr = new MultipartRequest(request, path, 10 * 1024 * 1024, "euc-kr", new DefaultFileRenamePolicy());

			new_m_photo = mr.getFilesystemName("m_photo"); // ������ ���ϸ�
			if (new_m_photo != null) { // �� ������ (���ο� ������ �־�����)
				new_m_photo = URLEncoder.encode(new_m_photo, "euc-kr").replace("+", " ");
			} else { // �� ������ (���ο� ������ �ȳ־�����)
				new_m_photo = old_m_photo;
			}

		} catch (Exception e) {
			e.printStackTrace(); // NullPointerException�� ���⼭ �ߴ°� �ƴϾ�!
			System.out.println("���Ͽ뷮..");
			return;
		}

		// ������� �����ϴµ� �� �� ������

		// ���� ����� 10MB���� �����ɷ� �� �����ؼ� - ���� ���ε� ����
		// ���� ���� ���ؼ�(0MB)

		// ���� ������ �ߴ� : ���� �ٲٰڴ� -> �� ���� ���ϸ��� DB�� �־�� -> �� ���� ���ϸ��� Ȯ��
		// ���� ���� ���� ������ �ؾ� -> ���� ���� ���ϸ��� Ȯ��

		// ���� ������ ���ϸ� : ����� �ȹٲٰڴ� -> ���� ���� ���ϸ��� DB�� �־�� -> ���� ���� ���ϸ��� Ȯ��

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
			pstmt.setString(5, new_m_photo); // ������!!
			pstmt.setString(6, m_id);
			if (pstmt.executeUpdate() == 1) {
				request.setAttribute("r", "�������� ����");
				if (!new_m_photo.equals(old_m_photo)) { // ���縦 �ٲ�����
					new File(path + "/" + URLDecoder.decode(old_m_photo, "euc-kr")).delete(); // ���� �������� �����
				}
				SimpleDateFormat sdf =new SimpleDateFormat("yyyyMMdd");
				m = new Member(m_id, m_pw, m_name, m_phone, sdf.parse(m_birthday), new_m_photo);
				request.getSession().setAttribute("loginMember", m);
			} else {
				request.setAttribute("r", "�������� ����");
				if (!new_m_photo.equals(old_m_photo)) { // ���ο� ���� �ö󰣰� ����
					new File(path + "/" + URLDecoder.decode(new_m_photo, "euc-kr")).delete();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("r", "�������� ����(DB����)");
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

























