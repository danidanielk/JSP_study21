package com.beaver.sep282.home;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.beaver.sep282.board.BoardDAO;
import com.beaver.sep282.member.MemberDAO;

@WebServlet("/HomeController")
public class HomeController extends HttpServlet {
	
	// ���� ������ �� �Խù� ���� ������� ����
	// ������Ʈ �����Ű�� ��Ĺ�� ��� ��Ʈ�ѷ��� �� ����
	public HomeController() {
		BoardDAO.getBdao().countAllBoardMsg();
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		MemberDAO.loginCheck(request);
		request.setAttribute("cp", "home.jsp");
		request.getRequestDispatcher("index.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

}
