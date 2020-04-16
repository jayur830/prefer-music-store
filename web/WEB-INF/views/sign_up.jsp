<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ include file="/WEB-INF/include/header.jsp" %>
<html>
<head>
    <title>회원가입</title>
    <link rel="stylesheet" href="<c:url value="/resources/css/center_form.css" />" />
    <style>
        #username, #password, #password_confirm,
        #name, #birth, #email, .placeholder, .placeholder_up {
            font-size: 14pt;
        }

        #username_valid {
            font-size: 12pt;
            padding: 3px 15px;
            margin-bottom: 30px;
        }

        #sign_up, #cancel {
            font-size: 12pt;
            padding: 6px 30px;
            margin-right: 15px;
        }

        input[type=date] {
            height: 30px;
        }

        #label_male, #label_female {
            cursor: pointer;
        }

        #label_male:active, #label_female:active {
            cursor: default;
        }
    </style>
</head>
<body>
    <div id="parent_form">
        <table id="child_form">
            <tr>
                <td>
                    <input type="text" id="username" required />
                    <label for="username" class="placeholder">아이디</label>
                </td>
                <td style="padding: 0; text-align: center;">
                    <input type="button" id="username_valid" class="btn_dark_light" value="중복확인" />
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    <input type="password" id="password" required />
                    <label for="password" class="placeholder">비밀번호</label>
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    <input type="password" id="password_confirm" required />
                    <label for="password_confirm" class="placeholder">비밀번호 확인</label>
                </td>
            </tr>
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
            <tr>
                <td colspan="2">
					<span style="float: left; text-align: center; padding-bottom: 35px; width: 50%;">
		                <input type="radio" id="gender_male" name="gender" value="1" />
		                <label id="label_male" for="gender_male" class="fal fa-male fa-3x"></label>
		            </span>
                    <span style="float: left; text-align: center; padding-bottom: 35px; width: 50%;">
		                <input type="radio" id="gender_female" name="gender" value="0" />
		                <label id="label_female" for="gender_female" class="fal fa-female fa-3x"></label>
		            </span>
                </td>
            </tr>
            <tr>
                <td colspan="2" style="text-align: center;">
                    <input type="button" id="sign_up" class="btn_light_dark" value="회원가입" />
                    <input type="button" id="cancel" class="btn_light_dark" value="돌아가기" />
                </td>
            </tr>
        </table>
    </div>
    <script src="<c:url value="/resources/js/date_format.js"/>"></script>
    <script src="<c:url value="/resources/js/user.js"/>"></script>
    <script>
        console.log("${_csrf.parameterName}", "${_csrf.token}");
        let validation = () => {
            if ($("#username").val() === "") {
                alert("아이디를 입력해주세요.");
                return false;
            } else if ($("#username").val().length < 5) {
                alert("아이디는 5자 이상으로 입력해주세요.");
                return false;
            } else if ($("#username").val().indexOf(" ") !== -1) {
                alert("아이디는 공백 없이 입력해주세요.");
                return false;
            } else if (!$("#username_valid").attr("disabled")) {
                alert("아이디 중복을 확인하세요.");
                return false;
            } else if ($("#password").val() === "") {
                alert("비밀번호를 입력해주세요.");
                return false;
            } else if ($("#password").val().length < 8 || $("#password").val().length > 20) {
                alert("비밀번호는 8자 이상 20자 이하로 입력해주세요.");
                return false;
            } else if (!/[a-zA-Z]/.test($("#password").val()) ||
                !/[0-9]/.test($("#password").val()) ||
                !/[~!@#$%^&*()_+|<>?:{}]/.test($("#password").val())) {
                alert("비밀번호는 영문+숫자+특수문자를 포함하여 입력해주세요.");
                return false;
            } else if ($("#password").val().indexOf(" ") !== -1) {
                alert("비밀번호는 공백 없이 입력해주세요.");
                return false;
            } else if ($("#password_confirm").val() === "") {
                alert("비밀번호 확인을 입력해주세요.");
                return false;
            } else if ($("#password").val() !== $("#password_confirm").val()) {
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
            } else if ($("input[name=gender]:checked").val() === undefined) {
                alert("성별을 체크하세요.");
                return false;
            }
            return true;
        };
        $("#username, #password, #password_confirm, #name, #birth, #email, input[name=gender], #sign_up").on("keyup", e => {
            if (e.keyCode === 13 && validation())
                user.signUp(
                    "${_csrf.parameterName}", "${_csrf.token}",
                    $("#username").val(), $("#password").val(),
                    $("#name").val(), $("#birth").val(), $("#email").val(),
                    parseInt($("input[name=gender]:checked").val()), new Date().getFullYear() + 1 - parseInt($("#birth").val().split("-")[0]));
        });
        $("#sign_up").on("click", () =>  {
            if (validation())
                user.signUp(
                    "${_csrf.parameterName}", "${_csrf.token}",
                    $("#username").val(), $("#password").val(),
                    $("#name").val(), $("#birth").val(), $("#email").val(),
                    parseInt($("input[name=gender]:checked").val()),
                    new Date().getFullYear() + 1 - parseInt($("#birth").val().split("-")[0]));
        });
        $("#cancel").on("click", () => location.replace("/"));

        $("#username_valid").on("click", function () {
            const username = $("#username").val();
            if (username === "") alert("아이디를 입력하고 중복확인하세요.");
            else {
                user.checkUsername("${_csrf.parameterName}", "${_csrf.token}", username);
                $(this).attr("disabled", true);
                $(this).removeClass("btn_dark_light");
                $(this).addClass("btn_disabled");
            }
        });

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
    </script>
</body>
</html>
