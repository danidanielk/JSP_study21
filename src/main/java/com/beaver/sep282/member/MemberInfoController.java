package com.beaver.sep282.member;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.beaver.sep282.home.DateManager;

@WebServlet("/MemberInfoController")
public class MemberInfoController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (MemberDAO.loginCheck(request)) {
			DateManager.getCurYear(request);
			request.setAttribute("cp", "member/info.jsp");
		} else {
			request.setAttribute("cp", "home.jsp");
		}
		request.getRequestDispatcher("index.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (MemberDAO.loginCheck(request)) {
			MemberDAO.update(request);
			DateManager.getCurYear(request);
			request.setAttribute("cp", "member/info.jsp"); // 정보수정 끝나면 바로 확인해볼수 있는 상태
			// MemberDAO.loginCheck(request);
		} else {
			request.setAttribute("cp", "home.jsp");
		}
		request.getRequestDispatcher("index.jsp").forward(request, response);
	}

}
