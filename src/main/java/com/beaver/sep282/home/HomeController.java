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
	
	// 서비스 시작할 때 게시물 갯수 세어놓고 싶음
	// 프로젝트 실행시키면 톰캣이 모든 컨트롤러를 다 만듬
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
