function bye() {
	let t = prompt("Ż�� ��? Y/N");
	if (t == "Y") {
		location.href = "DeleteMemberController";
	}
}

function boardDelete(b_no) {
	let t = confirm("��¥ �� ���� ?");
	if (t) {
		location.href = "BoardDeleteController?b_no=" + b_no;
	}
}

function boardUpdate(b_no, b_text) {
	b_text = prompt("���� ����");
	if (b_text.length < 200) {
		location.href = "BoardUpdateController?b_no=" + b_no + "&b_text=" + b_text;
	}
}

function logout() {
	let really = confirm("�����ٰ� ?");
	if (really) {
		location.href = "LoginController";
	}
}

function memberInfoGo() {
	location.href = "MemberInfoController";
}


function signUpgo() {
	location.href = "SignupController";
}
