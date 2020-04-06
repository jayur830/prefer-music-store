<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%request.setAttribute("status", response.getStatus());%>
<html>
<head>
    <title>Error ${status}</title>
</head>
<body>
    <h1>
        Error ${status}:&nbsp;
        <c:choose>
            <c:when test="${status == 403}">접근이 불가능합니다.</c:when>
            <c:when test="${status == 404}">페이지를 찾을 수 없습니다.</c:when>
            <c:when test="${status == 500}">내부 서버 오류</c:when>
        </c:choose>
    </h1>
</body>
</html>
