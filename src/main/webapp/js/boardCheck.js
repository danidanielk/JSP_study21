function boardCheck() {
	var b_text = document.boardForm.b_text;
	
	if (isEmpty(b_text)) {
		alert("�Ƚ�?");
		b_text.value = "";
		return false;
	}
	return true;
}

function searchCheck() {
	var s_text = document.boardSearchForm.search;
	
	if (isEmpty(s_text)) {
		alert("�Ƥ��� �˻��϶�� ������");
		s_text.value = "";
		s_text.focus();
		return false;
	}
	return true;
}