function boardCheck() {
	var b_text = document.boardForm.b_text;
	
	if (isEmpty(b_text)) {
		alert("안써?");
		b_text.value = "";
		return false;
	}
	return true;
}

function searchCheck() {
	var s_text = document.boardSearchForm.search;
	
	if (isEmpty(s_text)) {
		alert("아ㅋㅋ 검색하라고 ㅋㅋㅋ");
		s_text.value = "";
		s_text.focus();
		return false;
	}
	return true;
}