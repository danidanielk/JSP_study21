package com.beaver.sep282.board;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import com.beaver.db.manager.BeaverDBManager;
import com.beaver.sep282.member.Member;

public class BoardDAO {

	private int allBoardMsgCount;

	private static final BoardDAO BDAO = new BoardDAO();

	private BoardDAO() {
		// TODO Auto-generated constructor stub
	}

	public static BoardDAO getBdao() {
		return BDAO;
	}

	public void clearSearch(HttpServletRequest request) {
		request.getSession().setAttribute("search", null);
	}

	public void countAllBoardMsg() {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			con = BeaverDBManager.connect("beaverPool");
			String sql = "select count(*) from board";
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			rs.next();
			allBoardMsgCount = rs.getInt("count(*)");
			System.out.println(allBoardMsgCount);
		} catch (Exception e) {
			e.printStackTrace();
		}
		BeaverDBManager.close(con, pstmt, rs);
	}

	private int countSearchMsg(String search) { // Controller���� �� �� �ƴϴ� private�� ��ױ�
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			con = BeaverDBManager.connect("beaverPool");
			String sql = "select count(*) " + "from board, SEP28_SNS "
					+ "where b_writer = m_id and b_text like '%'||?||'%' ";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, search);
			rs = pstmt.executeQuery();
			rs.next();
			return rs.getInt("count(*)");
		} catch (Exception e) {
			e.printStackTrace();
			return 0; // �ش��ϴ� ������ ������ 0 ����
		} finally {
			BeaverDBManager.close(con, pstmt, rs);
		}
	}

	public void delete(HttpServletRequest request) {
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = BeaverDBManager.connect("beaverPool");
			
			int b_no = Integer.parseInt(request.getParameter("b_no"));
			
			String sql = "delete from board where b_no= ? ";
		
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, b_no);
			
			if (pstmt.executeUpdate() == 1) {
				request.setAttribute("r", "���� ����");
				allBoardMsgCount--;
			} else {
				request.setAttribute("r", "���� ����");
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("r", "���� ����");
		}
		BeaverDBManager.close(con, pstmt, null);
	}

	public void getBoardMsg(int pageNo, HttpServletRequest request) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			con = BeaverDBManager.connect("beaverPool");

			int boardCount = allBoardMsgCount; // �ϴ� ��ü ����(31)
			String search = (String) request.getSession().getAttribute("search"); // �˻���
			if (search == null) { // �˻�� ���� = ��ü��ȸ
				search = "";
			} else { // �˻� O
				boardCount = countSearchMsg(search);
			}

			// pageNo = 5;
			int boardPerPage = 5; // �������� �Խù� 5��
			int pageCount = (int) Math.ceil(boardCount / (double) boardPerPage); // �Խù� ���� ���� ������ ������ �� (�Ҽ��� �ø�) 
			request.setAttribute("pageCount", pageCount);
			request.setAttribute("pageNo", pageNo);

			int start = boardPerPage * (pageNo - 1) + 1; // �� �������� ���� �Խù� ù��° ��ȣ ��� (1p : 1, 2p: 6, ...)
			int end = (pageNo == pageCount) ? boardCount : (start + boardPerPage - 1); // �� �������� ���� �� �Խù� ��ȣ ��� (1p: 5, 2p: 10, ...) 
																					   // �� ���������� 5�� ä��� ���� �������� �Ѿ��

			String sql = "select * " 
					+ "from ( " 
					+ "	select rownum as rn, b_no, b_writer, b_when, b_text, m_photo "
					+ "	from ( " 
					+ "		select * " 
					+ "		from board, SEP28_SNS "
					+ "		where b_writer = m_id and b_text like '%'||?||'%' " 
					+ "		order by b_when desc" 
					+ "	)"
					+ ")" 
					+ "where rn >= ? and rn <= ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, search);
			pstmt.setInt(2, start);
			pstmt.setInt(3, end);

			rs = pstmt.executeQuery();

			ArrayList<Board> boards = new ArrayList<Board>();
			Board board = null;
			while (rs.next()) {
				// boards.add(rs.getInt("b_no"), rs.getString("b_writer"), rs.getDate("b_when"),
				// rs.getString("b_text"), rs.getString("m_photo"));
				board = new Board();
				board.setB_no(rs.getInt("b_no"));
				board.setB_writer(rs.getString("b_writer"));
				board.setB_when(rs.getDate("b_when"));
				board.setB_text(rs.getString("b_text"));
				board.setM_photo(rs.getString("m_photo"));
				boards.add(board);
			}
			request.setAttribute("boards", boards);
		} catch (Exception e) {
			e.printStackTrace();
		}
		BeaverDBManager.close(con, pstmt, rs);
	}

	public void searchBoardMsg(HttpServletRequest request) {
		// ���ο� ��û�� ���� ��(������ ��ȯ)���� �˻���� ����־��
		// Ŭ���̾�Ʈ ������ �۾�
		String search = request.getParameter("search");
		request.getSession().setAttribute("search", search);
		// �˻�(��û) -> 25�� -> 1������ 10���� �����ٰ�
		// �˻��Ѱ��� 2��������(��û)
		// �˻��Ѱ��� 3��������(��û)
	}
	
	public boolean update(HttpServletRequest request) {
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = BeaverDBManager.connect("beaverPool");
			request.setCharacterEncoding("euc-kr");
			
			int b_no = Integer.parseInt(request.getParameter("b_no"));
			String b_text = request.getParameter("b_text").replace("\r\n", "<br>");

			String sql = "update board set b_text=?, b_when=sysdate where b_no=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, b_text);
			pstmt.setInt(2, b_no);
			
			if (pstmt.executeUpdate() == 1) {
				request.setAttribute("r", "���� ����");
				return true;
			} else {
				request.setAttribute("r", "���� ����");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("r", "���� ����");
			return false;
		} finally {
			BeaverDBManager.close(con, pstmt, null);
		}
	}
	
	public void write(HttpServletRequest request) {
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = BeaverDBManager.connect("beaverPool");
			// insert�� �� �� �� å��
			// �� ��ȣ? - seq
			// �۾��� ���̵�?
			Member m = (Member) request.getSession().getAttribute("loginMember");
			String b_writer = m.getM_id();
			// �۾� ��¥? - sysdate
			// �� ����?
			String b_text = request.getParameter("b_text").replace("\r\n", "<br>");

			// ���ΰ�ħ : ����ߴ� �� ��û �״�� �ٽ�(�Ķ���� �� �״��)

			// 1Ʈ°) null
			// 2Ʈ° - �ٽ� �� ��) 11:01:02:123
			// 3Ʈ° - ���ΰ�ħ) 11:04:12:678
			String formerToken = (String) request.getSession().getAttribute("formerToken");
			System.out.println(formerToken);
			// 1Ʈ°) 11:01:02:123
			// 2Ʈ° - �ٽ� �� ��) 11:04:12:678
			// 3Ʈ° - ���ΰ�ħ) 11:04:12:678
			String token = request.getParameter("token");
			System.out.println(token);

			// 1Ʈ°) formerToken == null => ���� insert���� => formerToken�� 11:01:02:123
			// 2Ʈ° - �ٽ� �� ��) formerToken�� token�� �ٸ� => ���� insert���� => formerToken��
			// 11:04:12:678
			// 3Ʈ° - ���ΰ�ħ) if�� ���ǿ� ���� ���� => �۾�x
			if (formerToken == null || !token.equals(formerToken)) {
				String sql = "insert into board values(board_seq.nextval, ?, sysdate, ?)";
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, b_writer);
				pstmt.setString(2, b_text);

				if (pstmt.executeUpdate() == 1) {
					request.setAttribute("r", "�۾��� ����");
					request.getSession().setAttribute("formerToken", token);
					allBoardMsgCount++;
				}
			} else {
				request.setAttribute("r", "���ΰ�ħ");
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("r", "�۾��� ����");
		}
		BeaverDBManager.close(con, pstmt, null);
	}

}
