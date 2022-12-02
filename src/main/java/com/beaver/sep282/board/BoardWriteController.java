package com.beaver.sep282.board;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.beaver.sep282.home.TokenManager;
import com.beaver.sep282.member.MemberDAO;

@WebServlet("/BoardWriteController")
public class BoardWriteController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// ���ͳ� �ϸ鼭 ���ΰ�ħ : �Ʊ� �ߴ� �� ��û�� �״�� �ٽ��ϴ°�
	//		XXController?a=10&b=20 ó�� �ѹ�(�ǵ��ߴ�)
	//		XXController?a=10&b=20 ���ΰ�ħ
	//		XXController?a=10&b=20 ���ΰ�ħ

	// �ش� ��û�� ó�� �ѹ��� ó��, �ι�°���ʹ� ����
	//		�۾��⸦ ���� �� ������ �����Ǵ� token��
	//		token���� ��� ������?!
	
	//		XXController?a=10&b=20&token=?
	//			token���� ��򰡿� ������
	//		XXController?a=10&b=20&token=?
	//			����Ǿ��ִ� token����, ���� ���� token���� ������ ����
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (MemberDAO.loginCheck(request)) {
			BoardDAO.getBdao().write(request);
		} 
		TokenManager.make(request);
		BoardDAO.getBdao().getBoardMsg(1, request);
		request.setAttribute("cp", "board/board.jsp");
		request.getRequestDispatcher("index.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

}
