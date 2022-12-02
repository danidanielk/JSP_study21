<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="EUC-KR">
<title>Insert title here</title>
</head>
<body>
	<form action="SignupController" method="post" enctype="multipart/form-data" name="signupForm"
		onsubmit="return signupCheck();">
		<table id="signupTbl">
			<tr>
				<td>
					<input name="m_id" placeholder="ID" autofocus="autofocus"
					autocomplete="off" maxlength="10">
				</td>
			</tr>
			<tr>
				<td>
					<input name="m_pw" placeholder="PW" type="password" 
					autocomplete="off" maxlength="12">
				</td>
			</tr>
			<tr>
				<td>
					<input name="m_pwchk" placeholder="PW Check" type="password" 
					autocomplete="off" maxlength="12">
				</td>
			</tr>
			<tr>
				<td>
					<input name="m_name" placeholder="Name"  
					autocomplete="off" maxlength="10">
				</td>
			</tr>
			<tr>
				<td>
					<input name="m_phone" placeholder="Phone Number"  
					autocomplete="off" maxlength="11">
				</td>
			</tr>
			<tr>
				<td>
					생년월일<br>
					<select name="m_y">
						<c:forEach var="y" begin="${cy - 100 }" end="${cy }">
							<option>${y }</option>
						</c:forEach>
					</select>&nbsp;년&nbsp;&nbsp;
					<select name="m_m">
						<c:forEach var="m" begin="1" end="12">
							<option>${m }</option>
						</c:forEach>
					</select>&nbsp;월&nbsp;&nbsp;
					<select name="m_d">
						<c:forEach var="d" begin="1" end="31">
							<option>${d }</option>
						</c:forEach>
					</select>&nbsp;일&nbsp;&nbsp;
				</td>
			</tr>
			<tr>
				<td>
					사진<br>
					<input type="file" name="m_photo">
				</td>
			</tr>
			<tr>
				<td align="center">
					<button>Sign Up</button>
				</td>
			</tr>
		</table>
	</form>
</body>
</html>

















