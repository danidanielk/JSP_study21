<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="EUC-KR">
<title>Insert title here</title>
</head>
<body>
	<table id="loginSuccessTbl">
		<tr>
			<td rowspan="2"><img src="img/${sessionScope.loginMember.m_photo }"></td>
			<td>${sessionScope.loginMember.m_id }</td>
		</tr>
		<tr>
			<td align="center" colspan="2">${sessionScope.loginMember.m_name } ��</td>
		</tr>
		<tr>
			<td align="center" colspan="2">�������</td>
		</tr>
		<tr>
			<td align="center" colspan="2">
				<button onclick="memberInfoGo();">����</button>
				<button onclick="logout();">�α׾ƿ�</button>
			</td>
		</tr>
	</table>
</body>
</html>












