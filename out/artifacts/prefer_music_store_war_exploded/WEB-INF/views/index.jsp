<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ include file="/WEB-INF/include/header.jsp" %>
<html>
<head>
    <title>Prefer Music Store</title>
    <link rel="stylesheet" href="<c:url value="/resources/css/center_form.css" />" />
    <style>
        td {
            text-align: center;
        }

        input {
            font-size: 16pt;
            margin: 10px 0;
            height: 60px;
            width: 200px;
        }
    </style>
</head>
<body>
    <div id="parent_form">
        <table id="child_form">
            <tr>
                <td><input id="sign_up" type="button" class="btn_dark_light" value="회원가입" /></td>
            </tr>
            <tr>
                <td><input id="login" type="button" class="btn_dark_light" value="로그인" /></td>
            </tr>
        </table>
    </div>
    <script>
        $("#sign_up").on("click", () => location.replace("/sign_up"));
        $("#login").on("click", () => location.replace("/login"));
    </script>
</body>
</html>