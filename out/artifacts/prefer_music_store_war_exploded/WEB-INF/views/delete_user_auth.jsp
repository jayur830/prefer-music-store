<%@ page import="org.springframework.security.core.Authentication" %>
<%@ page import="org.springframework.security.core.context.SecurityContextHolder" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ include file="/WEB-INF/include/header.jsp" %>
<%
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = "";
    if (auth.getPrincipal() != null) username = auth.getName();
%>
<html>
<head>
    <title>Title</title>
    <link rel="stylesheet" href="<c:url value="/resources/css/center_form.css" />" />
    <style>
        #auth_failed {
            font-weight: bold;
            font-size: 14pt;
            color: red;
            caption-side: bottom;
            text-align: center;
        }

        input[type=button] {
            padding: 5px 20px;
        }

        input {
            font-size: 14pt;
        }

        label.placeholder {
            font-size: 14pt;
        }
    </style>
</head>
<body>
    <div id="parent_form">
        <table id="child_form">
            <caption id="auth_failed"></caption>
            <tr>
                <td colspan="2">
                    <input type="password" id="password" required />
                    <label for="password" class="placeholder">비밀번호</label>
                </td>
            </tr>
            <tr>
                <td style="text-align: center;">
                    <input id="delete_user" type="button" value="회원탈퇴" class="btn_light_dark" />
                </td>
                <td style="text-align: center;">
                    <input id="cancel" type="button" value="돌아가기" class="btn_light_dark" />
                </td>
            </tr>
        </table>
    </div>
    <script src="<c:url value="/resources/js/user.js"/>"></script>
    <script>
        const deleteUser = () => {
            if ($("#password").val() === "")
                alert("비밀번호를 입력해주세요.");
            else user.deleteUser("${_csrf.parameterName}", "${_csrf.token}", "<%=username%>", $("#password").val());
        };

        $("#password, #delete_user").on("keyup", e => {
            if (e.keyCode === 13) deleteUser();
        });
        $("#delete_user").on("click", deleteUser);
        $("#cancel").on("click", () => location.replace("/user_info"));
    </script>
</body>
</html>
