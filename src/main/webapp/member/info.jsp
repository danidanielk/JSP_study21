<%@ page language="java" contentType="text/html; charset=EUC-KR"
	pageEncoding="EUC-KR"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="EUC-KR">
<title>Insert title here</title>
</head>
<body>
	<table id="signupTbl">
		<form action="MemberInfoController" name="updateForm" method="post"
			enctype="multipart/form-data" onsubmit="return updateCheck();">
			<tr>
				<td><input value="${sessionScope.loginMember.m_id }"
					name="m_id" maxlength="10" readonly="readonly"></td>
			</tr>
			<tr>
				<td><input value="${sessionScope.loginMember.m_pw }"
					name="m_pw" autofocus="autofocus" autocomplete="off" maxlength="12"
					type="password"></td>
			</tr>
			<tr>
				<td><input value="${sessionScope.loginMember.m_pw }"
					name="m_pwchk" autocomplete="off" maxlength="12" type="password"></td>
			</tr>
			<tr>
				<td><input value="${sessionScope.loginMember.m_name }"
					name="m_name" autocomplete="off" maxlength="10"></td>
			</tr>
			<tr>
				<td><input value="${sessionScope.loginMember.m_phone }"
					name="m_phone" autocomplete="off" maxlength="11"></td>
			</tr>
			<tr>
				<td>생년월일<br> <select name="m_y">
						<option><fmt:formatDate
								value="${sessionScope.loginMember.m_birthday }" pattern="yyyy" /></option>
						<c:forEach var="y" begin="${cy - 100 }" end="${cy }">
							<option>${y }</option>
						</c:forEach>
				</select>&nbsp;년&nbsp;&nbsp;<select name="m_m">
						<option><fmt:formatDate
								value="${sessionScope.loginMember.m_birthday }" pattern="M" /></option>
						<c:forEach var="m" begin="1" end="12">
							<option>${m }</option>
						</c:forEach>
				</select>&nbsp;월&nbsp;&nbsp;<select name="m_d">
						<option><fmt:formatDate
								value="${sessionScope.loginMember.m_birthday }" pattern="d" /></option>
						<c:forEach var="d" begin="1" end="31">
							<option>${d }</option>
						</c:forEach>
				</select>&nbsp;일&nbsp;&nbsp;
				</td>
			</tr>
			<tr>
				<td>사진<br> <img
					src="img/${sessionScope.loginMember.m_photo }"
					id="memberUpdatePhoto"><br> <input type="file"
					name="m_photo"></td>
			</tr>
			<tr>
				<td align="center">
					<button>정보수정</button>
		</form>
		<button onclick="bye();">회원탈퇴</button>
		</td>
		</tr>
	</table>
</body>
</html>