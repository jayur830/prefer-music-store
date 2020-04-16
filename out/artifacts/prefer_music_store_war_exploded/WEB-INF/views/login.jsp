<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ include file="/WEB-INF/include/header.jsp" %>
<sec:authorize access="isAuthenticated()">
    <c:redirect url="/main" />
</sec:authorize>
<html>
<head>
    <title>로그인</title>
    <link rel="stylesheet" href="<c:url value="/resources/css/center_form.css" />" />
    <style>
        #login_error {
            font-weight: bold;
            font-size: 14pt;
            color: red;
            caption-side: bottom;
            text-align: center;
        }
    </style>
</head>
<body>
    <div id="parent_form">
        <table id="child_form">
            <caption id="login_error"></caption>
            <tr>
                <td colspan="2">
                    <input type="text" id="username" style="font-size: 16pt;" required />
                    <label for="username" class="placeholder" style="font-size: 16pt;">아이디</label>
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    <input type="password" id="password" style="font-size: 16pt;" required />
                    <label for="password" class="placeholder" style="font-size: 16pt;">비밀번호</label>
                </td>
            </tr>
            <tr>
                <td colspan="2" style="text-align: center; font-size: 10pt;">
                    <a href="<c:url value="/find_username"/>" style="padding: 0 10px;">아이디 찾기</a>
                    <a href="<c:url value="/find_password"/>" style="padding: 0 10px;">비밀번호 찾기</a>
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    <input type="button" id="login" value="로그인" class="btn_light_dark" style="width: 100%; font-size: 14pt; padding: 10px 0;" />
                </td>
            </tr>
            <tr style="text-align: center;">
                <td style="padding: 20px 0;"><input type="button" id="back" value="돌아가기" class="btn_light_dark" style="font-size: 14pt; padding: 10px 50px;" /></td>
                <td style="padding: 20px 0;"><input type="button" id="guest" value="비회원" class="btn_dark_light" style="font-size: 14pt; padding: 10px 50px;" /></td>
            </tr>
        </table>
    </div>
    <script src="<c:url value="/resources/js/user.js"/>"></script>
    <script>
        let validation = () => {
            if ($("#username").val() === "") {
                alert("아이디를 입력해주세요.");
                return false;
            } else if ($("#password").val() === "") {
                alert("비밀번호를 입력해주세요.");
                return false;
            }
            return true;
        };
        $("#username, #password, #login").on("keyup", e => {
            if (e.keyCode === 13 && validation())
                user.login(
                    "${_csrf.parameterName}", "${_csrf.token}",
                    $("#username").val(), $("#password").val());
        });
        $("#login").on("click", () =>  {
            if (validation())
                user.login(
                    "${_csrf.parameterName}", "${_csrf.token}",
                    $("#username").val(), $("#password").val());
        });
        $("#back").on("click", () => location.replace("/"));
        $("#guest").on("click", () => location.replace("/main"));
    </script>
</body>
</html>
