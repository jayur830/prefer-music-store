<%@ page import="org.springframework.security.core.Authentication" %>
<%@ page import="org.springframework.security.core.context.SecurityContextHolder" %>
<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ include file="/WEB-INF/include/header.jsp" %>
<%
	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	String username = "";
	boolean isAdmin = false;
	if (auth.getPrincipal() != null) {
		username = auth.getName();
		isAdmin = auth.getAuthorities().toArray()[0].toString().equals("ROLE_ADMIN");
	}
%>
<html>
<head>
	<title>회원정보 수정</title>
	<link rel="stylesheet" href="<c:url value="/resources/css/center_form.css" />" />
	<style>
		td {
			padding: 3px 10px;
		}
		
		#delete_user {
			font-size: 10pt;
			padding: 5px 15px;
		}
		
		label.placeholder, label.placeholder_up {
			font-size: 12pt;
		}
		
		input[type=date] {
			height: 25px;
		}
		
		input[type=text], 
		input[type=password], 
		input[type=date], 
		input[type=email] {
			font-size: 12pt;
		}
		
		input[type=button] {
			padding: 5px 20px;
		}
		
		#label_male, #label_female {
			cursor: pointer;
		}
		
		#label_male:active, #label_female:active {
			cursor: default;
		}

		#child_form {
			width: 500px;
		}

		input {
			font-size: 14pt;
		}

		#delete_user {
			font-size: 10pt;
			padding: 5px 15px;
		}

		#change_password {
			font-size: 10pt;
			margin-bottom: 30px;
		}

		#edit {
			padding: 10px 35px;
			margin-right: 20px;
		}

		#cancel {
			padding: 10px 35px;
			margin-left: 20px;
		}

		@media screen and (max-width: 786px) {
			#child_form {
				width: 400px;
			}

			input {
				font-size: 16pt;
			}

			#delete_user {
				font-size: 12pt;
			}

			#change_password {
				font-size: 10pt;
				margin-bottom: 30px;
			}
		}
	</style>
</head>
<body>
	<div style="position: absolute; top: 5px; left: 5px; text-align: left;">
		<input type="button" id="delete_user" value="회원탈퇴" class="btn_dark_light" />
	</div>
	<div id="parent_form">
		<table id="child_form">
			<tr>
				<td id="row_0" style="width: 100%;">
					<input type="text" id="username" disabled />
					<label for="username" class="placeholder_up">아이디</label>
				</td>
			</tr>
			<tr>
				<td>
					<input type="password" id="password" disabled required />
					<label id="label_password" for="password" class="placeholder_up">비밀번호</label>
				</td>
				<td><input type="button" value="비밀번호 변경" id="change_password" class="btn_dark_light" /></td>
			</tr>
			<tr id="password_confirm_toggle">
				<td id="row_1">
					<input type="password" id="password_confirm" required />
					<label for="password_confirm" class="placeholder">비밀번호 확인</label>
				</td>
			</tr>
			<tr>
				<td id="row_2">
					<input type="text" id="name" required />
					<label for="name" class="placeholder">이름</label>
				</td>
			</tr>
			<tr>
				<td id="row_3">
					<input type="date" id="birth" required />
					<label for="birth" class="placeholder_up">생년월일</label>
				</td>
			</tr>
			<tr>
				<td id="row_4">
					<input type="email" id="email" required />
					<label for="email" class="placeholder">이메일</label>
				</td>
			</tr>
			<tr>
				<td id="row_5">
					<span style="float: left; text-align: center; padding-bottom: 25px;  width: 50%;">
						<input type="radio" id="gender_male" name="gender" value="1" />
						<label id="label_male" for="gender_male" class="fal fa-male fa-3x"></label>
					</span>
					<span style="float: left; text-align: center; padding-bottom: 25px;  width: 50%;">
						<input type="radio" id="gender_female" name="gender" value="0" />
						<label id="label_female" for="gender_female" class="fal fa-female fa-3x"></label>
					</span>
				</td>
			</tr>
			<tr style="text-align: center;">
				<td id="row_6">
					<input id="edit" type="button" value="수정" class="btn_disabled" style="margin-right: 10px;" disabled />
					<input id="cancel" type="button" value="돌아가기" class="btn_light_dark" style="margin-left: 10px;" />
				</td>
			</tr>
		</table>
	</div>
	<script src="<c:url value="/resources/js/date_format.js"/>"></script>
	<script src="<c:url value="/resources/js/user.js"/>"></script>
	<script>
		user.initUserInfo("${_csrf.parameterName}", "${_csrf.token}", "<%=username%>");
		let editable = () => {
			let disabled = $("#password").val() === user.getUserInfo().password &&
					$("#name").val() === user.getUserInfo().name &&
					$("#birth").val() === user.getUserInfo().birth &&
					$("#email").val() === user.getUserInfo().email &&
					parseInt($("input[name=gender]:checked").val()) === user.getUserInfo().gender;
			$("#edit").attr("disabled", disabled);
			if (disabled) {
				$("#edit").removeClass("btn_light_dark");
				$("#edit").addClass("btn_disabled");
			} else {
				$("#edit").removeClass("btn_disabled");
				$("#edit").addClass("btn_light_dark");
			}
		};

		let isToggled = false;
		$("#password_confirm_toggle").toggle(0);
		$("#change_password").on("click", () => {
			if (!isToggled) {
				$("#password").attr("disabled", false);
				$("#password").val("");
				$("#label_password").removeClass("placeholder_up");
				$("#label_password").addClass("placeholder");
				isToggled = true;
			} else {
				$("#password").attr("disabled", true);
				$("#password").val(user.getUserInfo().password);
				$("#label_password").removeClass("placeholder");
				$("#label_password").addClass("placeholder_up");
				isToggled = false;
				editable();
			}
			$("#password_confirm_toggle").toggle(0);
		});

		$("#password").on("keyup", editable);
		$("#name").on("keyup", editable);
		$("#birth").on("click", editable);
		$("#email").on("keyup", editable);
		$("input[name=gender]").on("change", editable);

		$("#label_male").on("click", function () {
			$("#label_female").removeClass("fas");
			$("#label_female").addClass("fal");
			$(this).removeClass("fal");
			$(this).addClass("fas");
		});
		$("#label_female").on("click", function () {
			$("#label_male").removeClass("fas");
			$("#label_male").addClass("fal");
			$(this).removeClass("fal");
			$(this).addClass("fas");
		});

		let setMobile = () => {
			for (let i = 0; i < 7; ++i)
				$("#row_" + i).attr("colspan", "3");
		}, setDesktop = () => {
			for (let i = 0; i < 7; ++i)
				$("#row_" + i).attr("colspan", "1");
		};

		let isMobile = $(window).width() < 786;
		let check = () => {
			if (!isMobile && $(window).width() < 786) {
				isMobile = true;
				setMobile();
			} else if (isMobile && $(window).width() >= 786) {
				isMobile = false;
				setDesktop();
			}
		};

		$(window).on("load", () => isMobile ? setMobile() : setDesktop());
		$(window).resize(check);

		let validation = () => {
			if ($("#password").attr("disabled") !== undefined && $("#password").val() === "") {
				alert("비밀번호를 입력해주세요.");
				return false;
			} else if ($("#password").attr("disabled") !== undefined && ($("#password").val().length < 8 || $("#password").val().length > 20)) {
				alert("비밀번호는 8자 이상 20자 이하로 입력해주세요.");
				return false;
			} else if ($("#password").attr("disabled") !== undefined && (!/[a-zA-Z]/.test($("#password").val()) ||
					!/[0-9]/.test($("#password").val()) ||
					!/[~!@#$%^&*()_+|<>?:{}]/.test($("#password").val()))) {
				alert("비밀번호는 영문+숫자+특수문자를 포함하여 입력해주세요.");
				return false;
			} else if ($("#password").attr("disabled") !== undefined && $("#password").val().indexOf(" ") !== -1) {
				alert("비밀번호는 공백 없이 입력해주세요.");
				return false;
			} else if ($("#password").attr("disabled") !== undefined && $("#password_confirm").val() === "") {
				alert("비밀번호 확인을 입력해주세요.");
				return false;
			} else if ($("#password").attr("disabled") !== undefined && $("#password").val() !== $("#password_confirm").val()) {
				alert("비밀번호가 일치하지 않습니다. 다시 입력하세요.")
				return false;
			} else if ($("#name").val() === "") {
				alert("이름을 입력해주세요.");
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

		$("#delete_user").on("click", () => location.replace("/delete_user_auth"));

		$("#username, #password, #password_confirm, #name, #birth, #email, input[name=gender], #edit").on("keyup", e => {
			if (e.keyCode === 13 && validation() && confirm("입력한 정보로 수정하시겠습니까?"))
				user.edit(
						"${_csrf.parameterName}", "${_csrf.token}",
						$("#username").val(), $("#password").attr("disabled") !== undefined ? null : $("#password").val(),
						$("#name").val(), $("#birth").val(), $("#email").val(),
						parseInt($("input[name=gender]:checked").val()), new Date().getFullYear() + 1 - parseInt($("#birth").val().split("-")[0]));
		});
		$("#edit").on("click", () => {
			if (confirm("입력한 정보로 수정하시겠습니까?"))
				user.edit(
						"${_csrf.parameterName}", "${_csrf.token}",
						$("#username").val(), $("#password").attr("disabled") !== undefined ? null : $("#password").val(),
						$("#name").val(), $("#birth").val(), $("#email").val(),
						parseInt($("input[name=gender]:checked").val()), new Date().getFullYear() + 1 - parseInt($("#birth").val().split("-")[0]));
		});
		$("#cancel").on("click", () => location.replace("<%=isAdmin ? "/admin" : "/main"%>"));
	</script>
</body>
</html>