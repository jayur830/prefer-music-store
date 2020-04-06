<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ include file="/WEB-INF/include/header.jsp" %>
<html>
<head>
	<title>비밀번호 찾기</title>
	<link rel="stylesheet" href="<c:url value="/resources/css/center_form.css" />" />
	<style>
		input[type=text],
		input[type=date], 
		input[type=email] {
			font-size: 14pt;
		}
		
		input[type=submit], input[type=button] {
			font-size: 14pt;
			padding: 8px 20px;
		}
		
		label.placeholder {
			font-size: 14pt;
		}
		
		td {
			padding: 10px 10px;
		}
	</style>
</head>
<body>
	<div id="parent_form">
		<table id="child_form" style="margin: 0 auto;">
			<tr>
				<td colspan="2">
					<input type="text" id="username" required />
					<label for="username" class="placeholder">아이디</label>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<input type="email" id="email" required />
					<label for="email" class="placeholder">이메일</label>
				</td>
			</tr>
			<tr style="text-align: center;">
				<td><input id="find_password" type="button" value="비밀번호 찾기" class="btn_light_dark" /></td>
				<td><input id="cancel" type="button" value="돌아가기" class="btn_light_dark" /></td>
			</tr>
		</table>
	</div>
	<script src="<c:url value="/resources/js/user.js"/>"></script>
	<script>
		const findPassword = () => {
			const email = $("#email").val();

			// 이메일이 유효하지 않은 경우
			if (!/^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$/i.test(email))
				alert("올바른 이메일 주소를 입력해주세요.");
			else {
				$(".placeholder").addClass("placeholder_up");
				$(".placeholder").removeClass("placeholder");
				$("#username, #email, #find_password, #cancel").attr("disabled", true);
				user.findPassword(
						"${_csrf.parameterName}", "${_csrf.token}",
						$("#username").val(), email);
			}
		};

		$("#username, #email, #find_password").on("keyup", e => {
			if (e.keyCode === 13) findPassword();
		});

		$("#find_password").on("click", findPassword);
		$("#cancel").on("click", () => location.replace("/login"));
	</script>
</body>
</html>