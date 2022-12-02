<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="EUC-KR">
<title>Insert title here</title>
</head>
<body>
	<table id="loginTbl">
		<form action="LoginController" name="loginForm" method="post" onsubmit="return loginCheck();">
			<tr>
				<td align="center" id="loginTd">Login</td>
			</tr>
			<tr>
				<td align="center">
					<input autocomplete="off" placeholder="ID" name="m_id"
					maxlength="10" value=${cookie.lastLoginId.value }>
				</td>
			</tr>
			<tr>
				<td align="center">
					<input autocomplete="off" placeholder="Password" name="m_pw"
					maxlength="12" type="password">
				</td>
			</tr>
			<tr>
				<td align="center">
					<button>Login</button>			
		</form>
					<button onclick="signUpgo();">Sign Up</button>
				</td>
			</tr>
			
	</table>
</body>
</html>









