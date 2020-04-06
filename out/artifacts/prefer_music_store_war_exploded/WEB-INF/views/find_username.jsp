<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ include file="/WEB-INF/include/header.jsp" %>
<html>
<head>
	<title>아이디 찾기</title>
	<link rel="stylesheet" href="<c:url value="/resources/css/center_form.css" />" />
	<style>
		td {
			padding: 3px;
		}
		
		input[type=text], 
		input[type=date], 
		input[type=email] {
			font-size: 14pt;
		}
		
		input[type=button] {
			font-size: 14pt;
			padding: 8px 20px;
		}
		
		label.placeholder {
			font-size: 14pt;
		}
		
		label.placeholder_up {
			transform: translateY(-270%);
		}
	</style>
</head>
<body>
	<div id="parent_form">
		<table id="child_form" style="margin: 0 auto;">
			<tr>
				<td colspan="2">
					<input type="text" id="name" required />
					<label for="name" class="placeholder">이름</label>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<input type="date" id="birth" required />
					<label for="birth" class="placeholder_up">생년월일</label>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<input type="email" id="email" required />
					<label for="email" class="placeholder">이메일</label>
				</td>
			</tr>
			<tr style="text-align: center;">
				<td><input id="find_username" type="button" value="아이디 찾기" class="btn_light_dark" /></td>
				<td><input id="cancel" type="button" value="돌아가기" class="btn_light_dark" /></td>
			</tr>
		</table>
	</div>
	<script src="<c:url value="/resources/js/date_format.js"/>"></script>
	<script src="<c:url value="/resources/js/user.js"/>"></script>
	<script>
		let validation = () => {
			if ($("#name").val() === "") {
				alert("이름을 입력해주세요.");
				return false;
			} else if (!/[a-zA-Z]/.test($("#name").val()) && !/[가-힣]/.test($("#name").val())) {
				alert("이름을 제대로 입력해주세요.");
				return false;
			} else if ($("#birth").val() === "" || new Date().format("yyyy-MM-dd") <= $("#birth").val()) {
				alert("올바른 생년월일을 입력해주세요.");
				return false;
			} else if (!/^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$/i.test($("#email").val())) {
				alert("올바른 이메일 주소를 입력해주세요.");
				return false;
			}
			return true;
		};
		$("#name, #birth, #email, #find_username").on("keyup", e => {
			if (e.keyCode === 13 && validation()) user.findUsername(
					"${_csrf.parameterName}", "${_csrf.token}",
					$("#name").val(), $("#birth").val(), $("#email").val());
		});
		$("#find_username").on("click", () => {
			if (validation()) user.findUsername(
					"${_csrf.parameterName}", "${_csrf.token}",
					$("#name").val(), $("#birth").val(), $("#email").val());
		});
		$("#cancel").on("click", () => location.replace("/login"));
	</script>
</body>
</html>