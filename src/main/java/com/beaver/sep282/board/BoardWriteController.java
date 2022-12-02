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

	// 인터넷 하면서 새로고침 : 아까 했던 그 요청을 그대로 다시하는거
	//		XXController?a=10&b=20 처음 한번(의도했던)
	//		XXController?a=10&b=20 새로고침
	//		XXController?a=10&b=20 새로고침

	// 해당 요청을 처음 한번만 처리, 두번째부터는 무시
	//		글쓰기를 새로 할 때마다 생성되는 token값
	//		token값은 어디에 저장을?!
	
	//		XXController?a=10&b=20&token=?
	//			token값을 어딘가에 저장을
	//		XXController?a=10&b=20&token=?
	//			저장되어있는 token값과, 지금 들어온 token값이 같으면 무시
	
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
